using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Assets.Logic.Lights;
using UnityEditor;
using UnityEngine;

namespace Assets.Logic.Detector
{
    public abstract class Detector : MonoBehaviour
    {
        public Lane Lane;
        public Vector3 Size = new Vector3(0, 0, 2);

        private void Update()
        {
            Collider[] overlapBox = Physics.OverlapBox(transform.position, Size / 2);
            Lane.OnDetectorUpdate(FilterGameObjects(overlapBox.Select(bca => bca.transform.gameObject).ToList()));
        }

        protected abstract List<GameObject> FilterGameObjects(List<GameObject> triggeringGameObjects);
    }

    [CustomEditor(typeof(Detector), true)]
    public class DetectorInspector : Editor
    {
        private void OnSceneGUI()
        {
            Detector detector = target as Detector;
            Handles.DrawWireCube(detector.transform.position, detector.Size);
        }
    }
}