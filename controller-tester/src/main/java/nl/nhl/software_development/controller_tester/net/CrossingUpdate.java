package nl.nhl.software_development.controller_tester.net;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrossingUpdate
{
	private List<TrafficLightUpdate> lights;
	@SuppressWarnings("unused")
	private Double timeScale;

	public CrossingUpdate()
	{
		lights = new ArrayList<>(0);
		timeScale = 1.0;
	}

	public CrossingUpdate(List<TrafficLightUpdate> lights, Double timeScale)
	{
		this.lights = lights;
		this.timeScale = timeScale;
	}

	public TrafficLightUpdate getTrafficLightUpdate(int lightId)
	{
		return lights.parallelStream().filter(l -> l.getId() == lightId).collect(Collectors.toList()).get(0);
	}

	public List<TrafficLightUpdate> getLights()
	{
		return lights;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = true;
		if (CrossingUpdate.class.isInstance(obj))
		{
			CrossingUpdate other = CrossingUpdate.class.cast(obj);
			if (/* this.timeScale != other.timeScale || */this.lights.size() != other.lights.size())
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
