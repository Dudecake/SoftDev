using UnityEngine;

namespace Assets.Logic.Traffic
{
    public class Car : TrafficObject
    {
        private void Update()
        {
            if (!this.Leaving && this.ParentLight?.Status == 2)
            {
                StartCoroutine(this.Leave(2));
            }
        }
    }
}