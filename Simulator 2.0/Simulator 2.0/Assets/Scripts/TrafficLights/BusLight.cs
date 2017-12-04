using System;
using Assets.DataModels;
using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class BusLight : TrafficLight
    {
        public LightMaterialScript TopLeftLight;
        public LightMaterialScript TopMiddleLight;
        public LightMaterialScript TopRightLight;
        public LightMaterialScript MiddleLeftLight;
        public LightMaterialScript MiddleMiddleLight;
        public LightMaterialScript MiddleRightLight;
        public LightMaterialScript BottomLeftLight;
        public LightMaterialScript BottomMiddleLight;
        public LightMaterialScript BottomRightLight;

        public override void UpdateLightVisualization()
        {
            TopLeftLight.Color = TopMiddleLight.Color = TopRightLight.Color =
                MiddleLeftLight.Color = MiddleMiddleLight.Color = MiddleRightLight.Color = BottomLeftLight.Color =
                    BottomMiddleLight.Color = BottomRightLight.Color = Color.black;
            switch (Status)
            {
                case 1: // Stop if possible (orange)
                    MiddleMiddleLight.Color = Color.yellow;
                    break;
                case 2: // Straight ahead
                    TopMiddleLight.Color = BottomMiddleLight.Color = Color.white;
                    break;
                case 3: // Left
                    TopLeftLight.Color = BottomRightLight.Color = Color.white;
                    break;
                case 4: // Right
                    TopRightLight.Color = BottomLeftLight.Color = Color.white;
                    break;
                case 5: // Left + Straight
                    TopMiddleLight.Color = BottomMiddleLight.Color = TopLeftLight.Color = Color.white;
                    break;
                case 6: // Right + Straight
                    TopMiddleLight.Color = BottomMiddleLight.Color = TopRightLight.Color = Color.white;
                    break;
                case 7: // Left + Right
                    BottomMiddleLight.Color =
                        TopLeftLight.Color = TopRightLight.Color = Color.white;
                    break;
                case 8: // All
                    TopMiddleLight.Color = BottomMiddleLight.Color =
                        TopLeftLight.Color = TopRightLight.Color = Color.white;
                    break;
                default: // Stop (red)
                    MiddleLeftLight.Color = MiddleRightLight.Color = Color.red;
                    break;
            }
        }
    }
}