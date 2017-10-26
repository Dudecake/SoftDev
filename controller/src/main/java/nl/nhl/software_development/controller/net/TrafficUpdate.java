package nl.nhl.software_development.controller.net;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TrafficUpdate
{
	public enum DirectionRequest
	{
		STRAIGHT(2), LEFT(3), RIGHT(4);
		private final int direction;

		DirectionRequest(int direction)
		{
			this.direction = direction;
		}
	}

	@SerializedName("Speed")
	private double speed;
	@SerializedName("LightId")
	private int lightId;
	@SerializedName("Count")
	private int count;
	@SerializedName("DirectionRequests")
	private List<DirectionRequest> directionRequests;

	public TrafficUpdate()
	{
		// TODO Auto-generated constructor stub
	}

	public TrafficUpdate(int lightId, int count, List<DirectionRequest> directionRequests, double speed)
	{
		this.speed = speed;
		this.lightId = lightId;
		this.count = count;
		this.directionRequests = directionRequests;
	}

	public double getSpeed()
	{
		return speed;
	}

	public int getLightId()
	{
		return lightId;
	}

	public int getCount()
	{
		return count;
	}

	public List<DirectionRequest> getDirectionRequests()
	{
		return directionRequests;
	}
}
