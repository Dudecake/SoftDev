using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using UnityEngine;

namespace Assets.Logic
{
    public class Communicator
    {
        private static Communicator _instance;
        public static Communicator Instance => _instance ?? (_instance = new Communicator());

        private readonly ConnectionFactory _factory = new ConnectionFactory()
        {
            HostName = Constants.Communication.HostName,
            VirtualHost = Constants.Communication.VirtualHost,
            UserName = Constants.Communication.UserName,
            Password = Constants.Communication.Password
        };
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private readonly EventingBasicConsumer _consumer;

        private Communicator()
        {
            if (_connection == null)
                _connection = _factory.CreateConnection();
            _channel = _connection.CreateModel();
            _channel.QueueDeclare(Constants.Communication.SendQueueName, false, false, true, Constants.Communication.QueueDeclareArguments);
            _channel.QueueDeclare(Constants.Communication.ReceiveQueueName, false, false, true, Constants.Communication.QueueDeclareArguments);
            _consumer = new EventingBasicConsumer(_channel);
            _consumer.Received += ConsumerOnReceived;
            _channel.BasicConsume(queue: Constants.Communication.ReceiveQueueName, autoAck: true, consumer: _consumer);
        }

        private void ConsumerOnReceived(object sender, BasicDeliverEventArgs ea)
        {
            string message = Encoding.UTF8.GetString(ea.Body);
            Debug.Log($"Received: {message}");

            LightViewModel[] lights = JsonConvert.DeserializeObject<LightViewModel[]>(message);
            Debug.Log($"Received: {JsonUtility.ToJson(lights)}");
        }

        public void Send(string message)
        {
            byte[] body = Encoding.UTF8.GetBytes(message);
            _channel.BasicPublish(exchange: "",
                routingKey: Constants.Communication.SendQueueName,
                basicProperties: null,
                body: body);
            Debug.Log($"Sent: {message}");
        }
    }
}
