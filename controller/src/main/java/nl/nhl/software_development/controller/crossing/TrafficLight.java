package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;

public abstract class TrafficLight
{
	public enum Status
	{
		RED, ORANGE, GREEN
	}

	enum Location
	{
		NORTH, EAST, WEST, SOUTH
	}

	protected static final Logger LOGGER = LoggerFactory.getLogger(TrafficLight.class);
	protected final int id;
	protected Status status;
	protected Duration cycleTime;
	protected ZonedDateTime resetTime;
	protected ZonedDateTime lastTime;
	protected int queueLength;

	int getId()
	{
		return id;
	}

	Status getStatus()
	{
		return status;
	}

	void setStatus(Status status)
	{
		if (status == Status.GREEN)
			lastTime = Crossing.updateTime;
		this.status = status;
	}

	int getWeight()
	{
		Duration delta = Duration.between(lastTime, Crossing.updateTime);
		return queueLength * (int)(delta.toMillis() / 2500);
	}

	public TrafficLight(int id, Status status)
	{
		this.id = id;
		this.status = status;
	}

	TrafficLightUpdate serialize()
	{
		TrafficLightUpdate res = new TrafficLightUpdate(id,
				nl.nhl.software_development.controller.net.TrafficLightUpdate.State.valueOf(status));

		return res;
	}

	boolean canReset(ZonedDateTime time)
	{
		boolean res = false;
		if (time.isAfter(resetTime))
			res = true;
		return res;
	}

	public boolean equals(TrafficLight other)
	{
		return this.id == other.id;
	}

	abstract boolean interferesWith(TrafficLight other);

	abstract boolean interferesWith(TrainTrafficLight other);
}
