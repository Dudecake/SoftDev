package nl.nhl.software_development.controller.net;

import java.util.List;

public class CrossingUpdateWrapper
{
	CrossingUpdate crossingUpdate;

	public CrossingUpdateWrapper()
	{
		crossingUpdate = new CrossingUpdate();
	}

	public CrossingUpdateWrapper(List<TrafficLightUpdateWrapper> lights, double timeScale)
	{
		crossingUpdate = new CrossingUpdate(lights, timeScale);
	}

	public TrafficLightUpdateWrapper getTrafficLightUpdate(int i)
	{
		return crossingUpdate.getTrafficLightUpdate(i);
	}

}
