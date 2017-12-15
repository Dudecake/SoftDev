using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Assets.Logic.Traffic;
using Assets.Logic.Lights;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using RabbitMQ.Client.Events;
using UnityEngine;

namespace Assets.Logic
{
    public class Intersection : MonoBehaviour
    {
        public float SpawnStartDelay;
        public float SpawnInterval;

        public Lane[] RegularLanes;
        public Lane[] BusLanes;
        public Lane[] BicycleLanes;
        public Lane[] PedestrianLanes;

        public TrafficObject[] CarPrefabs;
        public TrafficObject[] BusPrefabs;
        public TrafficObject[] BicyclePrefabs;
        public TrafficObject[] PedestrianPrefabs;

        private void Start()
        {
            Application.runInBackground = true;

            if (!CarPrefabs.Any() || !BusPrefabs.Any() || !BicyclePrefabs.Any() || !PedestrianPrefabs.Any())
            {
                Debug.LogError("Assign the traffic prefabs in the inspector before resuming");
                UnityEditor.EditorApplication.isPlaying = false;
            }

            Communicator.Instance.AttachReceiver(this.ConsumerOnReceived);

            InvokeRepeating("SpawnRandomTraffic", SpawnStartDelay, SpawnInterval);
        }

        private void SpawnRandomTraffic()
        {
            int randomNumber = Random.Range(0, 100);
            if (randomNumber < 50) //Car
            {
                TrafficObject trafficObject = CarPrefabs[Random.Range(0, CarPrefabs.Length)];
                Lane lane = RegularLanes[Random.Range(0, RegularLanes.Length)];
                if (!lane.QueueFull())
                {
                    lane.AddTraffic(trafficObject);
                }
            }
            else if (randomNumber < 60) //Bus
            {
                TrafficObject trafficObject = BusPrefabs[Random.Range(0, BusPrefabs.Length)];
                Lane lane = BusLanes[Random.Range(0, BusLanes.Length)];
                if (!lane.QueueFull())
                {
                    lane.AddTraffic(trafficObject);
                }
            }
            else if (randomNumber < 80) //Bicycle
            {
                TrafficObject trafficObject = BicyclePrefabs[Random.Range(0, BicyclePrefabs.Length)];
                Lane lane = BicycleLanes[Random.Range(0, BicycleLanes.Length)];
                if (!lane.QueueFull())
                {
                    lane.AddTraffic(trafficObject);
                }
            }
            else //Pedestrian
            {
                TrafficObject trafficObject = PedestrianPrefabs[Random.Range(0, PedestrianPrefabs.Length)];
                Lane lane = PedestrianLanes[Random.Range(0, PedestrianLanes.Length)];
                if (!lane.QueueFull())
                {
                    lane.AddTraffic(trafficObject);
                }
            }
        }

        private void ConsumerOnReceived(object sender, BasicDeliverEventArgs ea)
        {
            string message = Encoding.UTF8.GetString(ea.Body);
            Debug.Log($"Received: {message}");

            LightViewModel[] lights = JsonConvert.DeserializeObject<ControllerMessage>(message)?.Lights;

            this.UpdateLights(lights);
        }

        private void UpdateLights(LightViewModel[] lights)
        {
            foreach (LightViewModel lv in lights)
            {
                IEnumerable<TrafficLight> tLights = this.RegularLanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight);
                tLights = tLights.Concat(this.BusLanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight));
                tLights = tLights.Concat(this.BicycleLanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight));
                tLights = tLights.Concat(this.PedestrianLanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight));
                foreach (TrafficLight tLight in tLights)
                {
                    if (tLight.Status != lv.Status)
                    {
                        Debug.Log("Changed " + tLight.Id + " to " + lv.Status);
                    }
                    tLight.Status = lv.Status;
                    tLight.Time = lv.Time;
                }
            }
        }
    }
}