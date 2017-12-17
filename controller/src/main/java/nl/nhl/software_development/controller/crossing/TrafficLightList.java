package nl.nhl.software_development.controller.crossing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.nhl.software_development.controller.crossing.TrafficLight.Status;

class TrafficLightList extends ArrayList<TrafficLight>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3485823910488651024L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TrafficLightList.class);

	public TrafficLight getId(int lightId)
	{
		List<TrafficLight> resList = this.parallelStream().filter(l -> l.getId() == lightId).collect(Collectors.toList());
		if (resList.isEmpty())
		{
			throw new NullPointerException(String.format("TrafficLight with id %s is not in the TrafficLightList", lightId));
		}
		return resList.get(0);
	}

	void watchDogReset()
	{
		LOGGER.warn("Doing watchdog reset");
		this.parallelStream().forEach(l -> l.setStatus(Status.RED));
	}

	public List<TrainTrafficLight> getAllTrainLights()
	{
		return this.parallelStream().filter(TrainTrafficLight.class::isInstance).map(TrainTrafficLight.class::cast).collect(Collectors.toList());
	}
}
