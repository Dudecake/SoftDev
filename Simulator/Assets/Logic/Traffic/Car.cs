using UnityEngine;

namespace Assets.Logic.Traffic
{
    public class Car : TrafficObject
    {
        private void Update()
        {
            if (this.ParentLight?.Status == 2)
            {
                float time = Time.time;
            }
        }
    }
}