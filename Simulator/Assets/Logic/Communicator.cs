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
        
        private const string SendQueue = "controller";
        private const string ReceiveQueue = "simulator";
        private const string HostName = "localhost";
        private const string VirtualHost = "/6";
        private const string UserName = "softdev";
        private const string Password = "softdev";

        private readonly ConnectionFactory _factory = new ConnectionFactory()
        {
            HostName = HostName,
            VirtualHost = VirtualHost,
            UserName = UserName,
            Password = Password
        };
        private readonly IConnection _connection;
        private readonly IModel _channel;
        private readonly EventingBasicConsumer _consumer;


        private Communicator()
        {
            if (_connection == null)
                _connection = _factory.CreateConnection();
            _channel = _connection.CreateModel();
            _channel.QueueDeclare(SendQueue, false, false, true, new Dictionary<string, object>
            {
                { "x-message-ttl", 10000 }
            });
            _channel.QueueDeclare(ReceiveQueue, false, false, true, new Dictionary<string, object>
            {
                { "x-message-ttl", 10000 }
            });
            _consumer = new EventingBasicConsumer(_channel);
            _channel.BasicConsume(queue: ReceiveQueue, autoAck: true, consumer: _consumer);
        }

        public void AttachReceiver(EventHandler<BasicDeliverEventArgs> eventHandler)
        {
            _consumer.Received += eventHandler;
        }

        public void Send(string message)
        {
            byte[] body = Encoding.UTF8.GetBytes(message);
            _channel.BasicPublish(exchange: "",
                routingKey: SendQueue,
                basicProperties: null,
                body: body);
            Debug.Log($"Sent: {message}");
        }
    }
}
