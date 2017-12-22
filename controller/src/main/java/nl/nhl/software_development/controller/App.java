package nl.nhl.software_development.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

import com.google.gson.FieldNamingPolicy;
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

import nl.nhl.software_development.controller.Time.TimeAck;
import nl.nhl.software_development.controller.crossing.Crossing;
import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.StateDeserializer;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.StateSerializer;
import nl.nhl.software_development.controller.net.TrafficUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequest;
import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequestDeserializer;
import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequestSerializer;
import nl.nhl.software_development.controller.net.TrafficUpdateWrapper;

public class App implements Runnable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			.registerTypeAdapter(State.class, new StateDeserializer()).registerTypeAdapter(State.class, new StateSerializer())
			.registerTypeAdapter(DirectionRequest.class, new DirectionRequestDeserializer())
			.registerTypeAdapter(DirectionRequest.class, new DirectionRequestSerializer());
	public static final String SIMULATOR_QUEUE_NAME = "simulator";
	public static final String COMMANDQUEUE_NAME = "controller";
	public static final Random R = new Random();

	private static ScheduledExecutorService executor;
	private static App p;
	private static ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection = null;
	private static boolean hasReceivedMessage = false;

	private Gson gson;
	private Crossing crossing;
	private Channel channel;
	private String lastCorrelationId;
	private CrossingUpdate lastUpdate;

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
		lastUpdate = new CrossingUpdate();
		channel.basicQos(1);
		Map<String, Object> args = new HashMap<>(1);
		args.put("x-message-ttl", 10000);
		channel.queueDeclare(COMMANDQUEUE_NAME, false, false, true, args);
		channel.queueDeclare(SIMULATOR_QUEUE_NAME, false, false, true, args);
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException
			{
				if (!hasReceivedMessage)
				{
					hasReceivedMessage = true;
					synchronized (p)
					{
						p.notifyAll();
					}
				}
				try
				{
					if (properties.getCorrelationId() != null)
					{
						lastCorrelationId = properties.getCorrelationId();
					}
					String message = new String(body, CHARSET);
					TrafficUpdateWrapper trafficUpdate = gson.fromJson(message, TrafficUpdateWrapper.class);
					if (message.contains("Speed"))
					{
						LOGGER.debug(message);
						if (trafficUpdate.getTimescale() != null)
						{
							LOGGER.debug("Timescale = ".concat(trafficUpdate.getTimescale().toString()));
						}
						LOGGER.debug("Got speed update");
					}
					int updateHash = trafficUpdate.getUpdateHash();
					if (updateHash == TrafficUpdate.class.hashCode())
					{
						crossing.handleUpdate(trafficUpdate);
					}
					else if (updateHash == Double.class.hashCode())
					{
						LOGGER.debug("Got timescale update message");
						double timescale = trafficUpdate.getTimescale();
						if (timescale >= 0)
							Time.setTimeScale(timescale);
						LOGGER.debug(gson.toJson(Time.getTimeScaleAck(), TimeAck.class));
						channel.basicPublish("", SIMULATOR_QUEUE_NAME, new Builder().correlationId(lastCorrelationId).build(),
								gson.toJson(Time.getTimeScaleAck(), TimeAck.class).getBytes(CHARSET));
					}
				}
				catch (JsonSyntaxException ex)
				{
					LOGGER.error("Message is not a TrafficUpdate");
					LOGGER.debug("Got: ".concat(new String(body, CHARSET)));
				}
				catch (Exception ex)
				{
					LOGGER.error("Encountered error:", ex);
				}
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		channel.basicConsume(COMMANDQUEUE_NAME, false, consumer);
	}

	@Override
	public void run()
	{
		try
		{
			Time.updateTime();
			Crossing.preUpdate();
			crossing.update();
			Builder propertiesBuilder = new Builder();
			if (!lastCorrelationId.isEmpty())
				propertiesBuilder.correlationId(lastCorrelationId);
			CrossingUpdate crossingUpdate = crossing.serialize();
			if (!lastUpdate.equals(crossingUpdate))
			{
				try
				{
					LOGGER.debug(String.format("Sending: %s", gson.toJson(crossingUpdate, CrossingUpdate.class)));
					channel.basicPublish("", SIMULATOR_QUEUE_NAME, propertiesBuilder.build(),
							gson.toJson(crossingUpdate, CrossingUpdate.class).getBytes(CHARSET));
				}
				catch (IOException ex)
				{
					LOGGER.error("Failed to send message", ex);
				}
				catch (Exception ex)
				{
					LOGGER.error("Failed to do stuff", ex);
				}
				lastUpdate = crossingUpdate;
				Time.watchDog();
			}
		}
		catch (Exception ex)
		{
			LOGGER.warn("Failed to update", ex);
		}
	}

	public static void main(String[] args)
	{
		Options options = new Options();
		options.addOption(Option.builder("h").longOpt("host").hasArg().argName("remote host").desc("Set remote host").build());
		options.addOption(Option.builder("v").longOpt("vhost").hasArg().argName("virtual host").desc("Set vhost to use").build());
		options.addOption(Option.builder("u").longOpt("user").hasArg().argName("username").desc("User for login").build());
		options.addOption(Option.builder("p").longOpt("password").hasArg().argName("password").desc("Password to use when connecting to server").build());
		options.addOption(Option.builder("?").longOpt("help").desc("Print help message").build());

		// ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		// root.setLevel(ch.qos.logback.classic.Level.TRACE);

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
			LOGGER.info("Started ".concat("Program"));
			synchronized (p)
			{
				while (!hasReceivedMessage)
				{
					LOGGER.info("Started waiting for messages");
					p.wait();
				}
				LOGGER.info("Message received, starting logicloop");
			}
			executor.scheduleAtFixedRate(p, 0, 750, TimeUnit.MILLISECONDS);
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
	}
}
