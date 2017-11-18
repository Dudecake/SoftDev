using System.Collections.Generic;
using System.Linq;
using Assets.Logic.Traffic;
using Assets.Logic.ViewModels;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class BusLane : Lane {
        protected override TrafficUpdateViewModel CreateTrafficUpdate(List<GameObject> triggerinGameObjects)
        {
            TrafficUpdateViewModel trafficUpdate = base.CreateTrafficUpdate(triggerinGameObjects);

            List<GameObject> orderedTriggeringGameObjects = triggerinGameObjects.OrderBy(s => Vector3.Distance(this.TrafficLight.transform.position, s.transform.position)).ToList();

            trafficUpdate.DirectionRequests = new int[orderedTriggeringGameObjects.Count];
            for (int i = 0; i < orderedTriggeringGameObjects.Count; i++)
            {
                TrafficObject bus = orderedTriggeringGameObjects[i].gameObject.GetComponent<Bus>();
                trafficUpdate.DirectionRequests[i] = this.Paths[bus.PathId].BusDirectionId;
            }

            return trafficUpdate;
        }
    }
}
