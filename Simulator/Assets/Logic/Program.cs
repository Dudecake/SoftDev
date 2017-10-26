using Assets.Logic.Traffic;
using UnityEngine;

namespace Assets.Logic
{
    public class Program : MonoBehaviour
    {
        public Intersection Intersection;
        
        public TrafficObject CarPrefab;
        public TrafficObject BusPrefab;
        public TrafficObject BicyclePrefab;
        public TrafficObject PedestrianPrefab;
        public TrafficObject TrainPrefab;

        private void Start()
        {
            if (CarPrefab == null)
            {
                Debug.LogError("Assign the Prefabs in the inspector before resuming");
                UnityEditor.EditorApplication.isPlaying = false;
            }

            this.Intersection.AddTraffic(CarPrefab, 101);
        }
    }
}