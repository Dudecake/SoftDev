package nl.nhl.software_development.controller.net;

import java.util.List;

import nl.nhl.software_development.controller.net.TrafficUpdate.DirectionRequest;

public class TrafficUpdateWrapper
{
	private TrafficUpdate trafficUpdate;
	private TimescaleUpdate speed;

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
		trafficUpdate = new TrafficUpdate(lightId, count, directionRequests);
		this.speed = new TimescaleUpdate(speed);
	}

	public int getUpdateHash()
	{
		int res = -1;
		if (trafficUpdate != null)
			res = TrafficUpdate.class.hashCode();
		else if (speed != null)
			res = TimescaleUpdate.class.hashCode();
		return res;
	}

	public double getTimescale()
	{
		return speed.getSpeed();
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