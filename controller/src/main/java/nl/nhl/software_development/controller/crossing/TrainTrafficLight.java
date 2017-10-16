package nl.nhl.software_development.controller.crossing;

import java.time.Duration;

public class TrainTrafficLight extends TrafficLight
{
	private final Location origin;

	Location getOrigin()
	{
		return origin;
	}

	public TrainTrafficLight(int id, State status, Location origin)
	{
		super(id, status);
		this.origin = origin;
		cycleTime = Duration.ofSeconds(30);
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (BusTrafficLight.class.isInstance(other))
		{
			BusTrafficLight busTrafficLight = BusTrafficLight.class.cast(other);
			if (busTrafficLight.getOrigin() == origin || busTrafficLight.getDestinations().contains(origin))
				res = true;
		}
		else if (CarTrafficLight.class.isInstance(other))
		{
			CarTrafficLight carTrafficLight = CarTrafficLight.class.cast(other);
			if (carTrafficLight.getOrigin() == origin || carTrafficLight.getDestinations().contains(origin))
				res = true;
		}
		return res;
	}

	@Override
	boolean interferesWith(TrainTrafficLight other)
	{
		return false;
	}

}
