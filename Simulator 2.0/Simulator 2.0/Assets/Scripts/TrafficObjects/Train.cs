using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public class Train : TrafficObject
    {
        //private void Update()
        //{
        //    if (TargetIsLight)
        //    {
        //        Target.TrafficLight.AddTraffic(this);
        //    }
        //    else
        //    {
        //        Target.TrafficLight.RemoveTraffic(this);
        //    }
        //}

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

        protected override void RemoveTraffic() { }

        protected override void AddTraffic() { }
    }
}