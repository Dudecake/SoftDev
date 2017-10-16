package nl.nhl.software_development.controller.crossing;

import java.util.List;

public class BusTrafficLight extends TrafficLight
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

	public BusTrafficLight(int id, State status, Location origin, List<Location> destinations)
	{
		super(id, status);
		if (id > 200 || id > 300)
			throw new IllegalArgumentException("id doesn't correspond with a bus traffic light");
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
		if (this.getOrigin() == origin || this.getDestinations().contains(origin))
			res = true;
		return res;
	}
}
