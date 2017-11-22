package nl.nhl.software_development.controller_tester.crossing;

import nl.nhl.software_development.controller_tester.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller_tester.crossing.TrafficLight.Status;

public class Crossing
{
	private final TrafficLightList lights;

	public Crossing()
	{
		TrafficLightList lights = new TrafficLightList();
		// Add car traffic lights
		Location location = Location.NORTH;
		for (int i = 101; i < 111; i++)
		{
			switch (i)
			{
			case 104:
				location = Location.EAST;
				break;
			case 106:
				location = Location.SOUTH;
				break;
			case 108:
				location = Location.WEST;
				break;
			default:
				break;
			}
			lights.add(new TrafficLight(i, Status.RED, location));
		}
		// Add bus traffic light
		lights.add(new TrafficLight(201, Status.RED, Location.EAST));
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
			lights.add(new TrafficLight(i, Status.RED, location));
		}
		// Add pedestrian traffic lights
		location = Location.WEST;
		for (int i = 401; i < 412; i++)
		{
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
			lights.add(new TrafficLight(i, Status.RED, location));
		}
		// Add train traffic light
		lights.add(new TrafficLight(601, Status.RED, Location.SOUTH));
		lights.add(new TrafficLight(501, Status.RED, Location.SOUTH));
		lights.add(new TrafficLight(502, Status.RED, Location.SOUTH));
		lights.sort(null);
		this.lights = lights;
	}

	public TrafficLight getLightById(int lightId)
	{
		return lights.getId(lightId);
	}

}
