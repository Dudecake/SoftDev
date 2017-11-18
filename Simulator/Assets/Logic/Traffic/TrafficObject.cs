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
        protected abstract float Speed { get; set; }

        protected Rigidbody Rb;

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
    }
}