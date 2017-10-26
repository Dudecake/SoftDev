using System;
using System.Collections;
using System.Collections.Generic;
using Assets.Logic.Traffic;
using Assets.Logic.Lights;
using Assets.Logic.ViewModels;
using UnityEngine;

namespace Assets.Logic
{
    public class Intersection : MonoBehaviour
    {
        private readonly Dictionary<int, TrafficLight> _trafficLights = Constants.lights;

        public void AddTraffic(TrafficObject trafficObject, int lightId)
        {
            StartCoroutine(AttachToLight(Instantiate(trafficObject), _trafficLights[lightId], 3));
        }

        private IEnumerator AttachToLight(TrafficObject trafficObject, TrafficLight trafficLight, float afterSeconds)
        {
            yield return new WaitForSeconds(afterSeconds);
            trafficObject.ParentLight = trafficLight;
            trafficLight.AddTrafficObject(trafficObject);
        }

        public void UpdateLights(LightViewModel[] lights)
        {
            foreach (LightViewModel l in lights)
            {
                if (this._trafficLights.ContainsKey(l.Id))
                {
                    this._trafficLights[l.Id].Status = l.Status;
                }
            }
        }
    }
}