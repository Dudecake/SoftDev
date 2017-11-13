package nl.nhl.software_development.controller.crossing;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;

public class BikeTrafficLight extends TrafficLight
{
	private final Location origin;

	Location getOrigin()
	{
		return origin;
	}

	public BikeTrafficLight(int id, Status status, Location origin)
	{
		super(id, status);
		if (id < 300 || id > 500)
			throw new IllegalArgumentException("id doesn't correspond with a bike or pedestrian traffic light");
		this.origin = origin;
	}

	public BikeTrafficLight(int id, Status status, Location origin, int time)
	{
		super(id, status);
		if (id < 300 || id > 500)
			throw new IllegalArgumentException("id doesn't correspond with a bike or pedestrian traffic light");
		this.origin = origin;
		this.time = time;
	}

	@Override
	TrafficLightUpdate serialize()
	{
		return new TrafficLightUpdate(id, State.valueOf(status), time);
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (!BikeTrafficLight.class.isInstance(other))
		{
			if (BusTrafficLight.class.isInstance(other))
			{
				BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(other);
				if (busTrafficLight.getOrigin() == origin || busTrafficLight.getDestinations().contains(origin))
				{
					res = true;
				}
			}
			else if (CarTrafficLight.class.isInstance(other))
			{
				CarTrafficLight carTrafficLight = CarTrafficLight.class.cast(other);
				if (carTrafficLight.getOrigin() == origin || carTrafficLight.getDestinations().contains(origin))
				{
					res = true;
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
