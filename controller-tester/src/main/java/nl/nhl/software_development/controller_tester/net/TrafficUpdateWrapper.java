package nl.nhl.software_development.controller_tester.net;

import java.util.List;

import nl.nhl.software_development.controller_tester.net.TrafficUpdate.DirectionRequest;

public class TrafficUpdateWrapper
{
	private TrafficUpdate trafficUpdate;

	public TrafficUpdate geTrafficLightUpdate()
	{
		return trafficUpdate;
	}

	public TrafficUpdateWrapper()
	{
		trafficUpdate = new TrafficUpdate();
	}

	public TrafficUpdateWrapper(int lightId, int count, List<DirectionRequest> directionRequests)
	{
		trafficUpdate = new TrafficUpdate(lightId, count, directionRequests);
	}

	public int getUpdateHash()
	{
		int res = -1;
		if (trafficUpdate != null)
			res = TrafficUpdate.class.hashCode();
		else
			res = Double.class.hashCode();
		return res;
	}

	public int getLightId()
	{
		return trafficUpdate.getLightId();
	}

	public int getCount()
	{
		return trafficUpdate.getCount();
	}

	public List<DirectionRequest> getDirectionRequests()
	{
		return trafficUpdate.getDirectionRequests();
	}
}
