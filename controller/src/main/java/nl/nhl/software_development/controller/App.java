package nl.nhl.software_development.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import nl.nhl.software_development.controller.crossing.Crossing;
import nl.nhl.software_development.controller.net.Hello;
import nl.nhl.software_development.controller.net.TrafficUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class App
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	//private static final String CORRELATIONID = UUID.randomUUID().toString();
	private static final GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
	private static final String SIMULATOR_QUEUE_NAME = "simulator";
	private static final String COMMANDQUEUE_NAME = "controller";
	private static App p;
	private static ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection = null;

	private Gson gson;
	private Crossing crossing;
	private Channel channel;

	public static App instance()
	{
		return p;
	}

	public static GsonBuilder gsonBuilder() { return gsonBuilder; }

	public App() throws IOException
	{
		gson = gsonBuilder.create();
		crossing = new Crossing();
		channel = connection.createChannel();
		channel.queueDeclare(COMMANDQUEUE_NAME, false, false, false, null);
		channel.basicQos(1);
		Consumer consumer = new DefaultConsumer(channel)
		{
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				byte[] reply = null;
				try {
					TrafficUpdate trafficUpdate = gson.fromJson(new String(body, CHARSET), TrafficUpdate.class);
					crossing.handleUpdate(trafficUpdate);
					reply = gson.toJson(new nl.nhl.software_development.controller.net.Crossing()).getBytes(CHARSET);
				}
				catch (JsonSyntaxException e) {
					LOGGER.error("Message is not a TrafficUpdate");
					try {
						Hello hello = gson.fromJson(new String(body, CHARSET), Hello.class);
						reply = gson.toJson(new Hello("Hello from group 6")).getBytes(CHARSET);
						LOGGER.info(hello.getMessage());
					} catch (JsonSyntaxException ex) {
						LOGGER.warn("Message not a Hello");
					}
				}
				String correlationId = properties.getCorrelationId();
				String replyTo = properties.getReplyTo();
				BasicProperties.Builder propertiesBuilder = new BasicProperties.Builder();
				if (!correlationId.isEmpty()) propertiesBuilder.correlationId(correlationId);
				if (replyTo.isEmpty()) replyTo = SIMULATOR_QUEUE_NAME;
				channel.basicPublish("", replyTo, propertiesBuilder.build(), reply);
			}
		};
		channel.basicConsume(COMMANDQUEUE_NAME, false, consumer);
	}

	public static void main(String[] args)
	{
		factory.setHost("localhost");
		factory.setVirtualHost("/6");
		factory.setUsername("softdev");
		factory.setPassword("softdev");
		LOGGER.info("Started ".concat("Program"));
		try
		{
			connection = factory.newConnection();
			p = new App();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}/*
		Hello hello = new Hello();
		try
		{
			Channel testChannel = connection.createChannel();
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(CORRELATIONID)
					.replyTo("controller.requests").build();
			testChannel.basicPublish("", SIMULATOR_QUEUE_NAME, properties, message.toByteArray());
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}*/
	}
}
