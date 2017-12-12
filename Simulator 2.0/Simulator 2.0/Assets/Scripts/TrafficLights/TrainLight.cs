using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class TrainLight : TrafficLight
    {
        public LightMaterialScript TopLight;
        public LightMaterialScript BottomLight;

        public override void UpdateLightVisualization()
        {
            if (TopLight == null || BottomLight == null)
            {
                return;
            }

            TopLight.Color = BottomLight.Color = Color.black;
            switch (Status)
            {
                case 2:
                    TopLight.Color = Color.green;
                    break;
                default:
                    BottomLight.Color = Color.red;
                    break;
            }
        }
    }
}