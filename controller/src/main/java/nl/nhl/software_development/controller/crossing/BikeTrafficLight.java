package nl.nhl.software_development.controller.crossing;

import java.util.ArrayList;
import java.util.List;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;

public class BikeTrafficLight extends TrafficLight
{
	private final Location origin;
	private List<BikeTrafficLight> pairedLights;
	private BikeTrafficLight pairedLight;

	Location getOrigin()
	{
		return origin;
	}

	@Override
	Status setStatus(Status status)
	{
		Status res = this.status;
		if (status != Status.GREEN && queueLength != 0)
		{
			res = super.setStatus(status);
			if (pairedLight != null)
			{
				pairedLight.status = res;
				if (res == Status.GREEN)
				{
					// lastTime = Crossing.updateTime;
					// resetTime = lastTime.plus(cycleTime);
				}
			}
		}
		return res;
	}

	public BikeTrafficLight(int id, Status status, Location origin)
	{
		super(id, status);
		if (id < 300 || id > 500)
			throw new IllegalArgumentException("id doesn't correspond with a bike or pedestrian traffic light");
		this.origin = origin;
		this.pairedLights = new ArrayList<>();
		this.pairedLight = null;
	}

	public BikeTrafficLight(int id, Status status, Location origin, int time)
	{
		super(id, status);
		if (id < 300 || id > 500)
			throw new IllegalArgumentException("id doesn't correspond with a bike or pedestrian traffic light");
		this.origin = origin;
		this.time = time;
		this.pairedLights = new ArrayList<>();
		this.pairedLight = null;
	}

	@Override
	TrafficLightUpdate serialize()
	{
		TrafficLightUpdate res = new TrafficLightUpdate(id, State.valueOf(status), time);
		// if (id == )
		return res;
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
				if (other.id != 106)
				{
					CarTrafficLight carTrafficLight = CarTrafficLight.class.cast(other);
					if (carTrafficLight.getOrigin() == origin || carTrafficLight.getDestinations().contains(origin))
					{
						res = true;
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

	void pairLights(BikeTrafficLight... lights)
	{
		for (BikeTrafficLight l : lights)
		{
			pairedLights.add(l);
		}
	}

	static void pairLights(BikeTrafficLight lightA, BikeTrafficLight lightB)
	{
		lightA.pairedLight = lightB;
		lightB.pairedLight = lightA;
	}
}
