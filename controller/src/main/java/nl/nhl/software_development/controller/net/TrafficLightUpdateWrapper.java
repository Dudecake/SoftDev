package nl.nhl.software_development.controller.net;

import nl.nhl.software_development.controller.net.TrafficLightUpdate.State;

public class TrafficLightUpdateWrapper
{
	TrafficLightUpdate trafficLightUpdate;

	public TrafficLightUpdateWrapper()
	{
		trafficLightUpdate = new TrafficLightUpdate();
	}

	public TrafficLightUpdateWrapper(int id, State state)
	{
		trafficLightUpdate = new TrafficLightUpdate(id, state);
	}

	public TrafficLightUpdateWrapper(int id, State state, int time)
	{
		trafficLightUpdate = new TrafficLightUpdate(id, state, time);
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
