package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.Time;
import nl.nhl.software_development.controller.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller.crossing.TrafficLight.Status;
import nl.nhl.software_development.controller.crossing.TrafficLight.WeightComparator;
import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdate;

public class Crossing
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Crossing.class);
	private static final WeightComparator WEIGHT_COMPARATOR = new WeightComparator();
	static Duration updateTime;
	private final TrafficLightList lights;
	private TrafficLightList workLights;
	private Object lock;

	public Crossing()
	{
		TrafficLightList lights = new TrafficLightList();
		workLights = new TrafficLightList();
		lock = new Object();
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
		lights.sort(null);
		this.lights = lights;
	}

	public CrossingUpdate serialize()
	{
		return new CrossingUpdate(lights.parallelStream().map(TrafficLight::serialize).collect(Collectors.toList()),
				1.0);
	}

	public static void preUpdate()
	{
		updateTime = Time.getTime();
	}

	static void preUpdate(Duration time)
	{
		updateTime = time;
	}

	public void update()
	{
		synchronized (lock)
		{
			workLights.clear();
			workLights.addAll(lights);
			workLights.sort(WEIGHT_COMPARATOR);
			if (workLights.get(0).getWeight() == Short.MAX_VALUE && workLights.get(0).getStatus() != Status.GREEN)
			{
				TrainTrafficLight trainTrafficLight = TrainTrafficLight.class.cast(workLights.get(0));
				for (int i = 1; i < workLights.size();)
				{
					if (workLights.get(i).interferesWith(trainTrafficLight))
					{
						workLights.get(i).setStatus(Status.RED);
						workLights.remove(i);
					}
					else
					{
						i++;
					}
				}
			}
			TrafficLightList greenLights = new TrafficLightList();
			TrafficLightList interferingLights = new TrafficLightList();
			TrafficLight workLight;
			lightTrimLoop: for (int i = 1; i < workLights.size(); i++)
			{
				workLight = workLights.get(i);
				for (TrafficLight t : greenLights)
				{
					if (workLight.interferesWith(t))
					{
						interferingLights.add(t);
						// if (workLight.compareTo(t) < 0)
						// {
						//
						// }
						continue lightTrimLoop;
					}
				}
				if (workLight.getStatus() != Status.RED)
					greenLights.add(workLight);
			}
			if (greenLights.isEmpty())
			{
				for (int i = 0; i < workLights.size();)
				{
					workLight = workLights.get(i);
					if (!workLight.interferesWith(greenLights))
					{
						workLight.setStatus(Status.GREEN);
						greenLights.add(workLight);
						workLights.remove(i);
					}
					else
					{
						i++;
					}
				}
			}
			// TODO: Add logic
		}
	}

	public void handleUpdate(TrafficUpdate trafficUpdate)
	{
		synchronized (lock)
		{
			TrafficLight light = lights.getId(trafficUpdate.getLightId());
			light.setQueueLength(trafficUpdate.getCount());
			if (BusTrafficLight.class.isInstance(light))
			{
				BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(light);
				busTrafficLight.setDirectionRequests(trafficUpdate.getDirectionRequests());
			}
		}
	}
}
