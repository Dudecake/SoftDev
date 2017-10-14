package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Crossing
{
    @SerializedName("Lights")
    private List<TrafficLight> lights;
    @SerializedName("Speed")
    private double timeScale;

    public Crossing()
    {
        lights = new ArrayList<>();
        // Add car traffic lights
        for (int i = 101; i < 111; i++) lights.add(new TrafficLight(i, TrafficLight.State.RED));
        // Add bike traffic lights
        for (int i = 301; i < 306; i++) lights.add(new TrafficLight(i, TrafficLight.State.RED));
        // Add pedestrian traffic lights
        for (int i = 401; i < 407; i++) lights.add(new TrafficLight(i, TrafficLight.State.RED));
        // Add train traffic light
        lights.add(new TrafficLight(501, TrafficLight.State.RED));
        timeScale = 1.0;
    }
}
