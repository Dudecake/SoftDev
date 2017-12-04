using System;
using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public class Car : TrafficObject
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
            return Target.TrafficLight == null || Target.TrafficLight.Status == 2;
        }

        protected override bool SweepTestFront()
        {
            RaycastHit hit;
            return Rb.SweepTest(transform.forward, out hit, 0.5f);
        }
    }
}