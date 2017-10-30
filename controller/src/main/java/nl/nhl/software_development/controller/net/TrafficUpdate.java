package nl.nhl.software_development.controller.net;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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

		public int asInt()
		{
			return direction;
		}

		public static DirectionRequest valueOf(int direction)
		{
			DirectionRequest res = DirectionRequest.STRAIGHT;
			switch (direction)
			{
			case 2:
				break;
			case 3:
				res = DirectionRequest.LEFT;
			case 4:
				res = DirectionRequest.RIGHT;
				break;
			}
			return res;
		}
	}

	public static class DirectionRequestDeserializer implements JsonDeserializer<DirectionRequest>
	{
		@Override
		public DirectionRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			int direction = json.getAsInt();
			return DirectionRequest.valueOf(direction);
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

	TrafficUpdate()
	{
		// TODO Auto-generated constructor stub
	}

	TrafficUpdate(int lightId, int count, List<DirectionRequest> directionRequests, double speed)
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
