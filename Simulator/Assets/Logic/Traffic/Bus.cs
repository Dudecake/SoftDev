﻿namespace Assets.Logic.Traffic
{
    public class Bus : TrafficObject
    {
        protected override float Speed { get; set; } = 1f;

        protected override void TryMove()
        {
            throw new System.NotImplementedException();
        }
    }
}