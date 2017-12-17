using Assets.Scripts.Lanes;
using UnityEngine;

namespace Assets.Scripts.TrafficObjects
{
    public class Pedestrian : TrafficObject
    {
        [Range(0, 1)]
        public float MaxPathPointShift;
        public BloodSplatter BloodSplatterPrefab;

        private float _pathPointShift;

        private void Start()
        {
            _pathPointShift = Random.Range(-MaxPathPointShift, MaxPathPointShift);
        }

        protected override void Move()
        {
            Vector3 targetDir = ShiftedTarget(Target.Point) - transform.position;
            float step = Speed * Time.deltaTime;
            Vector3 newDir = Vector3.RotateTowards(transform.forward, targetDir, step, 0.0F);

            Rb.MoveRotation(Quaternion.LookRotation(newDir));
            Rb.MovePosition(transform.position + (ShiftedTarget(Target.Point) - transform.position).normalized * step);
        }

        protected override bool LightIsGreen()
        {
            return Target.TrafficLight == null || Target.TrafficLight.Status == 2;
        }

        protected override bool SweepTestFront()
        {
            return false; //Pedestrian doesnt queue
        }

        protected override Vector3 ShiftedTarget(Vector3 target)
        {
            return target + transform.right * _pathPointShift;
        }

        private void OnTriggerEnter(Collider other)
        {
            if (!(other.attachedRigidbody.mass > Rb.mass * 5)) return;
            
            if (BloodSplatterPrefab != null)
            {
                BloodSplatter newBloodSplatter = Instantiate(BloodSplatterPrefab);
                newBloodSplatter.transform.position = new Vector3(transform.position.x, 0.01f, transform.position.z);
            }

            Target.TrafficLight?.RemoveTraffic(this);
            Destroy(gameObject);
        }
    }
}