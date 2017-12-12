using System;
using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class BicycleLight : TrafficLight, ITimerSupporter
    {
        private long _timeSetTime;
        private int _time = -1;

        public LightMaterialScript TopLight;
        public LightMaterialScript MiddleLight;
        public LightMaterialScript BottomLight;

        public bool HasTimer;
        private LightMaterialScript[] _timerLights;
        public LightMaterialScript TimerLightPrefab;
        public Vector3 TimerLightMiddle;
        public int TimerLightCount;
        public float Radius;

        private void Start()
        {
            if (HasTimer)
            {
                _timerLights = new LightMaterialScript[TimerLightCount];
                for (int i = 0; i < TimerLightCount; i++)
                {
                    Vector3 pos;
                    pos.x = TimerLightMiddle.x + Radius * Mathf.Sin(360 / TimerLightCount * i * Mathf.Deg2Rad);
                    pos.y = TimerLightMiddle.y + Radius * Mathf.Cos(360 / TimerLightCount * i * Mathf.Deg2Rad);
                    pos.z = TimerLightMiddle.z;

                    _timerLights[i] = Instantiate(TimerLightPrefab);
                    _timerLights[i].transform.parent = transform;
                    _timerLights[i].transform.localPosition = pos;
                    _timerLights[i].transform.rotation = transform.rotation;
                }
            }
        }

        public override void UpdateLightVisualization()
        {
            TopLight.Color = MiddleLight.Color = BottomLight.Color = Color.black;
            switch (Status)
            {
                case 1:
                    MiddleLight.Color = Color.yellow;
                    break;
                case 2:
                    BottomLight.Color = Color.green;
                    break;
                default:
                    TopLight.Color = Color.red;
                    break;
            }

            if (HasTimer)
            {
                for (int i = 0; i < _timerLights.Length; i++)
                {
                    if (Status == 2)
                    {
                        _timerLights[i].Color = Color.black;
                    }
                    else
                    {
                        _timerLights[i].Color = i < (_time * 1000 - (DateTime.Now.Ticks / 10000 - _timeSetTime / 10000)) / 1000 ? Color.white : Color.black;
                    }
                }
            }
        }
        
        public void SetTime(int time)
        {
            _time = time;
            _timeSetTime = DateTime.Now.Ticks;
        }
    }
}