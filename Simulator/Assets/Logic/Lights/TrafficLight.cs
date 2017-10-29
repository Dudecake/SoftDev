using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Assets.Logic.Traffic;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;

namespace Assets.Logic.Lights
{
    public abstract class TrafficLight
    {
        private readonly List<TrafficObject> _trafficObjects = new List<TrafficObject>();

        public int Id { get; private set; }
        public int Status { get; set; }

        protected TrafficLight(int id, int status = 0)
        {
            Id = id;
            Status = status;
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

        public override string ToString()
        {
            TrafficUpdateViewModel o = new TrafficUpdateViewModel()
            {
                LightId = this.Id,
                Count = _trafficObjects.Count,
                //DirectionRequests = _trafficObjects.Where(t => t is Bus).Select(t => ((Bus) t).DirectionRequest).ToArray()
            };

            return JsonConvert.SerializeObject(o);
        }
    }
}
