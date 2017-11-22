package nl.nhl.software_development.controller_tester.crossing;

public class TrafficLight implements Comparable<TrafficLight>
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

		public static Status valueOf(int value)
		{
			Status res = Status.RED;
			switch (value)
			{
			case 0:
				break;
			case 1:
				res = Status.ORANGE;
				break;
			case 2:
				res = Status.GREEN;
			}
			return res;
		}
	}

	enum Location
	{
		NORTH, EAST, WEST, SOUTH;
	}

	private final int id;
	private Status status;
	private int queueLength;
	private Location location;

	int getId()
	{
		return id;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	int getQueueLength()
	{
		return queueLength;
	}

	public void setQueueLength(int queueLength)
	{
		this.queueLength = queueLength;
	}

	public Location getLocation()
	{
		return location;
	}

	public TrafficLight(int id, Status status, Location location)
	{
		this.id = id;
		this.status = status;
		this.queueLength = 0;
		this.location = location;
	}

	@Override
	public int compareTo(TrafficLight other)
	{
		return this.id - other.id;
	}

}
