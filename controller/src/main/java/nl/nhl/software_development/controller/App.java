package nl.nhl.software_development.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import nl.nhl.software_development.controller.crossing.Crossing;
import nl.nhl.software_development.controller.net.TrafficUpdate;

public class App implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
	public static final String SIMULATOR_QUEUE_NAME = "simulator";
	public static final String COMMANDQUEUE_NAME = "controller";

	private static ScheduledExecutorService executor;
	private static App p;
	private static ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection = null;

	private Gson gson;
	private Crossing crossing;
	private Channel channel;
	private String lastCorrelationId;

	public static App instance()
	{
		return p;
	}

	public static Connection brokerConnection()
	{
		return connection;
	}

	public static final GsonBuilder gsonBuilder()
	{
		return gsonBuilder;
	}

	public App() throws IOException
	{
		gson = gsonBuilder.create();
		crossing = new Crossing();
		channel = connection.createChannel();
		lastCorrelationId = "";
		channel.basicQos(1);
		Map<String, Object> args = new HashMap<>(1);
		args.put("x-message-ttl", 10000);
		channel.queueDeclare(COMMANDQUEUE_NAME, false, false, true, args);
		channel.queueDeclare(SIMULATOR_QUEUE_NAME, false, false, true, null);
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException
			{
				try
				{
					if (properties.getCorrelationId() != null)
					{
						lastCorrelationId = properties.getCorrelationId();
					}
					TrafficUpdate trafficUpdate = gson.fromJson(new String(body, CHARSET), TrafficUpdate.class);
					crossing.handleUpdate(trafficUpdate);
				}
				catch (JsonSyntaxException ex)
				{
					LOGGER.error("Message is not a TrafficUpdate");
					LOGGER.debug("Got: ".concat(new String(body, CHARSET)));
				}
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		channel.basicConsume(COMMANDQUEUE_NAME, false, consumer);
	}

	@Override
	public void run()
	{
		Time.updateTime();
		Crossing.preUpdate();
		crossing.update();
		Builder propertiesBuilder = new Builder();
		if (!lastCorrelationId.isEmpty())
			propertiesBuilder.correlationId(lastCorrelationId);
		try
		{
			channel.basicPublish("", SIMULATOR_QUEUE_NAME, propertiesBuilder.build(),
					gson.toJson(crossing.serialize()).getBytes(CHARSET));
		}
		catch (IOException ex)
		{
			LOGGER.error("Failed to send message", ex);
		}
	}

	public static void main(String[] args)
	{
		Options options = new Options();
		options.addOption(
				Option.builder("h").longOpt("host").hasArg().argName("remote host").desc("Set remote host").build());
		options.addOption(
				Option.builder("v").longOpt("vhost").hasArg().argName("virtual host").desc("Set vhost to use").build());
		options.addOption(
				Option.builder("u").longOpt("user").hasArg().argName("username").desc("User for login").build());
		options.addOption(Option.builder("p").longOpt("password").hasArg().argName("password")
				.desc("Password to use when connecting to server").build());
		options.addOption(Option.builder("?").longOpt("help").desc("Print help message").build());

		CommandLineParser parser = new DefaultParser();
		executor = Executors.newSingleThreadScheduledExecutor();
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
			executor.scheduleAtFixedRate(p, 100, 16, TimeUnit.MILLISECONDS);
		}
		catch (ParseException ex)
		{
			System.out.println(ex.getLocalizedMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Program", options, true);
			System.exit(0);
		}
		catch (Exception ex)
		{
			LOGGER.error("Failed to start Program", ex);
			System.exit(1);
		}
		LOGGER.info("Started ".concat("Program"));
	}
}
