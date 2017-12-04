namespace Assets.Logic.Traffic
{
    public class Train : TrafficObject
    {
        protected override float Speed { get; set; } = 2f;
        protected override float CloseToLightDistance { get; set; } = 0.5f;
        protected override float RandomLocationShiftY { get; set; } = 0;
        protected override float RandomLocationShiftX { get; set; } = 0;

        protected override void TryMove()
        {
            throw new System.NotImplementedException();
        }
    }
}