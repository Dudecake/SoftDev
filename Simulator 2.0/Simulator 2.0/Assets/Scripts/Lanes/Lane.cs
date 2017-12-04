using UnityEngine;

namespace Assets.Scripts.Lanes
{
    public class Lane : MonoBehaviour
    {
        public int LaneId;
        public Path[] Paths { get; private set; }

        private void Start()
        {
            Paths = GetComponentsInChildren<Path>();
        }
    }
}
