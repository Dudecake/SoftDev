package nl.nhl.software_development.controller.crossing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

import nl.nhl.software_development.controller.App;
import nl.nhl.software_development.controller.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller.crossing.TrafficLight.State;
import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdate;

public class Crossing
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Crossing.class);
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final String SIMULATOR_QUEUE_NAME = "simulator";

	private final List<TrafficLight> lights;
	private Gson gson;
	private Channel channel;

	public Crossing() throws IOException
	{
		gson = App.gsonBuilder().create();
		channel = App.brokerConnection().createChannel();
		List<TrafficLight> lights = new ArrayList<>();
		// Add car traffic lights
		for (int i = 101; i < 111; i++)
		{
			Location location = Location.NORTH;
			List<Location> destinations = null;
			switch (i)
			{
			case 101:
				destinations = Arrays.asList(Location.NORTH);
				break;
			case 102:
				destinations = Arrays.asList(Location.SOUTH);
			case 103:
			case 109:
				destinations = Arrays.asList(Location.EAST);
			case 104:
				location = Location.EAST;
				destinations = Arrays.asList(Location.NORTH);
			case 105:
				destinations = Arrays.asList(Location.WEST);
			case 106:
				location = Location.SOUTH;
				destinations = Arrays.asList(Location.EAST);
			case 107:
				destinations = Arrays.asList(Location.WEST);
			case 108:
				location = Location.WEST;
				destinations = Arrays.asList(Location.SOUTH);
			case 110:
				destinations = Arrays.asList(Location.NORTH);
			default:
				break;
			}
			lights.add(new CarTrafficLight(i, State.RED, location, destinations));
		}
		// Add bike traffic lights
		for (int i = 301; i < 306; i++)
		{
			Location location = Location.WEST;
			switch (i)
			{
			case 302:
			case 303:
				location = Location.NORTH;
				break;
			case 304:
				location = Location.EAST;
			default:
				break;
			}
			lights.add(new BikeTrafficLight(i, State.RED, location));
		}
		// Add pedestrian traffic lights
		for (int i = 401; i < 407; i++)
		{
			Location location = Location.WEST;
			switch (i)
			{
			case 402:
			case 403:
				location = Location.NORTH;
			case 404:
				location = Location.EAST;
				break;

			default:
				break;
			}
			lights.add(new BikeTrafficLight(i, State.RED, location));
		}
		// Add train traffic light
		lights.add(new TrainTrafficLight(501, State.RED, Location.SOUTH));
		this.lights = lights;
	}

	private CrossingUpdate serialize()
	{
		List<TrafficLightUpdate> lightUpdates = new ArrayList<>();
		for (TrafficLight t : lights)
		{
			lightUpdates.add(t.serialize());
		}
		return new CrossingUpdate(lightUpdates, 1.0);
	}

	public void update() throws IOException
	{
		TrafficLight.preUpdate();
		// TODO: Add logic
		CrossingUpdate update = this.serialize();
		channel.basicPublish("", SIMULATOR_QUEUE_NAME, null, gson.toJson(update).getBytes(CHARSET));
	}

	public void handleUpdate(TrafficUpdate trafficUpdate)
	{
		// TODO: Collect updates for processing
	}
}
