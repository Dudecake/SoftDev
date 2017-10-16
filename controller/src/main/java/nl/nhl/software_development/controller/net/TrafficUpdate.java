package nl.nhl.software_development.controller.net;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TrafficUpdate
{
	enum DirectionRequest
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
}
