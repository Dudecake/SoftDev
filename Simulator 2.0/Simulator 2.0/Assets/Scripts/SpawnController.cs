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

        [Range(0.1f, 10)] public float SpawnStartDelay;
        [Range(0.2f, 10)] public float SpawnInterval;
        private float _lastSpawn;
        
        [Range(0, 100)] public int CarSpawnWeight;
        [Range(0, 100)] public int BusSpawnWeight;
        [Range(0, 100)] public int BicycleSpawnWeight;
        [Range(0, 100)] public int PedestrianSpawnWeight;
        [Range(10, 1200)] public int FromEastTrainSpawnInterval = 60;
        [Range(10, 1200)] public int FromWestTrainSpawnInterval = 90;
        public TrafficObject[] CarPrefabs;
        public TrafficObject[] BusPrefabs;
        public TrafficObject[] BicyclePrefabs;
        public TrafficObject[] PedestrianPrefabs;
        public TrafficObject TrainPrefab;

        private const int FromEastTrainLaneId = 502;
        private const int FromWestTrainLaneId = 501;
        private float _lastFromEastTrainSpawn;
        private float _lastFromWestTrainSpawn;
        

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

            TrainSpawn();
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
                {PrefabType.Pedestrian, PedestrianSpawnWeight}
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

        private void TrainSpawn()
        {
            if (_lastFromEastTrainSpawn + FromEastTrainSpawnInterval < Time.time)
            {
                _lastFromEastTrainSpawn = Time.time;
                
                Lane lane = _lanes.FirstOrDefault(l => l.LaneId == FromEastTrainLaneId);
                if (lane != null)
                {
                    TrafficObject trafficObject = Instantiate(TrainPrefab);
                    trafficObject.Lane = lane;
                    trafficObject.PathIndex = Random.Range(0, lane.Paths.Length);
                    trafficObject.transform.position = lane.Paths[trafficObject.PathIndex].PathPoints[0].Point;
                    trafficObject.transform.LookAt(lane.Paths[trafficObject.PathIndex].PathPoints[1].Point);
                    ((Train) trafficObject).IgnoreLight = true;
                    ((Train) trafficObject).PassedCrossingPathPoint = 3;
                }

            }

            if (_lastFromWestTrainSpawn + FromWestTrainSpawnInterval < Time.time)
            {
                _lastFromWestTrainSpawn = Time.time;

                Lane lane = _lanes.FirstOrDefault(l => l.LaneId == FromWestTrainLaneId);
                if (lane != null)
                {
                    TrafficObject trafficObject = Instantiate(TrainPrefab);
                    trafficObject.Lane = lane;
                    trafficObject.PathIndex = Random.Range(0, lane.Paths.Length);
                    trafficObject.transform.position = lane.Paths[trafficObject.PathIndex].PathPoints[0].Point;
                    trafficObject.transform.LookAt(lane.Paths[trafficObject.PathIndex].PathPoints[1].Point);
                    ((Train)trafficObject).PassedCrossingPathPoint = 3;
                }
            }
        }

        public enum PrefabType
        {
            Car,
            Bus,
            Bicycle,
            Pedestrian
        }
    }
}