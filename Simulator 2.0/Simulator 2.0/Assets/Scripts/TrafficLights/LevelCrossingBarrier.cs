using System;
using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class LevelCrossingBarrier : MonoBehaviour
    {
        private MeshRenderer _myMeshRenderer;

        private float _lastColorToggleTime;
        public float ToggleSpeedSeconds = 0.5f;

        private readonly Color _toggleColor1 = Color.black;
        private readonly Color _toggleColor2 = Color.red;

        public float BarrierMovementSpeed = 0.5f;
        public Vector3 OpenRotation;
        public Vector3 ClosedRotation;

        public bool Down { get; set; } = true;

        private void Start()
        {
            _myMeshRenderer = transform.GetComponentInChildren<MeshRenderer>();
        }

        private void Update()
        {
            transform.eulerAngles = Vector3.MoveTowards(transform.eulerAngles, Down ? ClosedRotation : OpenRotation,
                Time.deltaTime * 5);

            if (transform.eulerAngles != OpenRotation)
            {
                // ReSharper disable once CompareOfFloatsByEqualityOperator
                if (Time.time > _lastColorToggleTime + ToggleSpeedSeconds || _lastColorToggleTime == default(float))
                {
                    ToggleColor();
                    _lastColorToggleTime = Time.time;
                }
            }
            else
            {
                _myMeshRenderer.material.color = _toggleColor1;
            }
        }

        private void ToggleColor()
        {
            _myMeshRenderer.material.color = _myMeshRenderer.material.color == _toggleColor1
                ? _toggleColor2
                : _toggleColor1;
        }
    }
}