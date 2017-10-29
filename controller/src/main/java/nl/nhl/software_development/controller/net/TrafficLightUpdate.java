package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;

import nl.nhl.software_development.controller.crossing.TrafficLight.Status;

public class TrafficLightUpdate
{
	public enum State
	{
		RED, ORANGE, GREEN;

		public static State valueOf(Status status)
		{
			State res = State.RED;
			switch (status)
			{
			case ORANGE:
				res = State.ORANGE;
				break;
			case GREEN:
				res = State.GREEN;
			default:
				break;
			}
			return res;
		}
	}

	@SerializedName("Id")
	private int id;
	@SerializedName("State")
	private State state;

	public int getId()
	{
		return id;
	}

	public State getState()
	{
		return state;
	}

	public TrafficLightUpdate()
	{
		id = 101;
		state = State.RED;
	}

	public TrafficLightUpdate(int id, State state)
	{
		this.id = id;
		this.state = state;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = false;
		if (TrafficLightUpdate.class.isInstance(obj))
		{
			TrafficLightUpdate other = TrafficLightUpdate.class.cast(obj);
			res = (this.id == other.id && this.state == other.state);
		}
		else
			res = super.equals(obj);
		return res;
	}
}
