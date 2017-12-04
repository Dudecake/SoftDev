using System;
using System.Collections.Generic;
using System.Text;
using Assets.DataModels;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using UnityEngine;

namespace Assets.Scripts
{
    public class Communicator
    {
        private readonly string _sendqueue;
        private readonly IModel _channel;
        private readonly EventingBasicConsumer _consumer;
        private readonly bool _verbose;

        public Communicator(string sendqueue, string receivequeue, string hostname, string virtualhost = "/",
            string username = "guest", string password = "guest", bool verbose = false, int messagettl = 10000)
        {
            _sendqueue = sendqueue;
            _verbose = verbose;
            string receivequeue1 = receivequeue;

            ConnectionFactory factory = new ConnectionFactory
            {
                HostName = hostname,
                VirtualHost = virtualhost,
                UserName = username,
                Password = password
            };
            IConnection connection = factory.CreateConnection();
            _channel = connection.CreateModel();
            _channel.QueueDeclare(_sendqueue, false, false, true, new Dictionary<string, object>
            {
                {"x-message-ttl", messagettl}
            });
            _channel.QueueDeclare(receivequeue1, false, false, true, new Dictionary<string, object>
            {
                {"x-message-ttl", messagettl}
            });
            _consumer = new EventingBasicConsumer(_channel);
            _channel.BasicConsume(receivequeue1, true, _consumer);
            AttachReceiver(OnMessageReceive);
        }

        public void AttachReceiver(EventHandler<BasicDeliverEventArgs> eventHandler)
        {
            _consumer.Received += eventHandler;
            if (_verbose)
            {
                Debug.Log($"Receiver from \"{eventHandler.Target.GetType().Name}\" attached");
            }
        }

        public void Send(SimulatorMessageDataModel message)
        {
            Send(JsonConvert.SerializeObject(message));
        }
        private void Send(string message)
        {
            byte[] body = Encoding.UTF8.GetBytes(message);
            _channel.BasicPublish("", _sendqueue, null, body);
            if (_verbose)
            {
                Debug.Log($"Sent: {message}");
            }
        }

        private void OnMessageReceive(object sender, BasicDeliverEventArgs ea)
        {
            if (_verbose)
            {
                string message = Encoding.UTF8.GetString(ea.Body);
                Debug.Log($"Received: {message}");
            }
        }
    }
}