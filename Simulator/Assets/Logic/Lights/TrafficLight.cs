using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Assets.Logic.Traffic;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class TrafficLight : MonoBehaviour
    {
        private int _trafficCount = 0;
        //private readonly List<TrafficObject> _trafficObjects = new List<TrafficObject>();

        public int Id;
        private SpriteRenderer sr;

        public int Status { get; set; }

        public void SetTrafficCount(int newTrafficCount)
        {
            if (newTrafficCount != this._trafficCount)
            {
                this._trafficCount = newTrafficCount;
                Communicator.Instance.Send(this.ToString());
            }
        }

        public override string ToString()
        {
            TrafficUpdateViewModel o = new TrafficUpdateViewModel()
            {
                LightId = this.Id,
                //Count = _trafficObjects.Count,
                Count = _trafficCount
                //DirectionRequests = _trafficObjects.Where(t => t is Bus).Select(t => ((Bus) t).DirectionRequest).ToArray()
            };

            return JsonConvert.SerializeObject(new TrafficUpdateContainerViewModel {TrafficUpdate = o});
        }

        private void Start()
        {
            sr = gameObject.GetComponent<SpriteRenderer>();
        }

        private void Update()
        {
            switch (Status)
            {
                case 0:
                    this.sr.color = Color.red;
                    break;
                case 1:
                    this.sr.color = Color.yellow;
                    break;
                case 2:
                    this.sr.color = Color.green;
                    break;
            }
        }
    }
}