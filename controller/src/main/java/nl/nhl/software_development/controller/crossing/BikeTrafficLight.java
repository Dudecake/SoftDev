package nl.nhl.software_development.controller.crossing;

public class BikeTrafficLight extends TrafficLight
{
	private final Location origin;

	public BikeTrafficLight(int id, State status, Location origin)
	{
		super(id, status);
		if (id < 300 || id > 500)
			throw new IllegalArgumentException("id doesn't correspond with a bike or pedestrian traffic light");
		this.origin = origin;
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = true;
		if (BikeTrafficLight.class.isInstance(other))
		{
			res = false;
		}
		return res;
	}

	@Override
	boolean interferesWith(TrainTrafficLight other)
	{
		return false;
	}

}
