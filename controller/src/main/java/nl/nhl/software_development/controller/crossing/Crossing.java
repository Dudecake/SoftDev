package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.App;
import nl.nhl.software_development.controller.Time;
import nl.nhl.software_development.controller.crossing.TrafficLight.InverseWeightComparator;
import nl.nhl.software_development.controller.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller.crossing.TrafficLight.Status;
import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdateWrapper;

public class Crossing
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Crossing.class);
	private static final InverseWeightComparator WEIGHT_COMPARATOR = new InverseWeightComparator();
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
		Location location = Location.NORTH;
		for (int i = 101; i < 111; i++)
		{
			List<Location> destinations = null;
			switch (i)
			{
			case 110:
				destinations = Arrays.asList(Location.NORTH);
				break;
			case 102:
				destinations = Arrays.asList(Location.SOUTH);
				break;
			case 103:
			case 109:
				destinations = Arrays.asList(Location.EAST);
				break;
			case 104:
				location = Location.EAST;
				destinations = Arrays.asList(Location.NORTH);
				break;
			case 101:
			case 105:
				destinations = Arrays.asList(Location.WEST);
				break;
			case 106:
				location = Location.SOUTH;
				destinations = Arrays.asList(Location.EAST);
				break;
			case 107:
				destinations = Arrays.asList(Location.WEST);
				break;
			case 108:
				location = Location.WEST;
				destinations = Arrays.asList(Location.SOUTH);
				break;
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
			location = Location.WEST;
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
		location = Location.WEST;
		for (int i = 401; i < 407; i++)
		{
			switch (i)
			{
			case 402:
			case 403:
				location = Location.NORTH;
				break;
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
		TrafficLight.fillInterferingLights(this.lights);
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
						handleUpdate(new TrafficUpdateWrapper(randomLightA, 1, null, 1.0));
						handleUpdate(new TrafficUpdateWrapper(randomLightB, 2, null, 1.0));
						Thread.sleep(2500);
						if (Crossing.this.lights.getId(randomLightA).getStatus() == Status.GREEN)
							handleUpdate(new TrafficUpdateWrapper(randomLightA, 0, null, 1.0));
						if (Crossing.this.lights.getId(randomLightB).getStatus() == Status.GREEN)
							handleUpdate(new TrafficUpdateWrapper(randomLightB, 0, null, 1.0));
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
			TrafficLight workLight = workLights.get(0);
			// if (workLight.getWeight() == 0)
			// {
			// System.out.println("Weight is 0");
			// }
			if (workLight.getWeight() == Short.MAX_VALUE && workLight.getStatus() != Status.GREEN)
			{
				for (int i = 1; i < workLights.size();)
				{
					if (workLights.get(i).interferesWith(workLight))
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
				{
					if (workLight.getWeight() == 0 || workLight.getStatus() == Status.ORANGE)
					{
						if (workLight.setStatus(Status.RED) == Status.RED)
						{
							workLights.remove(i);
							i--;
							continue;
						}
					}
					greenLights.add(workLight);
				}
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

	public void handleUpdate(TrafficUpdateWrapper trafficUpdate)
	{
		synchronized (lock)
		{
			try
			{
				TrafficLight light = lights.getId(trafficUpdate.getLightId());
				light.setQueueLength(trafficUpdate.getCount());
				if (BusTrafficLight.class.isInstance(light))
				{
					BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(light);
					busTrafficLight.setDirectionRequests(trafficUpdate.getDirectionRequests());
				}
			}
			catch (NullPointerException ex)
			{
				LOGGER.error("Failed to handle TrafficLightUpdate", ex);
			}
		}
	}
}
