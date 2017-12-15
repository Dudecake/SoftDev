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
		RED, ORANGE, GREEN;
		public Status inverse()
		{
			Status res = Status.ORANGE;
			switch (this)
			{
			case RED:
				res = Status.GREEN;
				break;
			case ORANGE:
				break;
			case GREEN:
				res = Status.RED;
			}
			return res;
		}
	}

	static class InverseWeightComparator implements Comparator<TrafficLight>
	{
		@Override
		public int compare(TrafficLight o1, TrafficLight o2)
		{
			return (o2.getWeight() - o1.getWeight());
		}
	}

	static class WeightComparator implements Comparator<TrafficLight>
	{
		@Override
		public int compare(TrafficLight o1, TrafficLight o2)
		{
			return (o1.getWeight() - o2.getWeight());
		}
	}

	static void fillInterferingLights(final TrafficLightList lights)
	{
		for (TrafficLight t : lights)
		{
			for (TrafficLight o : lights)
			{
				if (!t.equals(o) && t.interferesWith(o) && !t.getinterferingLights().contains(o))
				{
					t.addInterferingLight(o);
					o.addInterferingLight(t);
				}
			}
		}
	}

	enum Location
	{
		NORTH, EAST, WEST, SOUTH;
		@Deprecated
		public Point2D asPoint2d()
		{
			Point2D res = null;
			switch (this)
			{
			case NORTH:
				res = new Point2D.Float(0, 1);
				break;
			case EAST:
				res = new Point2D.Float(1, 0);
				break;
			case SOUTH:
				res = new Point2D.Float(0, -1);
				break;
			case WEST:
				res = new Point2D.Float(-1, 0);
			default:
				break;
			}
			return res;
		}

		public Point2D asOriginPoint2d()
		{
			Point2D res = null;
			switch (this)
			{
			case NORTH:
				res = new Point2D.Float(-.25f, 1);
				break;
			case EAST:
				res = new Point2D.Float(1, .25f);
				break;
			case SOUTH:
				res = new Point2D.Float(.25f, -1);
				break;
			case WEST:
				res = new Point2D.Float(-1, -.25f);
			default:
				break;
			}
			return res;
		}

		public Point2D asDestPoint2d()
		{
			Point2D res = null;
			switch (this)
			{
			case NORTH:
				res = new Point2D.Float(.25f, 1);
				break;
			case EAST:
				res = new Point2D.Float(1, -.25f);
				break;
			case SOUTH:
				res = new Point2D.Float(-.25f, -1);
				break;
			case WEST:
				res = new Point2D.Float(-1, .25f);
			default:
				break;
			}
			return res;
		}
	}

	static TrafficLight getHeaviestLight(TrafficLight aLight, TrafficLight bLight)
	{
		return aLight.queueLength > bLight.queueLength ? aLight : bLight;
	}

	private static final int DEBUGID = 101;
	protected static final Logger LOGGER = LoggerFactory.getLogger(TrafficLight.class);
	protected final int id;
	protected Status status;
	protected int time;
	protected Duration cycleTime;
	protected Duration orangeTime;
	protected Duration resetTime;
	protected Duration lastTime;
	protected int queueLength;
	protected TrafficLightList interferingLights;

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
		if (canReset(Time.getTime()))
		{
			if (this.status == Status.GREEN)
			{
				if (status == Status.RED)
				{
					this.status = Status.ORANGE;
					LOGGER.trace(String.format("Setting %d to %s", DEBUGID, this.status.toString()));
				}
				else
				{
					this.status = status;
					LOGGER.trace(String.format("Setting %d to %s", DEBUGID, this.status.toString()));
				}
			}
			else
			{
				this.status = status;
				LOGGER.trace(String.format("Setting %d to %s", DEBUGID, this.status.toString()));
			}
			lastTime = Crossing.updateTime;
			if (this.status == Status.ORANGE)
				resetTime = lastTime.plus(orangeTime);
			else
				resetTime = lastTime.plus(cycleTime);
			LOGGER.trace(String.format("Setting %d resetTime on %s (current time %s)", DEBUGID, resetTime.toString(), Time.getTime().toString()));
		}
		return this.status;
	}

	boolean canReset(Duration time)
	{
		boolean res = false;
		if (id == DEBUGID)
			LOGGER.trace(String.format("Called canreset on id %d", DEBUGID));
		// Duration compareTime = cycleTime;
		// if (status == Status.ORANGE)
		// {
		// compareTime = orangeTime;
		// }
		if (time.compareTo(resetTime) > 0)
		{
			res = true;
			if (id == DEBUGID)
			{
				LOGGER.trace(String.format("Id %s can reset", DEBUGID));
				LOGGER.trace(String.format("%s vs %s", time.toString(), resetTime.toString()));
			}
		}
		else
		{
			if (id == DEBUGID)
			{
				LOGGER.trace(String.format("Id %s can not reset", DEBUGID));
				LOGGER.trace(String.format("%s vs %s", time.toString(), resetTime.toString()));
			}
		}
		return res;
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
		Duration delta = Crossing.updateTime.minus(lastTime);
		return (int)(queueLength * delta.toMillis());
	}

	public TrafficLight(int id, Status status)
	{
		this.id = id;
		this.status = status;
		this.time = -1;
		lastTime = Duration.ZERO;
		orangeTime = Duration.ofMillis(2500);
		cycleTime = Duration.ofSeconds(5);
		resetTime = Duration.ofSeconds(1);
		interferingLights = new TrafficLightList();
	}

	void addInterferingLight(TrafficLight light)
	{
		interferingLights.add(light);
	}

	TrafficLightList getinterferingLights()
	{
		return interferingLights;
	}

	TrafficLightUpdate serialize()
	{
		int time = -1;
		if (id == 301 || id == 302)
		{
			for (TrafficLight l : interferingLights)
			{
				if (time < l.resetTime.toMillis() / 1000)
					time = (int)(l.resetTime.toMillis() / 1000);
			}
		}
		return new TrafficLightUpdate(id, State.valueOf(status), time);
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = false;
		if (TrafficLight.class.isInstance(obj))
		{
			TrafficLight other = TrafficLight.class.cast(obj);
			res = this.id == other.id;
		}
		else
			res = super.equals(obj);
		return res;
	}

	@Override
	public int compareTo(TrafficLight other)
	{
		return this.id - other.id;
	}

	protected static boolean crossesWith(Location originA, Location destA, Location originB, Location destB)
	{
		boolean res = false;
		if (originA != originB && originA != destB)
		{
			Line2D lineA = new Line2D.Float(originA.asOriginPoint2d(), destA.asDestPoint2d());
			Line2D lineB = new Line2D.Float(originB.asOriginPoint2d(), destB.asDestPoint2d());
			if (lineA.intersectsLine(lineB))
			{
				res = true;
			}
		}
		return res;
	}

	@Override
	public String toString()
	{
		return String.format("TrafficLight id: %d", id);
	}

	abstract boolean interferesWith(TrafficLight other);

	abstract boolean interferesWith(TrafficLightList others);
}
