package nl.nhl.software_development.controller_tester.net;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TrafficUpdate
{
	public enum DirectionRequest
	{
		STRAIGHT(2), LEFT(3), RIGHT(4);
		private final int direction;

		private DirectionRequest(int direction)
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
		public DirectionRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException
		{
			return DirectionRequest.valueOf(json.getAsInt());
		}
	}

	public static class DirectionRequestSerializer implements JsonSerializer<DirectionRequest>
	{

		@Override
		public JsonElement serialize(DirectionRequest src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.asInt());
		}
	}

	private int lightId;
	private int count;
	private List<DirectionRequest> directionRequests;

	TrafficUpdate()
	{
		this.lightId = 101;
		this.count = 0;
		this.directionRequests = null;
	}

	TrafficUpdate(int lightId, int count, List<DirectionRequest> directionRequests)
	{
		this.lightId = lightId;
		this.count = count;
		this.directionRequests = directionRequests;
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
