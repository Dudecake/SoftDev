using System;

namespace Assets.Scripts.Lanes
{
    public class BusPath : Path
    {
        public enum Direction
        {
            Undefined = 0,
            StraightAhead = 2,
            Left = 3,
            Right = 4
        }

        public Direction PathDirection;

        public bool AllowedWithStatus(int status)
        {
            switch (PathDirection)
            {
                case Direction.StraightAhead:
                    return status == 2 || status == 5 || status == 6 || status == 8;
                case Direction.Left:
                    return status == 3 || status == 5 || status == 7 || status == 8;
                case Direction.Right:
                    return status == 4 || status == 6 || status == 7 || status == 8;
                default:
                    throw new Exception("PathDirection undefined");
            }
        }
    }
}