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
            _channel.BasicConsume(queue: Constants.Communication.ReceiveQueueName, autoAck: true, consumer: _consumer);
        }

        public void AttachReceiver(EventHandler<BasicDeliverEventArgs> eventHandler)
        {
            _consumer.Received += eventHandler;
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
