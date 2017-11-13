using System.Collections;
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

            InvokeRepeating("RepeatingFunction", 1, 0.5f);

            //Vector2 v2_1 = new Vector2(0, 0);
            //Vector2 v2_2 = new Vector2(2, 2);
            //float angleS = AngleBetweenVector2(v2_1, v2_2);
            //Debug.Log(angleS);
        }

        

        private void RepeatingFunction()
        {
            this.Intersection.AddTraffic(CarPrefab, Random.Range(101, 110));
        }
    }
}