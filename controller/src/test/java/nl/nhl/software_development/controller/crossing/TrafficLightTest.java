package nl.nhl.software_development.controller.crossing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import nl.nhl.software_development.controller.crossing.TrafficLight.Location;
import nl.nhl.software_development.controller.crossing.TrafficLight.Status;

@SuppressWarnings("static-method")
@Tag("fast")
public class TrafficLightTest
{
	@Test
	@DisplayName("Test intersection")
	public void testIntersection()
	{
		CarTrafficLight lightA = new CarTrafficLight(103, Status.RED, Location.NORTH, Arrays.asList(Location.EAST));
		CarTrafficLight lightB = new CarTrafficLight(106, Status.RED, Location.SOUTH,
				Arrays.asList(Location.NORTH, Location.EAST));
		assertTrue(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test no intersection I")
	public void testNoIntersectionI()
	{
		CarTrafficLight lightA = new CarTrafficLight(103, Status.RED, Location.NORTH, Arrays.asList(Location.EAST));
		CarTrafficLight lightB = new CarTrafficLight(106, Status.RED, Location.EAST, Arrays.asList(Location.NORTH));
		assertFalse(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test no intersection II")
	public void testNoIntersectionII()
	{
		CarTrafficLight lightA = new CarTrafficLight(103, Status.RED, Location.NORTH, Arrays.asList(Location.EAST));
		CarTrafficLight lightB = new CarTrafficLight(106, Status.RED, Location.SOUTH, Arrays.asList(Location.WEST));
		assertFalse(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test intersection long curve")
	public void testIntersectionLongCurve()
	{
		CarTrafficLight lightA = new CarTrafficLight(107, Status.RED, Location.SOUTH, Arrays.asList(Location.WEST));
		CarTrafficLight lightB = new CarTrafficLight(110, Status.RED, Location.WEST, Arrays.asList(Location.NORTH));
		assertTrue(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test intersection car")
	public void testInterSectionCar()
	{
		CarTrafficLight lightA = new CarTrafficLight(107, Status.RED, Location.SOUTH, Arrays.asList(Location.WEST));
		CarTrafficLight lightB = new CarTrafficLight(101, Status.RED, Location.NORTH, Arrays.asList(Location.WEST));
		assertTrue(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test no intersection bike")
	public void testNoIntersectionBike()
	{
		BikeTrafficLight lightA = new BikeTrafficLight(301, Status.RED, Location.WEST);
		BikeTrafficLight lightB = new BikeTrafficLight(302, Status.RED, Location.NORTH);
		assertFalse(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test intersection train")
	public void testIntersectionTrain()
	{
		CarTrafficLight lightA = new CarTrafficLight(106, Status.RED, Location.SOUTH, Arrays.asList(Location.WEST));
		TrainCrossingLight lightB = new TrainCrossingLight(601, Status.RED, Location.SOUTH);
		assertTrue(lightA.interferesWith(lightB));
	}

	@Test
	@DisplayName("Test no intersection train")
	public void testNoIntersectionTrain()
	{
		CarTrafficLight lightA = new CarTrafficLight(106, Status.RED, Location.EAST, Arrays.asList(Location.WEST));
		TrainCrossingLight lightB = new TrainCrossingLight(601, Status.RED, Location.SOUTH);
		assertFalse(lightA.interferesWith(lightB));
	}
}
