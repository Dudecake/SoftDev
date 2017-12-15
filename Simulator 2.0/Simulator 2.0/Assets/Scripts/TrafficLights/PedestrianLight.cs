using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class PedestrianLight : TrafficLight
    {
        public LightMaterialScript TopLight;
        public LightMaterialScript BottomLight;

        public float BlinkSpeed = 0.5f;
        private float _lastBlinkTime;
        private bool _blinkStateOn;

        public override void UpdateLightVisualization()
        {
            TopLight.Color = BottomLight.Color = Color.black;
            switch (Status)
            {
                case 1:
                    if (_blinkStateOn)
                    {
                        BottomLight.Color = Color.green;
                    }
                    if (Time.time - _lastBlinkTime > BlinkSpeed)
                    {
                        _blinkStateOn = !_blinkStateOn;
                        _lastBlinkTime = Time.time;
                    }
                    break;
                case 2:
                    BottomLight.Color = Color.green;
                    break;
                default:
                    TopLight.Color = Color.red;
                    break;
            }
        }
    }
}