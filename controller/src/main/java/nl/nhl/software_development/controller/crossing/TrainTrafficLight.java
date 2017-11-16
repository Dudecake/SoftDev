package nl.nhl.software_development.controller.crossing;

import java.time.Duration;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;

public class TrainTrafficLight extends TrafficLight
{
	private final Location origin;
	private final TrainCrossingLight connectedLight;

	Location getOrigin()
	{
		return origin;
	}

	@Override
	void setQueueLength(int queueLength)
	{
		connectedLight.setQueueLength(queueLength);
	}

	@Override
	Status setStatus(Status status)
	{
		return status;
	}

	@Override
	int getWeight()
	{
		return id == 502 ? Short.MAX_VALUE : 0;
	}

	@Override
	boolean canReset(Duration time)
	{
		return true;
	}

	public TrainTrafficLight(int id, Status status, Location origin, TrainCrossingLight connectedLight)
	{
		super(id, status);
		this.origin = origin;
		this.connectedLight = connectedLight;
		cycleTime = Duration.ofSeconds(30);
	}

	@Override
	TrafficLightUpdate serialize()
	{
		return null;
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		boolean res = false;
		if (TrainTrafficLight.class.isInstance(other))
		{
			res = (!this.equals(other));
		}
		return res;
	}

	@Override
	boolean interferesWith(TrafficLightList others)
	{
		return false;
	}

}
