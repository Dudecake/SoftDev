package nl.nhl.software_development.controller.crossing;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;

public abstract class TrafficLight
{
	enum State
	{
		RED, ORANGE, GREEN
	}

	enum Location
	{
		NORTH, EAST, WEST, SOUTH
	}

	protected static final Logger LOGGER = LoggerFactory.getLogger(TrafficLight.class);
	protected static ZonedDateTime updateTime;
	protected final int id;
	protected State status;
	protected Duration cycleTime;
	protected ZonedDateTime resetTime;
	protected ZonedDateTime lastTime;
	protected int queueLength;

	int getId()
	{
		return id;
	}

	State getStatus()
	{
		return status;
	}

	void setStatus(State status)
	{
		if (status == State.GREEN)
			lastTime = updateTime;
		this.status = status;
	}

	int getWeight()
	{
		Duration delta = Duration.between(lastTime, updateTime);
		return queueLength * (int)(delta.toMillis() / 2500);
	}

	public TrafficLight(int id, State status)
	{
		this.id = id;
		this.status = status;
	}

	static void preUpdate()
	{
		updateTime = ZonedDateTime.now();
	}

	TrafficLightUpdate serialize()
	{
		TrafficLightUpdate res = new TrafficLightUpdate(id, nl.nhl.software_development.controller.net.TrafficLightUpdate.State.RED);

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
