using System.Collections.Generic;
using System.Linq;
using Assets.DataModels;
using Assets.Scripts.TrafficObjects;
using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public abstract class TrafficLight : MonoBehaviour
    {
        protected readonly Dictionary<TrafficObject, int> QueuedTraffic = new Dictionary<TrafficObject, int>();

        public int Id;
        public virtual int Status { get; set; } = 0;

        private void Update()
        {
            UpdateLightVisualization();
        }

        public void AddTraffic(TrafficObject to, int directionRequest = -1)
        {
            if (!QueuedTraffic.ContainsKey(to))
            {
                QueuedTraffic.Add(to, directionRequest);
                OnQueuedTrafficUpdate();
            }
        }

        public void RemoveTraffic(TrafficObject to)
        {
            if (QueuedTraffic.Remove(to))
            {
                OnQueuedTrafficUpdate();
            }
        }

        public abstract void UpdateLightVisualization();

        private void OnQueuedTrafficUpdate()
        {
            bool containsDirectionRequests = QueuedTraffic.Values.Any(v => v >= 0);

            SimulatorTrafficUpdateContainerDataModel trafficUpdate = new SimulatorTrafficUpdateContainerDataModel
            {
                TrafficUpdate = new SimulatorTrafficUpdateContainerDataModel.TrafficUpdateDataModel
                {
                    LightId = Id,
                    Count = QueuedTraffic.Count
                }
            };

            if (containsDirectionRequests)
            {
                trafficUpdate.TrafficUpdate.DirectionRequests = QueuedTraffic.Values.ToArray();
            }

            IntersectionController.Communicator.Send(trafficUpdate);
        }
    }
}