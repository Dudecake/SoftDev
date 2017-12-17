using UnityEngine;

namespace Assets.Scripts
{
    public class MyCameraScript : MonoBehaviour
    {
        [Range(0.05f, 1)] public float RotateSpeed = 0.2f;
        [Range(5, 20)] public float Radius = 12.0f;

        private Vector3 _startPosition;
        private float _angle;
        
        void Start()
        {
            _startPosition = transform.position;
        }
        
        void Update()
        {
            _angle += RotateSpeed * Time.deltaTime;
            float x = Mathf.Sin(-_angle) * Radius;
            float z = Mathf.Cos(-_angle) * Radius;

            transform.position = new Vector3(x, Radius * 0.6f, z);

            transform.LookAt(new Vector3(0,0,0));
            
        }
    }
}