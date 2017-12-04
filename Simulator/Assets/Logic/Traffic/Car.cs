﻿using UnityEngine;

namespace Assets.Logic.Traffic
{
    public class Car : TrafficObject
    {
        protected override float Speed { get; set; } = 1f;
        protected override float CloseToLightDistance { get; set; } = 0.15f;
        protected override float RandomLocationShiftY { get; set; }
        protected override float RandomLocationShiftX { get; set; }

        private void Awake()
        {
            RandomLocationShiftY = Random.Range(-0.02f, 0.02f);
            RandomLocationShiftX = Random.Range(-0.02f, 0.02f);
        }

        protected override void TryMove()
        {
            if (IsQueued())
            {
                
            }
            else if (SensorFront())
            {
                
            }
            else
            {
                Move();
            }
        }
        
        private bool LightIsGreen() => Lane.TrafficLight.Status == 2;
        private bool IsQueued() => GoalIsLight() && (CloseToLight() || SensorFront()) && !MovedPastLight() && !LightIsGreen();

        private void Move()
        {
            float directionAngle = AngleBetweenVector2(this.transform.position,
                ShiftVector3(Lane.Paths[PathId].points[PathPointIndex]));
            transform.rotation = Quaternion.Euler(0, 0, directionAngle);

            float step = Speed * Time.deltaTime;
            transform.position = Vector2.MoveTowards(transform.position,
                ShiftVector3(Lane.Paths[PathId].points[PathPointIndex]),
                step);
            if (transform.position == ShiftVector3(Lane.Paths[PathId].points[PathPointIndex]))
            {
                PathPointIndex++;
                if (PathPointIndex == Lane.Paths[PathId].points.Length)
                {
                    Destroy(this.gameObject);
                }
            }
        }
    }
}