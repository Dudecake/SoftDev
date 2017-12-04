using System.Collections.Generic;
using System.Linq;
using System.Text;
using Assets.DataModels;
using Assets.Scripts.Lanes;
using Assets.Scripts.TrafficLights;
using Assets.Scripts.TrafficObjects;
using Newtonsoft.Json;
using RabbitMQ.Client.Events;
using UnityEngine;

namespace Assets.Scripts
{
    public class IntersectionController : MonoBehaviour
    {
        public static Communicator Communicator { get; private set; }
        private TrafficLight[] _trafficLights;

        //Connection settings
        public string HostName;
        public string VirtualHost;
        public string UserName;
        public string PassWord;
        public bool Verbose;
        private const string SendQueue = "controller";
        private const string ReceiveQueue = "simulator";
        
        private void Start()
        {
            Communicator = new Communicator(SendQueue, ReceiveQueue, HostName, VirtualHost, UserName, PassWord, Verbose);
            Communicator.AttachReceiver(OnMessageReceive);

            _trafficLights = GetComponentsInChildren<TrafficLight>();

            // Keep the simulation running while its window is not in focus
            Application.runInBackground = true;
        }

        private void OnMessageReceive(object sender, BasicDeliverEventArgs ea)
        {
            string message = Encoding.UTF8.GetString(ea.Body);

            ControllerMessageDataModel model = JsonConvert.DeserializeObject<ControllerMessageDataModel>(message);

            if (model?.Lights?.Length > 0)
            {
                foreach (ControllerMessageDataModel.LightDataModel ldm in model.Lights)
                {
                    foreach (TrafficLight trafficLight in _trafficLights.Where(tl => tl.Id == ldm.Id))
                    {
                        trafficLight.Status = ldm.Status;

                        ITimerSupporter timerSupporter = trafficLight as ITimerSupporter;
                        timerSupporter?.SetTime(ldm.Time);
                    }
                }
            }
        }
    }
}