using Assets.Scripts.Lanes;
using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public abstract class TrafficObject : MonoBehaviour
    {
        public Lane Lane { get; set; }
        public int PathIndex { get; set; }

        protected int PathPointIndex = 1;
        protected Rigidbody Rb;

        [Range(0.5f, 5f)] public float Speed;

        private void Awake()
        {
            Rb = GetComponent<Rigidbody>();
        }

        private void FixedUpdate()
        {
            if (TargetIsLast && CloseToTarget)
            {
                Destroy(gameObject);
                return;
            }
            if (TargetIsLight && CloseToTarget && LightIsGreen())
            {
                RemoveTraffic();
                PathPointIndex++;
            }
            if (!TargetIsLight && CloseToTarget)
            {
                PathPointIndex++;
            }
            if (TargetIsLight && CloseToTarget && !LightIsGreen() || TargetIsLight && SweepTestFront())
            {
                AddTraffic();
                return;
            }
            Move();
        }

        protected PathPoint Target => Lane.Paths[PathIndex].PathPoints[PathPointIndex];
        protected bool TargetIsLight => Target.TrafficLight != null;
        protected bool TargetIsLast => PathPointIndex >= Lane.Paths[PathIndex].PathPoints.Length - 1;
        protected bool CloseToTarget => Vector3.Distance(transform.position, ShiftedTarget(Target.Point)) < 0.1f;

        protected abstract void Move();
        protected abstract bool LightIsGreen();
        protected abstract bool SweepTestFront();

        protected virtual void RemoveTraffic()
        {
            Target.TrafficLight.RemoveTraffic(this);
        }
        protected virtual void AddTraffic()
        {
            Target.TrafficLight.AddTraffic(this);
        }

        protected virtual Vector3 ShiftedTarget(Vector3 target)
        {
            return target;
        }
    }
}