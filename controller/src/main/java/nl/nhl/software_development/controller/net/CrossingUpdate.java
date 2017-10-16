package nl.nhl.software_development.controller.net;

import java.util.ArrayList;
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
		lights = new ArrayList<>();
		// Add car traffic lights
		for (int i = 101; i < 111; i++)
			lights.add(new TrafficLightUpdate(i, TrafficLightUpdate.State.RED));
		// Add bike traffic lights
		for (int i = 301; i < 306; i++)
			lights.add(new TrafficLightUpdate(i, TrafficLightUpdate.State.RED));
		// Add pedestrian traffic lights
		for (int i = 401; i < 407; i++)
			lights.add(new TrafficLightUpdate(i, TrafficLightUpdate.State.RED));
		// Add train traffic light
		lights.add(new TrafficLightUpdate(501, TrafficLightUpdate.State.RED));
		timeScale = 1.0;
	}

	public CrossingUpdate(List<TrafficLightUpdate> lights, double timeScale)
	{
		this.lights = lights;
		this.timeScale = timeScale;
	}
}
