using Assets.Logic.Traffic;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class Lane : MonoBehaviour
    {
        public TrafficLight TrafficLight;
        public int MaxQueue;
        public Path[] Paths;
        private string _prevTrafficUpdateString = "";

        public void AddTraffic(TrafficObject trafficObject)
        {
            trafficObject = Instantiate(trafficObject);
            trafficObject.transform.SetParent(this.transform);
            trafficObject.Lane = this;
            trafficObject.PathId = Random.Range(0, Paths.Length);
            trafficObject.transform.localPosition = Paths[trafficObject.PathId].points[0];
        }

        public void OnDetectorUpdate(List<GameObject> triggerinGameObjects)
        {
            TrafficLight.TrafficCount = triggerinGameObjects.Count;
            string trafficUpdateString = JsonConvert.SerializeObject(new TrafficUpdateContainerViewModel { TrafficUpdate = CreateTrafficUpdate(triggerinGameObjects) });
            if (trafficUpdateString != _prevTrafficUpdateString)
            {
                if (_prevTrafficUpdateString != "")
                {
                    Communicator.Instance.Send(trafficUpdateString);
                }
                _prevTrafficUpdateString = trafficUpdateString;
            }
        }

        public bool QueueFull()
        {
            return TrafficLight.TrafficCount >= MaxQueue;
        }

        protected virtual TrafficUpdateViewModel CreateTrafficUpdate(List<GameObject> triggerinGameObjects)
        {
            TrafficUpdateViewModel trafficUpdate = new TrafficUpdateViewModel
            {
                LightId = TrafficLight.Id,
                Count = triggerinGameObjects.Count
            };

            return trafficUpdate;
        }
    }
}