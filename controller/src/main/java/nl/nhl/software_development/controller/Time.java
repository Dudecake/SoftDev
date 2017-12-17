package nl.nhl.software_development.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Time
{
	private static double timeScale = 1.0;
	private static Duration time = Duration.ofSeconds(15);
	private static Duration resetTime = Duration.ofMinutes(1);
	private static LocalDateTime lastUpdate = LocalDateTime.now();

	public static double getTimeScale()
	{
		return timeScale;
	}

	public static void setTimeScale(double timeScale)
	{
		Time.timeScale = timeScale;
	}

	public static TimeAck getTimeScaleAck()
	{
		return new TimeAck(timeScale);
	}

	public static Duration getTime()
	{
		return time;
	}

	public static boolean needsFullReset()
	{
		return resetTime.compareTo(time) < 0;
	}

	static void watchDog()
	{
		resetTime = time.plus(Duration.ofMinutes(1));
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

	public static class TimeAck
	{
		@SuppressWarnings("unused")
		private Double speed;

		public TimeAck(Double speed)
		{
			this.speed = speed;
		}
	}
}
