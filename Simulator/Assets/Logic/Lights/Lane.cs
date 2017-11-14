using Assets.Logic.Traffic;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class Lane : MonoBehaviour
    {
        public int Id;

        public TrafficLight TrafficLight;
        public Path[] Paths;

        private int _trafficCount = 0;

        public void AddTrafficObject(TrafficObject trafficObject)
        {
            trafficObject = Instantiate(trafficObject);
            trafficObject.transform.SetParent(this.transform);
            trafficObject.Lane = this;
            trafficObject.transform.localPosition = Paths[0].points[0];
        }

        public void Update()
        {
            Vector2 heading = Paths[0].points[1] - Paths[0].points[0];
            float distance = heading.magnitude;
            Vector2 direction = heading / distance; // This is now the normalized direction.

            RaycastHit[] hits = Physics.RaycastAll(Paths[0].points[0], direction, distance);
            TrafficLight.SetTrafficCount(hits.Length);
        }
    }
}