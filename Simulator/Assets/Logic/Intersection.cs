using System;
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
        public Lane[] Lanes;

        private void Start()
        {
            Communicator.Instance.AttachReceiver(this.ConsumerOnReceived);
        }

        private void ConsumerOnReceived(object sender, BasicDeliverEventArgs ea)
        {
            string message = Encoding.UTF8.GetString(ea.Body);
            Debug.Log($"Received: {message}");

            LightViewModel[] lights = JsonConvert.DeserializeObject<LightsViewModel>(message).Lights;

            this.UpdateLights(lights);
        }

        public void AddTraffic(TrafficObject trafficObject, int laneId)
        {
            Lanes.FirstOrDefault(tl => tl.Id == laneId)?.AddTrafficObject(trafficObject);
        }

        private void UpdateLights(LightViewModel[] lights)
        {
            foreach (LightViewModel lv in lights)
            {
                IEnumerable<TrafficLight> tLights = this.Lanes.Where(l => l.TrafficLight.Id == lv.Id).Select(l => l.TrafficLight);
                foreach (TrafficLight tLight in tLights)
                {
                    tLight.Status = lv.Status;
                }
            }
        }
    }
}