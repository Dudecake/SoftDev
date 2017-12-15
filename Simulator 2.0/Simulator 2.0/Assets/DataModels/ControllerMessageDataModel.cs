namespace Assets.DataModels
{
    public class ControllerMessageDataModel
    {
        public class LightDataModel
        {
            public int Id { get; set; }
            public int Status { get; set; }
            public int Time { get; set; } = -1; // Indicitor for when the lights goes to green in seconds
        }

        public LightDataModel[] Lights { get; set; }
        public float Speed { get; set; } = -1;
    }
}