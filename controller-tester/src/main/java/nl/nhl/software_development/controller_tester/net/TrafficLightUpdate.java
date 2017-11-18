package nl.nhl.software_development.controller_tester.net;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TrafficLightUpdate
{
	public enum State
	{
		RED(0), ORANGE(1), GREEN(2);

		private final int value;

		private State(int value)
		{
			this.value = value;
		}

		public int asInt()
		{
			return value;
		}

		public static State valueOf(int value)
		{
			State res = State.RED;
			switch (value)
			{
			case 0:
				break;
			case 1:
				res = State.ORANGE;
				break;
			case 2:
				res = State.GREEN;
			default:
				break;
			}
			return res;
		}
	}

	public static class StateDeserializer implements JsonDeserializer<State>
	{

		@Override
		public State deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return State.valueOf(json.getAsInt());
		}
	}

	public static class StateSerializer implements JsonSerializer<State>
	{

		@Override
		public JsonElement serialize(State src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(src.asInt());
		}
	}

	private int id;
	private State status;
	@SuppressWarnings("unused")
	private int time;

	public int getId()
	{
		return id;
	}

	public State getState()
	{
		return status;
	}

	public void setState(State state)
	{
		this.status = state;
	}

	TrafficLightUpdate()
	{
		id = 101;
		status = State.RED;
	}

	public TrafficLightUpdate(int id, State state)
	{
		this.id = id;
		this.status = state;
		this.time = -1;
	}

	public TrafficLightUpdate(int id, State state, int time)
	{
		this(id, state);
		this.time = time;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = false;
		if (TrafficLightUpdate.class.isInstance(obj))
		{
			TrafficLightUpdate other = TrafficLightUpdate.class.cast(obj);
			res = (this.id == other.id && this.status == other.status);
		}
		else
			res = super.equals(obj);
		return res;
	}
}
