package nl.nhl.software_development.controller.crossing;

import java.util.List;

public class CarTrafficLight extends TrafficLight
{
	private final Location origin;
	private final List<Location> destinations;

	Location getOrigin()
	{
		return origin;
	}

	List<Location> getDestinations()
	{
		return destinations;
	}

	public CarTrafficLight(int id, Status status, Location origin, List<Location> destinations)
	{
		super(id, status);
		if (id > 200)
			throw new IllegalArgumentException("id doesn't correspond with a car traffic light");
		this.origin = origin;
		this.destinations = destinations;
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (BikeTrafficLight.class.isInstance(other))
		{
			BikeTrafficLight bikeTrafficLight = BikeTrafficLight.class.cast(other);
			if (origin == bikeTrafficLight.getOrigin() || destinations.contains(bikeTrafficLight.getOrigin()))
			{
				res = true;
			}
		}
		else if (BusTrafficLight.class.isInstance(other))
		{
			BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(other);
			locationLoop: for (Location thisDest : destinations)
			{
				for (Location otherDest : busTrafficLight.getDirectionRequests())
				{
					if (TrafficLight.crossesWith(origin, thisDest, busTrafficLight.getOrigin(), otherDest))
					{
						res = true;
						break locationLoop;
					}
				}
			}
		}
		else if (CarTrafficLight.class.isInstance(other))
		{
			CarTrafficLight carTrafficLight = CarTrafficLight.class.cast(other);
			locationLoop: for (Location thisDest : destinations)
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
		if (other.getOrigin() == origin || destinations.contains(other.getOrigin()))
			res = true;
		return res;
	}

}
