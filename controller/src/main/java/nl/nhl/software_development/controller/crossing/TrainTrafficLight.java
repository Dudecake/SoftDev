package nl.nhl.software_development.controller.crossing;

import java.time.Duration;

import nl.nhl.software_development.controller.Time;
import nl.nhl.software_development.controller.net.TrafficLightUpdate;

public class TrainTrafficLight extends TrafficLight
{
	private final Location origin;
	private final TrainCrossingLight connectedLight;
	private Duration crossTime;

	Location getOrigin()
	{
		return origin;
	}

	@Override
	void setQueueLength(int queueLength)
	{
		connectedLight.setQueueLength(queueLength);
		if (queueLength == 0)
		{
			this.status = Status.RED;
			clearCrossTime();
		}
		this.queueLength = queueLength;
	}

	@Override
	Status setStatus(Status status)
	{
		this.status = status;
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
		this.cycleTime = Duration.ofSeconds(30);
		this.crossTime = null;
	}

	void installCrossTime()
	{
		crossTime = Time.getTime().plus(Duration.ofSeconds(10));
	}

	boolean canCross()
	{
		return crossTime.compareTo(Time.getTime()) < 0;
	}

	boolean hasCrossTime()
	{
		return crossTime != null;
	}

	void clearCrossTime()
	{
		crossTime = null;
	}

	@Override
	TrafficLightUpdate serialize()
	{
		return id == 501 ? super.serialize() : null;
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
