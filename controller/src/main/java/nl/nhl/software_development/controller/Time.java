package nl.nhl.software_development.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Time
{
	private static double timeScale = 1.0;
	private static Duration time = Duration.ZERO;
	private static LocalDateTime lastUpdate = LocalDateTime.now();

	public static double getTimeScale()
	{
		return timeScale;
	}

	public static void setTimeScale(double timeScale)
	{
		Time.timeScale = timeScale;
	}

	public static Duration getTime()
	{
		return time;
	}

	private Time()
	{
		// Private constructor
	}

	static void updateTime()
	{
		LocalDateTime updateTime = LocalDateTime.now();
		time = time.plus((long)(Duration.between(lastUpdate, updateTime).toMillis() * timeScale), ChronoUnit.MILLIS);
		lastUpdate = updateTime;
	}
}
