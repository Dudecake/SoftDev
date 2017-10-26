namespace Assets.Logic.Traffic
{
    public class Bus : TrafficObject
    {
        public int DirectionRequest { get; private set; }

        public Bus(int directionRequest)
        {
            this.DirectionRequest = directionRequest;
        }
    }
}