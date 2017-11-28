namespace Assets.Logic.Traffic
{
    public class Pedestrian : TrafficObject
    {
        protected override float Speed { get; set; } = 0.15f;

        protected override void TryMove()
        {
            throw new System.NotImplementedException();
        }
    }
}