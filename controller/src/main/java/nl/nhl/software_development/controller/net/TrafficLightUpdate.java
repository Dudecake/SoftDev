package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;

import nl.nhl.software_development.controller.crossing.TrafficLight.Status;

public class TrafficLightUpdate
{
	public enum State
	{
		@SerializedName("0")
		RED, @SerializedName("1")
		ORANGE, @SerializedName("2")
		GREEN;

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

	private int id;
	private State state;
	@SuppressWarnings("unused")
	private int time;

	public int getId()
	{
		return id;
	}

	public State getState()
	{
		return state;
	}

	TrafficLightUpdate()
	{
		id = 101;
		state = State.RED;
	}

	TrafficLightUpdate(int id, State state)
	{
		this.id = id;
		this.state = state;
		this.time = -1;
	}

	TrafficLightUpdate(int id, State state, int time)
	{
		this(id, state);
		this.time = time;
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
