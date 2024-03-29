using System.Collections.Generic;
using System.Linq;
using Assets.Logic.Traffic;
using UnityEngine;

namespace Assets.Logic.Detector
{
    public class PedestrianDetector : Detector
    {
        protected override List<GameObject> FilterGameObjects(List<GameObject> triggeringGameObjects)
        {
            return triggeringGameObjects.Where(s => s.transform.gameObject.GetComponent<Pedestrian>() != null).ToList();
        }
    }
}