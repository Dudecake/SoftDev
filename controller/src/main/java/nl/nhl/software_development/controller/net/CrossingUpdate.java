package nl.nhl.software_development.controller.net;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CrossingUpdate
{
	@SerializedName("Lights")
	private List<TrafficLightUpdate> lights;
	@SerializedName("Speed")
	private double timeScale;

	public CrossingUpdate()
	{

	}

	public CrossingUpdate(List<TrafficLightUpdate> lights, double timeScale)
	{
		this.lights = lights;
		this.timeScale = timeScale;
	}
}
