using System.Collections;
using UnityEngine;
using Assets.Logic.Lights;
using UnityEditor;

namespace Assets.Logic.Traffic
{
    public abstract class TrafficObject : MonoBehaviour
    {
        public Lane Lane { get; set; }
        public int PathPointIndex { get; set; }

        private void Awake()
        {
            Debug.Log(name + " spawned");
        }

        private void Update()
        {
            if (Vector2.Distance(Lane.Paths[0].points[1], this.transform.position) < 1 &&
                !Lane.TrafficLight.ContainsTrafficObject(this))
            {
                Lane.TrafficLight.AddTrafficObject(this);
            }
            else if (Vector2.Distance(Lane.Paths[0].points[1], this.transform.position) >= 1 &&
                     Lane.TrafficLight.ContainsTrafficObject(this))
            {
                Lane.TrafficLight.RemoveTrafficObject(this);
            }

            if (PathPointIndex != 1 ||
                PathPointIndex == 1 &&
                Vector2.Distance(Lane.Paths[0].points[PathPointIndex], this.transform.position) > 1 ||
                Lane.TrafficLight.Status == 2)
            {
                float step = 2 * Time.deltaTime;
                transform.position = Vector2.MoveTowards(transform.position, Lane.Paths[0].points[PathPointIndex + 1],
                    step);
                if ((Vector2) transform.position == Lane.Paths[0].points[PathPointIndex + 1])
                {
                    PathPointIndex++;
                    if (PathPointIndex == Lane.Paths[0].points.Length - 1)
                    {
                        Lane.RemoveTrafficObject(this);
                        Destroy(this.gameObject);
                    }
                }
            }
        }
    }
}