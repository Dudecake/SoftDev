using UnityEngine;

namespace Assets.Scripts.TrafficLights
{
    public class LightMaterialScript : MonoBehaviour
    {
        private MeshRenderer _myMeshRenderer;

        private void Start()
        {
            _myMeshRenderer = transform.GetComponentInChildren<MeshRenderer>();
        }

        public Color Color
        {
            get { return _myMeshRenderer.material.color; }
            set { _myMeshRenderer.material.color = value; }
        }
    }
}
