package nl.nhl.software_development.controller.crossing;

public class PedesterianTrafficLightComponent extends TrafficLight
{

	public PedesterianTrafficLightComponent(int id, Status status)
	{
		super(id, status);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean interferesWith(TrafficLight other)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	boolean interferesWith(TrafficLightList others)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
