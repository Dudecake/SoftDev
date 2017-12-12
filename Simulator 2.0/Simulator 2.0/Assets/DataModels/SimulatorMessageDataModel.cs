namespace Assets.DataModels
{
    public class SimulatorTrafficUpdateContainerDataModel
    {
        public class TrafficUpdateDataModel
        {
            public int LightId { get; set; }
            public int Count { get; set; }
            public int[] DirectionRequests { get; set; }
        }

        public TrafficUpdateDataModel TrafficUpdate { get; set; }
    }

    public class SpeedViewModel
    {
        public float Speed { get; set; }
    }
}
