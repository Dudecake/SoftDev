using System;
using System.Text;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using UnityEngine;
using System.Collections.Generic;
using Assets.Logic.Models;


public class Program : MonoBehaviour
{
    private const string SendQueueName = "controller";
    private const string ReceiveQueueName = "simulator";

    private static readonly ConnectionFactory Factory = new ConnectionFactory()
    {
        HostName = "localhost",
        VirtualHost = "/6",
        UserName = "guest",
        Password = "guest"
    };

    private static IConnection _connection;
    private static IModel _channel;
    private static EventingBasicConsumer _consumer;

    // Use this for initialization
    private void Start()
    {
        if (_connection == null)
            _connection = Factory.CreateConnection();
        _channel = _connection.CreateModel();
        var args = new Dictionary<string, object>();
        args.Add("x-message-ttl", 10000);
        _channel.QueueDeclare(SendQueueName, false, false, true, args);
        _channel.QueueDeclare(ReceiveQueueName, false, false, true, args);
        _consumer = new EventingBasicConsumer(_channel);
        _consumer.Received += HandleControllerData;
        _channel.BasicConsume(queue: ReceiveQueueName, autoAck: true, consumer: _consumer);

        Send("Simulator: 6");
    }

    private void HandleControllerData(object sender, BasicDeliverEventArgs ea)
    {
        string message = Encoding.UTF8.GetString(ea.Body);
        Debug.Log($"Received: {message}");

        Assets.Logic.Models.Light[] Lights = JsonUtility.FromJson<Assets.Logic.Models.Light[]>(message);
        Debug.Log($"Received: {JsonUtility.ToJson(Lights)}");
    }

    private static void Send(string message)
    {
        byte[] body = Encoding.UTF8.GetBytes(message);
        _channel.BasicPublish(exchange: "",
            routingKey: SendQueueName,
            basicProperties: null,
            body: body);
        Debug.Log($"Sent: {message}");
    }

    // Update is called once per frame
    private void Update()
    {
        //Crossing messageBody = new Crossing() { Message = "Hoi" };
        //channel.BasicPublish(exchange: "", routingKey: queueName, mandatory: false, basicProperties: null, body: Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(messageBody)));
    }
}