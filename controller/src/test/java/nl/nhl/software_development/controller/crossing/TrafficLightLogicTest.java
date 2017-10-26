package nl.nhl.software_development.controller.crossing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.nhl.software_development.controller.net.TrafficLightUpdate;
import nl.nhl.software_development.controller.net.TrafficUpdate;

@SuppressWarnings("static-method")
@Tag("fast")
public class TrafficLightLogicTest
{
	@Test
	@DisplayName("Test basic logic")
	public void testBasicLogic()
	{
		Crossing.preUpdate(Duration.ZERO);
		Crossing crossing = new Crossing();
		crossing.handleUpdate(new TrafficUpdate(101, 1, null, 1.0));
		Crossing.preUpdate();
		crossing.update();
		assertEquals(TrafficLightUpdate.State.GREEN, crossing.serialize().geTrafficLightUpdate(101).getState());
	}
}
