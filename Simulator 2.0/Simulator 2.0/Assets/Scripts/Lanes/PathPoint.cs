using Assets.Scripts.TrafficLights;
using UnityEngine;

namespace Assets.Scripts.Lanes
{
    [System.Serializable]
    public class PathPoint
    {
        public Vector3 Point;
        public TrafficLight TrafficLight;
    }
}