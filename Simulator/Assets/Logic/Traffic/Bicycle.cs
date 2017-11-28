namespace Assets.Logic.Traffic
{
    public class Bicycle : TrafficObject
    {
        protected override float Speed { get; set; } = 0.4f;

        protected override void TryMove()
        {
            throw new System.NotImplementedException();
        }
    }
}