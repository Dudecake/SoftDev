namespace Assets.DataModels
{
    public class SimulatorMessageDataModel
    {
        public class TrafficUpdateDataModel
        {
            public int LightId { get; set; }
            public int Count { get; set; }
            public int[] DirectionRequests { get; set; }
        }

        public TrafficUpdateDataModel TrafficUpdate { get; set; }
    }
}
