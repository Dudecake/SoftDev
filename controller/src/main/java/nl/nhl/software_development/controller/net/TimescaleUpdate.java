package nl.nhl.software_development.controller.net;

public class TimescaleUpdate
{
	private double speed;

	public double getSpeed()
	{
		return speed;
	}

	public TimescaleUpdate()
	{
		speed = 1.0;
	}

	TimescaleUpdate(double speed)
	{
		this.speed = speed;
	}
}
