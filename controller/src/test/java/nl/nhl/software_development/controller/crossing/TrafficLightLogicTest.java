package nl.nhl.software_development.controller.crossing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.nhl.software_development.controller.net.CrossingUpdate;
import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdateWrapper;

@SuppressWarnings("static-method")
@Tag("fast")
public class TrafficLightLogicTest
{
	// @BeforeAll
	// public static void initialize() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	// {
	// Field timeField = Time.class.getDeclaredField("time");
	// timeField.setAccessible(true);
	// timeField.set(null, Duration.ofSeconds(15));
	// System.err.println(Time.getTime().toString());
	// }

	@Test
	@DisplayName("Test basic logic I")
	public void testBasicLogicI()
	{
		Crossing.preUpdate(Duration.ofMillis(16));
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdateWrapper(101, 1, null, 1.0));
		crossing.update();
		assertEquals(TrafficLightUpdate.State.GREEN, crossing.serialize().getTrafficLightUpdate(101).getState());
	}

	@Test
	@DisplayName("Test basic logic II")
	public void testBasicLogicII()
	{
		Crossing.preUpdate(Duration.ofMillis(16));
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdateWrapper(101, 1, null, 1.0));
		crossing.handleUpdate(new TrafficUpdateWrapper(102, 2, null, 1.0));
		crossing.update();
		CrossingUpdate update = crossing.serialize();
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(101).getState());
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(102).getState());
	}

	@Test
	@DisplayName("Test interfering logic I")
	public void testInterferingLogic()
	{
		Crossing.preUpdate(Duration.ofMillis(16));
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdateWrapper(102, 1, null, 1.0));
		crossing.handleUpdate(new TrafficUpdateWrapper(107, 2, null, 1.0));
		crossing.update();
		CrossingUpdate update = crossing.serialize();
		assertEquals(TrafficLightUpdate.State.RED, update.getTrafficLightUpdate(101).getState());
		assertEquals(TrafficLightUpdate.State.RED, update.getTrafficLightUpdate(102).getState());
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(107).getState());
	}

	@Test
	@DisplayName("Test interfering logic II")
	public void testInterferingLogicII()
	{
		Crossing.preUpdate(Duration.ofMillis(16));
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdateWrapper(101, 1, null, 1.0));
		crossing.handleUpdate(new TrafficUpdateWrapper(107, 2, null, 1.0));
		crossing.update();
		CrossingUpdate update = crossing.serialize();
		assertEquals(TrafficLightUpdate.State.RED, update.getTrafficLightUpdate(101).getState());
		assertEquals(TrafficLightUpdate.State.RED, update.getTrafficLightUpdate(102).getState());
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(107).getState());
	}

	@Test
	@DisplayName("Test interfering logic III")
	public void testInterferingLogicIII()
	{
		Crossing.preUpdate(Duration.ofMillis(16));
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdateWrapper(106, 2, null, 1.0));
		crossing.handleUpdate(new TrafficUpdateWrapper(304, 1, null, 1.0));
		crossing.update();
		CrossingUpdate update = crossing.serialize();
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(106).getState());
		assertEquals(TrafficLightUpdate.State.GREEN, update.getTrafficLightUpdate(304).getState());
	}
}
