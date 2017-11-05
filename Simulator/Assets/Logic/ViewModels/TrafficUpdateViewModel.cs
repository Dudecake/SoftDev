namespace Assets.Logic.ViewModels
{
	public class TrafficUpdateContainerViewModel
	{
		public TrafficUpdateViewModel TrafficUpdate { get; set; }
	}
    public class TrafficUpdateViewModel
    {
        public int LightId { get; set; }
        public int Count { get; set; }
        public int[] DirectionRequests { get; set; }
    }
}
