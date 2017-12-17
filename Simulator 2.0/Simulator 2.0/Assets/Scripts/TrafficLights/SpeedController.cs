using System;
using System.Text;
using Assets.DataModels;
using Newtonsoft.Json;
using RabbitMQ.Client.Events;
using UnityEngine;
using UnityEngine.UI;

namespace Assets.Scripts.TrafficLights
{
    public class SpeedController : MonoBehaviour
    {
        public Text SpeedSettingText;
        private Slider _slider;
        private float _speed = 1;

        private void Start()
        {
            _slider = GetComponent<Slider>();
            if (_slider == null)
            {
                Debug.LogError("Cant find Slider for SpeedController");
            }

            IntersectionController.Communicator.AttachReceiver(this.OnMessageReceive);
        }

        private void Update()
        {
            if (Mathf.Abs(Time.timeScale - _speed) > 0)
            {
                Time.timeScale = _speed;
                //Time.fixedDeltaTime = _speed;
                _slider.value = _speed;
                SpeedSettingText.text = $"Speed: {_speed}x";
                _slider.interactable = true;
            }
        }

        private void OnMessageReceive(object sender, BasicDeliverEventArgs ea)
        {
            string message = Encoding.UTF8.GetString(ea.Body);

            ControllerMessageDataModel model = JsonConvert.DeserializeObject<ControllerMessageDataModel>(message);

            if (model.Speed > 0)
            {
                _speed = model.Speed;
            }
        }

        public void OnChange()
        {
            SpeedSettingText.text = $"Speed: {_slider.value}x";
            IntersectionController.Communicator.Send(new SpeedViewModel
            {
                Speed = Convert.ToSingle(_slider.value)
            });
            _slider.interactable = false;
        }
    }
}