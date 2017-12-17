using System;

namespace Assets.Scripts.TrafficLights
{
    public class LevelCrossingLight : TrafficLight
    {
        public LevelCrossingBarrier[] Barriers;
        public long StatusChangeDelaySec = 2;

        private long _statusSetTimeTicks = 0;
        private int _prevStatus = 2;

        private void Start()
        {
            base.Status = 2;
        }

        //Overriding Status to create a delay on the status when the LevelCrossing barrier goes open
        public override int Status
        {
            get
            {
                if (base.Status == 0 || DateTime.Now.Ticks > _statusSetTimeTicks + StatusChangeDelaySec * TimeSpan.TicksPerSecond)
                {
                    return base.Status;
                }
                else
                {
                    return _prevStatus;
                }
                
            }
            set
            {
                _prevStatus = base.Status;
                base.Status = value;
                _statusSetTimeTicks = DateTime.Now.Ticks;
            }
        }

        public override void UpdateLightVisualization()
        {
            foreach (LevelCrossingBarrier barrier in Barriers)
            {
                barrier.Down = base.Status != 2;
            }
        }
    }
}