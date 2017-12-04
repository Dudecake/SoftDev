namespace Assets.Logic.ViewModels
{
    public class LightViewModel
    {
        public int Id { get; set; }
        public int Status { get; set; }
        public int Time { get; set; } = -1; // Indicator for when traffic can go through the light in seconds
    }

    public class ControllerMessage
    {
        public LightViewModel[] Lights { get; set; }
        public float Speed { get; set; } = -1;
    }
}