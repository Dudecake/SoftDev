using System;
using System.Text;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using Newtonsoft.Json;
using UnityEngine;

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
    void Start()
	{
	    if (_connection == null)
	        _connection = Factory.CreateConnection();
	    _channel = _connection.CreateModel();
	    _channel.QueueDeclare(queue: SendQueueName, durable: false, exclusive: false, autoDelete: false);
	    _consumer = new EventingBasicConsumer(_channel);
	    _consumer.Received += HandleControllerData;
	    _channel.BasicConsume(queue: ReceiveQueueName, autoAck: true, consumer: _consumer);

	    Send("Simulator: 6");
    }

	private void HandleControllerData(object sender, BasicDeliverEventArgs ea)
	{
	    Debug.Log($"Received: {Encoding.UTF8.GetString(ea.Body)}");
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
    void Update()
	{
		//Crossing messageBody = new Crossing() { Message = "Hoi" };
		//channel.BasicPublish(exchange: "", routingKey: queueName, mandatory: false, basicProperties: null, body: Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(messageBody)));
	}
}
