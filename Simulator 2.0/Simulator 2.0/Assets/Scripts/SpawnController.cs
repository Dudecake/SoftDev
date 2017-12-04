using System;
using System.Collections.Generic;
using System.Linq;
using Assets.Scripts.Lanes;
using Assets.Scripts.TrafficObjects;
using UnityEngine;
using Random = UnityEngine.Random;

namespace Assets.Scripts
{
    public class SpawnController : MonoBehaviour
    {
        private Lane[] _lanes;

        public float SpawnStartDelay;
        public float SpawnInterval;
        private float _lastSpawn;

        public int CarSpawnWeight;
        public TrafficObject[] CarPrefabs;
        public int BusSpawnWeight;
        public TrafficObject[] BusPrefabs;
        public int BicycleSpawnWeight;
        public TrafficObject[] BicyclePrefabs;
        public int PedestrianSpawnWeight;
        public TrafficObject[] PedestrianPrefabs;
        //public int TrainSpawnWeight;
        //public TrafficObject[] TrainPrefabs;

        private void Start()
        {
            _lanes = GetComponentsInChildren<Lane>();
        }

        private void Update()
        {
            if (Time.time > SpawnStartDelay && (_lastSpawn < float.Epsilon || Time.time - SpawnInterval > _lastSpawn))
            {
                RandomSpawn();
                _lastSpawn = Time.time;
            }
        }

        private void RandomSpawn()
        {
            PrefabType randomPrefabType = RandomPrefabType();

            switch (randomPrefabType)
            {
                case PrefabType.Car:
                    Spawn(CarPrefabs, 100);
                    break;
                case PrefabType.Bus:
                    Spawn(BusPrefabs, 200);
                    break;
                case PrefabType.Bicycle:
                    Spawn(BicyclePrefabs, 300);
                    break;
                case PrefabType.Pedestrian:
                    Spawn(PedestrianPrefabs, 400);
                    break;
                //case PrefabType.Train:
                //    Spawn(TrainPrefabs, 500);
                //    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }
        }

        private PrefabType RandomPrefabType()
        {
            Dictionary<PrefabType, int> weightedPrefabTypes = new Dictionary<PrefabType, int>
            {
                {PrefabType.Car, CarSpawnWeight},
                {PrefabType.Bus, BusSpawnWeight},
                {PrefabType.Bicycle, BicycleSpawnWeight},
                {PrefabType.Pedestrian, PedestrianSpawnWeight},
                //{PrefabType.Train, TrainSpawnWeight }
            };

            int totalWeight = weightedPrefabTypes.Values.Sum();
            int choice = Random.Range(0, totalWeight);
            int sum = 0;
            foreach (KeyValuePair<PrefabType, int> obj in weightedPrefabTypes)
            {
                for (int i = sum; i < obj.Value + sum; i++)
                {
                    if (i >= choice)
                    {
                        return obj.Key;
                    }
                }
                sum += obj.Value;
            }
            return weightedPrefabTypes.First().Key;
        }

        private void Spawn(TrafficObject[] trafficObjectPrefabs, int laneIdStart)
        {
            TrafficObject trafficObject =
                Instantiate(trafficObjectPrefabs[Random.Range(0, trafficObjectPrefabs.Length)]);

            Lane[] lanes = _lanes.Where(l => l.LaneId >= laneIdStart && l.LaneId < laneIdStart + 100).ToArray();
            Lane lane = lanes[Random.Range(0, lanes.Length)];

            trafficObject.Lane = lane;
            trafficObject.PathIndex = Random.Range(0, lane.Paths.Length);
            trafficObject.transform.position = lane.Paths[trafficObject.PathIndex].PathPoints[0].Point;
            trafficObject.transform.LookAt(lane.Paths[trafficObject.PathIndex].PathPoints[1].Point);
        }

        public enum PrefabType
        {
            Car,
            Bus,
            Bicycle,
            Pedestrian,
            //Train
        }
    }
}