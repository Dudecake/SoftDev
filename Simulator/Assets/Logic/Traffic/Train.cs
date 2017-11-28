namespace Assets.Logic.Traffic
{
    public class Train : TrafficObject
    {
        protected override float Speed { get; set; } = 2f;

        protected override void TryMove()
        {
            throw new System.NotImplementedException();
        }
    }
}