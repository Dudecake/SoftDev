package nl.nhl.software_development.controller_tester.crossing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrafficLightList extends ArrayList<TrafficLight>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3485823910488651024L;

	public TrafficLight getId(int lightId)
	{
		List<TrafficLight> resList = this.parallelStream().filter(l -> l.getId() == lightId).collect(Collectors.toList());
		if (resList.isEmpty())
		{
			throw new NullPointerException(String.format("TrafficLight with id %s is not in the TrafficLightList", lightId));
		}
		return resList.get(0);
	}
}
