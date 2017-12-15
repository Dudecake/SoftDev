using System.Collections;
using UnityEngine;
using Assets.Logic.Lights;
using UnityEditor;

namespace Assets.Logic.Traffic
{
    public abstract class TrafficObject : MonoBehaviour
    {
        public Lane Lane { get; set; }
        public int PathId { get; set; }

        protected int PathPointIndex = 1;
        protected Rigidbody Rb;

        protected abstract float Speed { get; set; }
        protected abstract float CloseToLightDistance { get; set; }
        protected abstract float RandomLocationShiftY { get; set; }
        protected abstract float RandomLocationShiftX { get; set; }

        private void Awake()
        {
            Debug.Log(name + " spawned");
        }

        private void Update()
        {
            this.TryMove();
        }

        private void Start()
        {
            Rb = GetComponent<Rigidbody>();
        }

        protected static float AngleBetweenVector2(Vector2 vec1, Vector2 vec2)
        {
            Vector2 diference = vec2 - vec1;
            float sign = (vec2.y < vec1.y) ? -1.0f : 1.0f;
            return Vector2.Angle(Vector2.right, diference) * sign;
        }

        protected abstract void TryMove();

        protected bool SensorFront(float range = 0.15f)
        {
            RaycastHit hit;
            return Rb.SweepTest(transform.right, out hit, range);
        }

        protected bool SensorBack(float range = 0.15f)
        {
            RaycastHit hit;
            return Rb.SweepTest(transform.right * -1, out hit, range);
        }

        protected bool GoalIsLight() => PathPointIndex == 1;

        protected bool CloseToLight() => Vector3.Distance(
                                             ShiftVector3(Lane.Paths[PathId].points[1]), this.transform.position) <
                                         CloseToLightDistance;

        protected bool MovedPastLight() => Vector3.Distance(
                                               ShiftVector3(Lane.Paths[PathId].points[1]), this.transform.position) >
                                           Vector3.Distance(ShiftVector3(Lane.Paths[PathId].points[1]),
                                               ShiftVector3(Lane.Paths[PathId].points[0]));

        protected Vector3 ShiftVector3(Vector3 v) => v + this.transform.up * RandomLocationShiftY + this.transform.right * RandomLocationShiftX;
    }
}