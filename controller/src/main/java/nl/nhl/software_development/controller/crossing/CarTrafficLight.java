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
		// TODO Auto-generated method stub
		return false;
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
