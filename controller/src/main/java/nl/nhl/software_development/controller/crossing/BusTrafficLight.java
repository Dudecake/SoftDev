package nl.nhl.software_development.controller.crossing;

import java.util.ArrayList;
import java.util.List;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;
import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequest;

public class BusTrafficLight extends TrafficLight
{
	private final Location origin;
	private final List<Location> destinations;
	private List<Location> directionRequests;
	private Status busStatus;

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
		if (directionRequests != null)
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
	}

	@Override
	Status setStatus(Status status)
	{
		Status res = super.setStatus(status);
		if (res == Status.GREEN)
			busStatus = Status.STRAIGHTRIGHT;
		else
			busStatus = res;
		return res;
	}

	@Override
	TrafficLightUpdate serialize()
	{
		return new TrafficLightUpdate(id, State.valueOf(busStatus), -1);
	}

	public BusTrafficLight(int id, Status status, Location origin, List<Location> destinations)
	{
		super(id, status);
		if (id < 200 || id > 300)
			throw new IllegalArgumentException("id doesn't correspond with a bus traffic light");
		this.origin = origin;
		this.destinations = destinations;
		this.directionRequests = new ArrayList<>();
		this.busStatus = status;
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (TrainCrossingLight.class.isInstance(other))
		{
			TrainCrossingLight trainTrafficLight = TrainCrossingLight.class.cast(other);
			if (origin == trainTrafficLight.getOrigin() || directionRequests.contains(trainTrafficLight.getOrigin()))
				res = true;
		}
		else if (BikeTrafficLight.class.isInstance(other))
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
	boolean interferesWith(TrafficLightList others)
	{
		boolean res = false;
		for (TrafficLight t : others)
		{
			if (interferesWith(t))
			{
				res = true;
				break;
			}
		}
		return res;
	}
}
