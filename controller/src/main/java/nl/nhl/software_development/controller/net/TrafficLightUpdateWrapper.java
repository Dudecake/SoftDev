package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;

import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;

public class TrafficLightUpdateWrapper
{
	@SerializedName("TrafficLightUpdate")
	TrafficLightUpdate trafficLightUpdate;

	public TrafficLightUpdateWrapper()
	{
		trafficLightUpdate = new TrafficLightUpdate();
	}

	public TrafficLightUpdateWrapper(int id, State state)
	{
		trafficLightUpdate = new TrafficLightUpdate(id, state);
	}

	public State getState()
	{
		return trafficLightUpdate.getState();
	}

	public int getId()
	{
		return trafficLightUpdate.getId();
	}

}
