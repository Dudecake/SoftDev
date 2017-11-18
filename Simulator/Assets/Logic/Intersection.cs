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

        public TrafficObject[] CarPrefabs;
        public TrafficObject[] BusPrefabs;

        private void Start()
        {
            Application.runInBackground = true;

            if (CarPrefabs.Length == 0)
            {
                Debug.LogError("Assign the Prefabs in the inspector before resuming");
                UnityEditor.EditorApplication.isPlaying = false;
            }

            Communicator.Instance.AttachReceiver(this.ConsumerOnReceived);

            InvokeRepeating("SpawnRandomTraffic", SpawnStartDelay, SpawnInterval);
        }

        private void SpawnRandomTraffic()
        {
            int randomNumber = Random.Range(0, 100);
            if (randomNumber < 100) //Car
            {
                TrafficObject trafficObject = CarPrefabs[Random.Range(0, CarPrefabs.Length)];
                Lane lane = RegularLanes[Random.Range(0, RegularLanes.Length)];
                if (!lane.QueueFull())
                {
                    lane.AddTraffic(trafficObject);
                }
            }
            else //Bus
            {
                TrafficObject trafficObject = BusPrefabs[Random.Range(0, BusPrefabs.Length)];
                Lane lane = BusLanes[Random.Range(0, BusLanes.Length)];
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

            LightViewModel[] lights = JsonConvert.DeserializeObject<LightsViewModel>(message).Lights;

            this.UpdateLights(lights);
        }

        private void UpdateLights(LightViewModel[] lights)
        {
            foreach (LightViewModel lv in lights)
            {
                IEnumerable<TrafficLight> tLights = this.RegularLanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight);
                foreach (TrafficLight tLight in tLights)
                {
                    tLight.Status = lv.Status;
                }
            }
        }
    }
}