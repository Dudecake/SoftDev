using System;
using System.Text;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using RabbitMQ.Client.Events;
using UnityEngine;
using UnityEngine.UI;
using NUnit.Framework.Constraints;

namespace Assets.Logic
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

            Communicator.Instance.AttachReceiver(this.ConsumerOnReceived);
        }

        private void Update()
        {
            if (Math.Abs(Time.timeScale - _speed) > 0)
            {
                Time.timeScale = _speed;
                Time.fixedDeltaTime = _speed;
                _slider.value = _speed;
                SpeedSettingText.text = $"Speed: {_speed}x";
                _slider.interactable = true;
            }
        }

        private void ConsumerOnReceived(object sender, BasicDeliverEventArgs e)
        {
            string message = Encoding.UTF8.GetString(e.Body);
            Debug.Log($"Received: {message}");

            float speed = JsonConvert.DeserializeObject<ControllerMessage>(message).Speed;
            if (speed > 0)
            {
                _speed = speed;
            }
        }

        public void OnChange()
        {
            if (_slider != null)
            {
                SpeedSettingText.text = $"Speed: {_slider.value}x";
                Communicator.Instance.Send(new SpeedViewModel
                {
                    Speed = Convert.ToSingle(_slider.value)
                });
                _slider.interactable = false;
            }
        }
    }
}