package nl.nhl.software_development.controller.crossing;

import java.util.ArrayList;
import java.util.List;

import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequest;

public class BusTrafficLight extends TrafficLight
{
	private final Location origin;
	private final List<Location> destinations;
	private List<Location> directionRequests;

	Location getOrigin()
	{
		return origin;
	}

	List<Location> getDestinations()
	{
		return destinations;
	}

	List<Location> getDirectionRequests()
	{
		return directionRequests;
	}

	void setDirectionRequests(List<DirectionRequest> directionRequests)
	{
		this.directionRequests.clear();
		for (DirectionRequest request : directionRequests)
		{
			Location dest = Location.NORTH;
			if (origin == Location.EAST)
			{
				switch (request)
				{
				case STRAIGHT:
					dest = Location.WEST;
					break;
				case RIGHT:
					break;
				default:
					break;
				}
			}
			this.directionRequests.add(dest);
		}
	}

	public BusTrafficLight(int id, Status status, Location origin, List<Location> destinations)
	{
		super(id, status);
		if (id < 200 || id > 300)
			throw new IllegalArgumentException("id doesn't correspond with a bus traffic light");
		this.origin = origin;
		this.destinations = destinations;
		this.directionRequests = new ArrayList<>();
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (BikeTrafficLight.class.isInstance(other))
		{
			BikeTrafficLight bikeTrafficLight = BikeTrafficLight.class.cast(other);
			if (origin == bikeTrafficLight.getOrigin() || directionRequests.contains(bikeTrafficLight.getOrigin()))
			{
				res = true;
			}
		}
		else if (BusTrafficLight.class.isInstance(other))
		{
			res = false;
		}
		else if (CarTrafficLight.class.isInstance(other))
		{
			CarTrafficLight carTrafficLight = CarTrafficLight.class.cast(other);
			locationLoop: for (Location thisDest : directionRequests)
			{
				for (Location otherDest : carTrafficLight.getDestinations())
				{
					if (TrafficLight.crossesWith(origin, thisDest, carTrafficLight.getOrigin(), otherDest))
					{
						res = true;
						break locationLoop;
					}
				}
			}
		}
		return res;
	}

	@Override
	boolean interferesWith(TrainTrafficLight other)
	{
		boolean res = false;
		if (origin == other.getOrigin() || directionRequests.contains(other.getOrigin()))
			res = true;
		return res;
	}
}
