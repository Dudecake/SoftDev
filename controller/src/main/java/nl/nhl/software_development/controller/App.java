package nl.nhl.software_development.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

	static
	{
		factory.setHost("localhost");
		factory.setVirtualHost("/6");
		factory.setUsername("softdev");
		factory.setPassword("softdev");
	}

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
		args.put("x-message-ttl", 5000);
		channel.queueDeclare(COMMANDQUEUE_NAME, false, false, true, args);
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException
			{
				try
				{
					TrafficUpdate trafficUpdate = gson.fromJson(new String(body, CHARSET), TrafficUpdate.class);
					crossing.handleUpdate(trafficUpdate);
				}
				catch (JsonSyntaxException e)
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
			channel.basicPublish("", SIMULATOR_QUEUE_NAME, propertiesBuilder.build(), gson.toJson(crossing.serialize()).getBytes(CHARSET));
		}
		catch (IOException ex)
		{
			LOGGER.error("Failed to send message", ex);
		}
	}

	public static void main(String[] args)
	{
		executor = Executors.newSingleThreadScheduledExecutor();
		try
		{
			connection = factory.newConnection();
			p = new App();
			executor.scheduleAtFixedRate(p, 100, 16, TimeUnit.MILLISECONDS);
		}
		catch (Exception ex)
		{
			LOGGER.error("Failed to start Program", ex);
			System.exit(1);
		}
		LOGGER.info("Started ".concat("Program"));
	}
}
