package nl.nhl.software_development.controller.net;

import java.util.List;
import java.util.stream.Collectors;

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

	public TrafficLightUpdate geTrafficLightUpdate(int lightId)
	{
		return lights.parallelStream().filter(l -> l.getId() == lightId).collect(Collectors.toList()).get(0);
	}
}
