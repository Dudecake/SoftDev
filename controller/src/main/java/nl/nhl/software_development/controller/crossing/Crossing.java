package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.Time;
import nl.nhl.software_development.controller.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller.crossing.TrafficLight.Status;
import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdate;

public class Crossing
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Crossing.class);
	static Duration updateTime;
	private final List<TrafficLight> lights;

	public Crossing()
	{
		List<TrafficLight> lights = new ArrayList<>();
		// Add car traffic lights
		for (int i = 101; i < 111; i++)
		{
			Location location = Location.NORTH;
			List<Location> destinations = null;
			switch (i)
			{
			case 101:
			case 110:
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
			default:
				break;
			}
			lights.add(new CarTrafficLight(i, Status.RED, location, destinations));
		}
		// Add bus traffic light
		lights.add(new BusTrafficLight(201, Status.RED, Location.EAST, Arrays.asList(Location.WEST, Location.NORTH)));
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
			lights.add(new BikeTrafficLight(i, Status.RED, location));
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
			lights.add(new BikeTrafficLight(i, Status.RED, location));
		}
		// Add train traffic light
		lights.add(new TrainTrafficLight(501, Status.RED, Location.SOUTH));
		this.lights = lights;
	}

	public CrossingUpdate serialize()
	{
		List<TrafficLightUpdate> lightUpdates = new ArrayList<>();
		for (TrafficLight t : lights)
		{
			lightUpdates.add(t.serialize());
		}
		return new CrossingUpdate(lightUpdates, 1.0);
	}

	public static void preUpdate()
	{
		updateTime = Time.getTime();
	}

	public void update()
	{
		// TODO: Add logic
	}

	private TrafficLight findTrafficLightById(int lightId)
	{
		TrafficLight res = null;
		for (TrafficLight t : lights)
		{
			if (t.id == lightId)
			{
				res = t;
				break;
			}
		}
		return res;
	}

	public void handleUpdate(TrafficUpdate trafficUpdate)
	{
		TrafficLight light = findTrafficLightById(trafficUpdate.getLightId());
		light.setQueueLength(trafficUpdate.getCount());
		if (BusTrafficLight.class.isInstance(light))
		{
			BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(light);
			// busTrafficLight.set
		}
	}
}
