package nl.nhl.software_development.controller.net;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CrossingUpdateWrapper
{
	@SerializedName("CrossingUpdate")
	CrossingUpdate crossingUpdate;

	public CrossingUpdateWrapper()
	{
		crossingUpdate = new CrossingUpdate();
	}

	public CrossingUpdateWrapper(List<TrafficLightUpdateWrapper> lights, double timeScale)
	{
		crossingUpdate = new CrossingUpdate(lights, timeScale);
	}

	public TrafficLightUpdateWrapper geTrafficLightUpdate(int i)
	{
		return crossingUpdate.geTrafficLightUpdate(i);
	}

}
