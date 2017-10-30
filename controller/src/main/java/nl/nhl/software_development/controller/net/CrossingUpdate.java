package nl.nhl.software_development.controller.net;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

public class CrossingUpdate
{
	@SerializedName("Lights")
	private List<TrafficLightUpdateWrapper> lights;
	@SerializedName("Speed")
	private double timeScale;

	CrossingUpdate()
	{
		lights = new ArrayList<>(0);
		timeScale = 1.0;
	}

	CrossingUpdate(List<TrafficLightUpdateWrapper> lights, double timeScale)
	{
		this.lights = lights;
		this.timeScale = timeScale;
	}

	TrafficLightUpdateWrapper geTrafficLightUpdate(int lightId)
	{
		return lights.parallelStream().filter(l -> l.getId() == lightId).collect(Collectors.toList()).get(0);
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = true;
		if (CrossingUpdate.class.isInstance(obj))
		{
			CrossingUpdate other = CrossingUpdate.class.cast(obj);
			if (this.timeScale != other.timeScale || this.lights.size() != other.lights.size())
			{
				res = false;
			}
			for (int i = 0; res && i < lights.size(); i++)
			{
				if (!this.lights.get(i).equals(other.lights.get(i)))
					res = false;
			}
		}
		else
			res = super.equals(obj);
		return res;
	}
}
