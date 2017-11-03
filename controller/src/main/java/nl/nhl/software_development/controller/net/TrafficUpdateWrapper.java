package nl.nhl.software_development.controller.net;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequest;

public class TrafficUpdateWrapper
{
	@SerializedName("TrafficUpdate")
	private TrafficUpdate trafficUpdate;

	public TrafficUpdate geTrafficLightUpdate()
	{
		return trafficUpdate;
	}

	public TrafficUpdateWrapper()
	{
		trafficUpdate = new TrafficUpdate();
	}

	public TrafficUpdateWrapper(int lightId, int count, List<DirectionRequest> directionRequests, double speed)
	{
		trafficUpdate = new TrafficUpdate(lightId, count, directionRequests, speed);
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
