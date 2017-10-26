package nl.nhl.software_development.controller.crossing;

import java.util.ArrayList;

class TrafficLightList extends ArrayList<TrafficLight>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3485823910488651024L;

	public TrafficLight getId(int lightId)
	{
		TrafficLight res = null;
		for (TrafficLight t : this)
		{
			if (t.id == lightId)
			{
				res = t;
				break;
			}
		}
		return res;
	}
}
