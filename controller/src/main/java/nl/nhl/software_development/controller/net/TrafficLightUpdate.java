package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;

public class TrafficLightUpdate
{
	public enum State
	{
		RED, ORANGE, GREEN
	}

	@SerializedName("Id")
	private int id;
	@SerializedName("State")
	private State state;

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
}
