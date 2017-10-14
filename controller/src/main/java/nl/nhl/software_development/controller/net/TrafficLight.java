package nl.nhl.software_development.controller.net;

public class TrafficLight
{
    enum State
    {
        RED, ORANGE, GREEN
    }
    @SerializedName("Id")
    private int id;
    @SerializedName("State")
    private State state;

    public TrafficLight()
    {
        id = 101;
        state = State.RED;
    }

    public  TrafficLight(int id, State state)
    {
        this.id = id;
        this.state = state;
    }
}
