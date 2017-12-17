using Assets.Scripts.TrafficLights;
using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public class Train : TrafficObject
    {
        //Index of the PathPoint goal for when the train should signal its TrafficLight (501 or 502) that it's no longer queued there
        public int PassedCrossingPathPoint { get; set; } = -1;

        private TrafficLight _parentLight;

        private void Start()
        {
            if (PassedCrossingPathPoint == -1)
            {
                Debug.LogError("Dont forget to set the PassedCrossingPathPoint on Trains");
                UnityEditor.EditorApplication.isPlaying = false;
            }
        }

        private void Update()
        {
            if (TargetIsLight)
            {
                Target.TrafficLight.AddTraffic(this);
                _parentLight = Target.TrafficLight;
            }
            else
            {
                if (_parentLight != null)
                {
                    if (PassedCrossingPathPoint == -1) // Fallback for when the PassedCrossingPathPoint is not set
                    {
                        _parentLight.RemoveTraffic(this);
                    }
                    else
                    {
                        if (PathPointIndex == PassedCrossingPathPoint)
                        {
                            _parentLight.RemoveTraffic(this);
                        }
                    }
                }
            }
        }

        public bool IgnoreLight { get; set; } = false;

        protected override void Move()
        {
            Vector3 targetDir = Lane.Paths[PathIndex].PathPoints[PathPointIndex].Point - transform.position;
            float step = Speed * Time.deltaTime;
            Vector3 newDir = Vector3.RotateTowards(transform.forward, targetDir, step, 0.0F);

            Rb.MoveRotation(Quaternion.LookRotation(newDir));
            Rb.MovePosition(transform.position + (Target.Point - transform.position).normalized * step);
        }

        protected override bool LightIsGreen()
        {
            return IgnoreLight || Target.TrafficLight == null || Target.TrafficLight.Status == 2;
        }

        protected override bool SweepTestFront()
        {
            return false; // Train shouldnt check for obstacles
        }

        protected override void RemoveTraffic()
        {
        }

        protected override void AddTraffic()
        {
        }
    }
}