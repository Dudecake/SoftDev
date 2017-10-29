namespace Assets.Logic.ViewModels
{
    public class LightViewModel
    {
        public int Id { get; set; }
        public int Status { get; set; }
    }

    public class LightsViewModel
    {
        public LightViewModel[] Lights { get; set; }
    }
}