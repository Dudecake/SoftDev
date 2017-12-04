using Assets.Scripts.Lanes;
using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public class Bus : TrafficObject
    {
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
            BusPath busPath = Lane.Paths[PathIndex] as BusPath;

            if (busPath != null) // Driving on a buslane
            {
                return Target.TrafficLight == null || busPath.AllowedWithStatus(Target.TrafficLight.Status);
            }
            else
            {
                return Target.TrafficLight == null || Target.TrafficLight.Status == 2;
            }
            
        }

        protected override bool SweepTestFront()
        {
            RaycastHit hit;
            return Rb.SweepTest(transform.forward, out hit, 0.5f);
        }

        protected override void AddTraffic()
        {
            BusPath busPath = Lane.Paths[PathIndex] as BusPath;

            if (busPath != null)
            {
                Target.TrafficLight.AddTraffic(this, busPath.PathDirection.GetHashCode());
            }
            else
            {
                Target.TrafficLight.AddTraffic(this);
            }
            
        }
    }
}