using UnityEngine;
using UnityEngine.UI;

namespace Assets.Logic.Lights
{
    public class LightTimer : MonoBehaviour
    {
        public GameObject Canvas;
        public Text TextPrefab;

        private TrafficLight _trafficLight;
        private Text _text;

        private float _lastTimeValue = -1;
        private float _lastTimeValueReceiveTime;

        private void Start()
        {
            if (Canvas == null || TextPrefab == null)
            {
                Debug.LogError("Assign all prefabs");
                UnityEditor.EditorApplication.isPlaying = false;
            }

            _trafficLight = GetComponent<TrafficLight>();

            if (_trafficLight == null)
            {
                Debug.LogError("Cant get component TrafficLight");
            }
            
            _text = Instantiate(TextPrefab, Canvas.transform);
            _text.transform.position = transform.localPosition;
        }
    
        private void Update()
        {
            if (_lastTimeValue >= 0)
            {
                float t = Mathf.Max(_lastTimeValue - (Time.time - _lastTimeValueReceiveTime), 0);
                _text.text = Mathf.RoundToInt(t).ToString();
            }

            if (_trafficLight != null && _trafficLight.Time >= 0)
            {
                _lastTimeValue = _trafficLight.Time;
                _lastTimeValueReceiveTime = Time.time;
                _trafficLight.Time = -1;
            }
        }
    }
}