using UnityEngine;

namespace Assets.Logic.Traffic
{
    public class Car : TrafficObject
    {
        protected override float Speed { get; set; } = 1f;

        private Vector3 _stuckCoords;
        private Vector3 _stuckCoordsGoal;
        private bool _attemptingUnstuck = false;

        protected override void TryMove()
        {
            if (_attemptingUnstuck)
            {
                MoveUnStuck();
            }
            else if (IsQueued())
            {
                
            }
            else if (SensorFront())
            {
                _stuckCoords = transform.position;
                _stuckCoordsGoal = transform.position - transform.right.normalized * 0.1f;
                _attemptingUnstuck = true;
            }
            else
            {
                Move();
            }
        }

        private bool IsQueued()
        {
            bool goalIsLight = PathPointIndex == 1;
            bool closeToLight = Vector2.Distance(Lane.Paths[PathId].points[1], this.transform.position) < 0.2;
            bool lightIsGreen = Lane.TrafficLight.Status == 2;
            bool pastLight = Vector2.Distance(Lane.Paths[PathId].points[1], this.transform.position) > Vector2.Distance(Lane.Paths[PathId].points[1], Lane.Paths[PathId].points[0]);
            
            return goalIsLight && (closeToLight || SensorFront()) && !pastLight && !lightIsGreen;
        }

        private void Move()
        {
            float directionAngle = AngleBetweenVector2(this.transform.position,
                Lane.Paths[PathId].points[PathPointIndex]);
            transform.rotation = Quaternion.Euler(0, 0, directionAngle);

            float step = Speed * Time.deltaTime;
            transform.position = Vector2.MoveTowards(transform.position,
                Lane.Paths[PathId].points[PathPointIndex],
                step);
            if ((Vector2)transform.position == Lane.Paths[PathId].points[PathPointIndex])
            {
                PathPointIndex++;
                if (PathPointIndex == Lane.Paths[PathId].points.Length)
                {
                    Destroy(this.gameObject);
                }
            }
        }

        private void MoveUnStuck()
        {
            if (SensorBack(0.05f) || Vector2.Distance(_stuckCoordsGoal, transform.position) < 0.0001)
            {
                _attemptingUnstuck = false;
            }
            else
            {
                float step = Speed / 4 * Time.deltaTime;

                Vector3 direction = (_stuckCoordsGoal - transform.position).normalized;
                Rb.MovePosition(transform.position + direction * step);
            }
        }
    }
}