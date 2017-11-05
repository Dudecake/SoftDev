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
        private readonly List<TrafficObject> _trafficObjects = new List<TrafficObject>();

        public int Id;
        private int _status;
        private SpriteRenderer sr;

        public int Status
        {
            get { return _status; }
            set
            {
                _status = value;
                this.OnStatusChange(value);
            }
        }

        public void AddTrafficObject(TrafficObject trafficObject)
        {
            _trafficObjects.Add(trafficObject);
            Communicator.Instance.Send(this.ToString());
        }

        public void RemoveTrafficObject(TrafficObject trafficObject)
        {
            _trafficObjects.Remove(trafficObject);
            Communicator.Instance.Send(this.ToString());
        }

        public bool ContainsTrafficObject(TrafficObject trafficObject)
        {
            return _trafficObjects.Contains(trafficObject);
        }

        public override string ToString()
        {
            TrafficUpdateViewModel o = new TrafficUpdateViewModel()
            {
                LightId = this.Id,
                Count = _trafficObjects.Count,
                //DirectionRequests = _trafficObjects.Where(t => t is Bus).Select(t => ((Bus) t).DirectionRequest).ToArray()
            };

            return JsonConvert.SerializeObject(new TrafficUpdateContainerViewModel {TrafficUpdate = o});
        }

        private void Start()
        {
            sr = gameObject.GetComponent<SpriteRenderer>();
        }

        private void OnStatusChange(int status)
        {
            switch (status)
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