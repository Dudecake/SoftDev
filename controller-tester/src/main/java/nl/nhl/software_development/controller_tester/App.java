package nl.nhl.software_development.controller_tester;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import nl.nhl.software_development.controller_tester.crossing.Crossing;
import nl.nhl.software_development.controller_tester.crossing.TrafficLight.Status;
import nl.nhl.software_development.controller_tester.net.CrossingUpdate;
import nl.nhl.software_development.controller_tester.net.TrafficLightUpdate;
import nl.nhl.software_development.controller_tester.net.TrafficUpdate.DirectionRequest;
import nl.nhl.software_development.controller_tester.net.TrafficUpdate.DirectionRequestDeserializer;
import nl.nhl.software_development.controller_tester.net.TrafficUpdate.DirectionRequestSerializer;
import nl.nhl.software_development.controller_tester.net.TrafficUpdateWrapper;

public class App
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			.registerTypeAdapter(DirectionRequest.class, new DirectionRequestDeserializer())
			.registerTypeAdapter(DirectionRequest.class, new DirectionRequestSerializer());
	public static final String SIMULATOR_QUEUE_NAME = "simulator";
	public static final String COMMANDQUEUE_NAME = "controller";
	public static final Random R = new Random();

	private static App p;
	private static ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection = null;

	private Gson gson;
	private Crossing crossing;
	private Channel channel;
	private String correlationId;

	public static class TimeAck
	{
		@SuppressWarnings("unused")
		private Double speed;

		public TimeAck(Double speed)
		{
			this.speed = speed;
		}
	}

	public static App instance()
	{
		return p;
	}

	public App() throws IOException
	{
		gson = gsonBuilder.create();
		crossing = new Crossing();
		channel = connection.createChannel();
		channel.basicQos(1);
		Map<String, Object> args = new HashMap<>(1);
		args.put("x-message-ttl", 10000);
		channel.queueDeclare(COMMANDQUEUE_NAME, false, false, true, args);
		channel.queueDeclare(SIMULATOR_QUEUE_NAME, false, false, true, args);
		correlationId = UUID.randomUUID().toString();
		BasicProperties properties = new Builder().correlationId(correlationId).build();

		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException
			{
				String message = new String(body, CHARSET);
				if (!message.contains("Speed"))
				{
					CrossingUpdate crossingUpdate = gson.fromJson(message, CrossingUpdate.class);
					for (TrafficLightUpdate u : crossingUpdate.getLights())
					{
						crossing.getLightById(u.getId()).setStatus(Status.valueOf(u.getState().asInt()));
					}
				}
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		channel.basicConsume(SIMULATOR_QUEUE_NAME, false, consumer);

		channel.basicPublish("", COMMANDQUEUE_NAME, properties, "{Speed:5.0}".getBytes(CHARSET));

		Thread testThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					int randomLightA = 101;
					int randomLightB = 107;
					for (int i = 0; i < 15; i++)
					{
						channel.basicPublish("", COMMANDQUEUE_NAME, properties,
								gson.toJson(new TrafficUpdateWrapper(randomLightA, 1, null), TrafficUpdateWrapper.class).getBytes(CHARSET));
						channel.basicPublish("", COMMANDQUEUE_NAME, properties,
								gson.toJson(new TrafficUpdateWrapper(randomLightB, 2, null), TrafficUpdateWrapper.class).getBytes(CHARSET));
						Thread.sleep(2500);
						if (App.this.crossing.getLightById(randomLightA).getStatus() == Status.GREEN)
							channel.basicPublish("", COMMANDQUEUE_NAME, properties,
									gson.toJson(new TrafficUpdateWrapper(randomLightA, 0, null), TrafficUpdateWrapper.class).getBytes(CHARSET));
						if (App.this.crossing.getLightById(randomLightB).getStatus() == Status.GREEN)
							channel.basicPublish("", COMMANDQUEUE_NAME, properties,
									gson.toJson(new TrafficUpdateWrapper(randomLightB, 0, null), TrafficUpdateWrapper.class).getBytes(CHARSET));
						randomLightA = App.R.nextInt(10) + 101;
						randomLightB = App.R.nextInt(10) + 101;
					}
				}
				catch (Exception ex)
				{
					LOGGER.error("Error in testingThread", ex);
				}
			}
		};
		testThread.setDaemon(true);
		testThread.setName("testthread");
		testThread.start();
	}

	public static void main(String[] args)
	{
		Options options = new Options();
		options.addOption(Option.builder("h").longOpt("host").hasArg().argName("remote host").desc("Set remote host").build());
		options.addOption(Option.builder("v").longOpt("vhost").hasArg().argName("virtual host").desc("Set vhost to use").build());
		options.addOption(Option.builder("u").longOpt("user").hasArg().argName("username").desc("User for login").build());
		options.addOption(Option.builder("p").longOpt("password").hasArg().argName("password").desc("Password to use when connecting to server").build());
		options.addOption(Option.builder("?").longOpt("help").desc("Print help message").build());

		CommandLineParser parser = new DefaultParser();
		try
		{
			CommandLine line = parser.parse(options, args);
			if (line.hasOption('?'))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Program", options, true);
				System.exit(0);
			}
			factory.setHost(line.getOptionValue('h', "localhost"));
			factory.setVirtualHost(line.getOptionValue('v', "/6"));
			factory.setUsername(line.getOptionValue('u', "softdev"));
			factory.setPassword(line.getOptionValue('p', "softdev"));
			connection = factory.newConnection();
			p = new App();
		}
		catch (ParseException ex)
		{
			System.out.println(ex.getLocalizedMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("TestProgram", options, true);
			System.exit(0);
		}
		catch (Exception ex)
		{
			LOGGER.error("Failed to start TestProgram", ex);
			System.exit(1);
		}
		LOGGER.info("Started ".concat("TestProgram"));
	}
}
