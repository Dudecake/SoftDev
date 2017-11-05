using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Assets.Logic;
using Assets.Logic.Lights;
using Assets.Logic.Traffic;
using UnityEngine;

public class Lane : MonoBehaviour
{
    private readonly List<TrafficObject> _trafficObjects = new List<TrafficObject>();

    public int Id;

    public TrafficLight TrafficLight;
    public Path[] Paths;

    public void AddTrafficObject(TrafficObject trafficObject)
    {
        trafficObject = Instantiate(trafficObject);
        trafficObject.transform.SetParent(this.transform);
        _trafficObjects.Add(trafficObject);
        trafficObject.Lane = this;
        trafficObject.transform.localPosition = Paths[0].points[0];
    }

    public void RemoveTrafficObject(TrafficObject trafficObject)
    {
        _trafficObjects.Remove(trafficObject);
    }
}