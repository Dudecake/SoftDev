package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final TrafficLightList compoundLights;
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
				destinations = Arrays.asList(Location.EAST, Location.NORTH);
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

		for (int i = 401; i < 413; i++)
		{
			location = Location.WEST;
			switch (i)
			{
			case 402:
			case 403:
			case 407:
			case 408:
				location = Location.NORTH;
				break;
			case 404:
			case 409:
			case 410:
				location = Location.EAST;
				break;
			default:
				break;
			}
			lights.add(new BikeTrafficLight(i, Status.RED, location));
		}
		// Add train traffic light
		TrainCrossingLight trainLight = new TrainCrossingLight(601, Status.RED, Location.SOUTH);
		lights.add(trainLight);
		lights.add(new TrainTrafficLight(501, Status.RED, Location.SOUTH, trainLight));
		lights.add(new TrainTrafficLight(502, Status.RED, Location.SOUTH, trainLight));
		lights.sort(null);
		this.lights = lights;
		this.compoundLights = new TrafficLightList();
		TrafficLight.fillInterferingLights(this.lights);
	}

	public CrossingUpdate serialize()
	{
		return new CrossingUpdate(lights.parallelStream().map(TrafficLight::serialize).filter(l -> l != null).collect(Collectors.toList()), 1.0);
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
			workLights.addAll(compoundLights);
			workLights.sort(WEIGHT_COMPARATOR);
			List<TrainTrafficLight> trainLights = workLights.getAllTrainLights();
			// if (workLight.getWeight() == 0)
			// {
			// System.out.println("Weight is 0");
			// }
			if (Time.needsFullReset())
				lights.watchDogReset();
			int trainFree = 0;
			workLights.removeAll(trainLights);
			workLights.remove(workLights.getId(601));
			for (TrainTrafficLight light : trainLights)
			{
				if (light.getQueueLength() != 0)
				{
					LOGGER.debug(String.format("Train arrived at: %d", light.getId()));
					if (light.getStatus() != Status.GREEN)
					{
						for (int i = 0; i < workLights.size();)
						{
							if (workLights.get(i).interferesWith(light))
							{
								workLights.get(i).setStatus(Status.RED);
								LOGGER.trace(String.format("Removed id %d from workLights (reason: train): queue length of %d", workLights.get(i).getId(),
										workLights.get(i).getQueueLength()));
								if (workLights.get(i).getStatus() != Status.GREEN)
									trainFree++;
								workLights.remove(i);

							}
							else
							{
								i++;
							}
						}
					}
				}
			}

			TrafficLight workLight = workLights.get(0);
			// if (workLight.getWeight() == Short.MAX_VALUE &&
			// workLight.getStatus() != Status.GREEN)
			// {
			// for (int i = 0; i < workLights.size();)
			// {
			// if (workLights.get(i).interferesWith(workLight))
			// {
			// workLights.get(i).setStatus(Status.RED);
			// workLights.remove(i);
			// }
			// else
			// {
			// i++;
			// }
			// }
			// }
			TrafficLightList greenLights = new TrafficLightList();
			TrafficLightList interferingLights = new TrafficLightList();
			lightTrimLoop: for (int i = 0; i < workLights.size(); i++)
			{
				workLight = workLights.get(i);
				for (TrafficLight t : greenLights)
				{
					if (workLight.interferesWith(t))
					{
						interferingLights.add(t);
						workLights.remove(i);
						i--;
						// LOGGER.trace(String.format("Added id %d to
						// interferinglights: queue length of %d vs %d",
						// t.getId(), t.getQueueLength(),
						// workLight.getQueueLength()));
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
							LOGGER.trace(String.format("Removed id %d from workLights (reason: weight): queue length of %d", workLights.get(i).getId(),
									workLight.getQueueLength()));
							workLights.remove(i);
							i--;
							continue;
						}
						else
						{
							LOGGER.trace(String.format("Tried setting %d to red", workLight.getId()));
						}
					}
					LOGGER.trace(String.format("Added id %d to greenLights: queue length of %d", workLight.getId(), workLight.getQueueLength()));
					greenLights.add(workLight);
				}
			}
			if (greenLights.isEmpty())
			{
				LOGGER.trace("greenlights is empty");
				for (int i = 0; i < workLights.size();)
				{
					workLight = workLights.get(i);
					if (!workLight.interferesWith(greenLights))
					{
						if (workLight.setStatus(Status.GREEN) != Status.RED)
						{
							greenLights.add(workLight);
							LOGGER.trace(String.format("Added id %d to greenLights: queue length of %d", workLight.getId(), workLight.getQueueLength()));
							workLights.remove(i);
						}
						else
						{
							i++;
						}
					}
					else
					{
						i++;
					}
				}
			}
			LOGGER.debug(String.format("workLights contains %d lights", workLights.size()));
			if (lights.getId(501).getQueueLength() == 0 && lights.getId(502).getQueueLength() == 0)
			{
				lights.getId(501).setStatus(Status.RED);
				lights.getId(601).setStatus(Status.GREEN);
			}
			if (trainFree != 0)
			{
				LOGGER.debug("Train is free");
				if (lights.getId(501).getQueueLength() != 0 && lights.getId(502).getQueueLength() == 0)
					lights.getId(501).setStatus(Status.GREEN);
				lights.getId(601).setStatus(Status.RED);
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
