package nl.nhl.software_development.controller;

public class Time
{
	private static double timeScale;
	private static double timer;

	public static double getTimeScale()
	{
		return timeScale;
	}

	public static void setTimeScale(double timeScale)
	{
		Time.timeScale = timeScale;
	}

	public static double getTimer()
	{
		return timer;
	}

	private Time()
	{
		// TODO Auto-generated constructor stub
	}

	static void updateTime(double delta)
	{
		timer += (delta * timeScale);
	}

}
