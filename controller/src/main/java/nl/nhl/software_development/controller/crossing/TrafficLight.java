package nl.nhl.software_development.controller.crossing;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.Time;
import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;

public abstract class TrafficLight implements Comparable<TrafficLight>
{
	public enum Status
	{
		RED, ORANGE, GREEN
	}

	static class WeightComparator implements Comparator<TrafficLight>
	{
		@Override
		public int compare(TrafficLight o1, TrafficLight o2)
		{
			return (o1.getWeight() - o2.getWeight());
		}
	}

	enum Location
	{
		NORTH, EAST, WEST, SOUTH;
		public Point2D asPoint2d()
		{
			Point2D res = null;
			switch (this)
			{
			case NORTH:
				res = new Point2D.Float(1, 0);
				break;
			case EAST:
				res = new Point2D.Float(0, 1);
				break;
			case SOUTH:
				res = new Point2D.Float(-1, 0);
				break;
			case WEST:
				res = new Point2D.Float(0, -1);
			default:
				break;
			}
			return res;
		}
	}

	protected static final Logger LOGGER = LoggerFactory.getLogger(TrafficLight.class);
	protected final int id;
	protected Status status;
	protected Duration cycleTime;
	protected Duration resetTime;
	protected Duration lastTime;
	protected int queueLength;

	int getId()
	{
		return id;
	}

	Status getStatus()
	{
		return status;
	}

	Status setStatus(Status status)
	{
		if (status == Status.GREEN)
			lastTime = Crossing.updateTime;
		if (canReset(Time.getTime()))
			this.status = status;
		return this.status;
	}

	int getQueueLength()
	{
		return queueLength;
	}

	void setQueueLength(int queueLength)
	{
		this.queueLength = queueLength;
	}

	int getWeight()
	{
		Duration delta = lastTime.minus(Crossing.updateTime);
		return queueLength * (int)(delta.toMillis() / 2500.0);
	}

	public TrafficLight(int id, Status status)
	{
		this.id = id;
		this.status = status;
		lastTime = Duration.ZERO;
		cycleTime = Duration.ofSeconds(30);
		resetTime = Duration.ZERO;
	}

	TrafficLightUpdate serialize()
	{
		TrafficLightUpdate res = new TrafficLightUpdate(id, State.valueOf(status));
		return res;
	}

	boolean canReset(Duration time)
	{
		boolean res = false;
		if (time.compareTo(resetTime) >= 0)
			res = true;
		return res;
	}

	public boolean equals(TrafficLight other)
	{
		return this.id == other.id;
	}

	@Override
	public int compareTo(TrafficLight other)
	{
		return this.id - other.id;
	}

	protected static boolean crossesWith(Location originA, Location destA, Location originB, Location destB)
	{
		boolean res = false;
		if (originA != destB && originB != destA)
		{
			Line2D lineA = new Line2D.Float(originA.asPoint2d(), destA.asPoint2d());
			Line2D lineB = new Line2D.Float(originB.asPoint2d(), destB.asPoint2d());
			if (lineA.intersectsLine(lineB))
			{
				res = true;
			}
		}
		return res;
	}

	abstract boolean interferesWith(TrafficLight other);

	abstract boolean interferesWith(TrafficLightList others);

	abstract boolean interferesWith(TrainTrafficLight other);
}
