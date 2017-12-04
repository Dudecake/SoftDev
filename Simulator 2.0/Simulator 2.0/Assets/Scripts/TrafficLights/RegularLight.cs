using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class RegularLight : TrafficLight
    {
        public LightMaterialScript TopLight;
        public LightMaterialScript MiddleLight;
        public LightMaterialScript BottomLight;

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
        }
    }
}