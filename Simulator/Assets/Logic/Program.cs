using System;
using System.Text;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using Newtonsoft.Json;
using UnityEngine;
using Assets.Logic;


public class Program : MonoBehaviour
{

	private static readonly string queueName = "simulator";
	private static readonly string commandqueue = "controller";
	private static readonly ConnectionFactory factory = new ConnectionFactory() {
		HostName = "localhost",
		VirtualHost = "/6",
		UserName = "guest",
		Password = "guest"
	};
	private static IConnection connection;
	private IModel channel;
	private EventingBasicConsumer simulatorConsumer;

	// Use this for initialization
	void Start()
	{
		if (connection == null)
			connection = factory.CreateConnection();
		channel = connection.CreateModel();
		channel.QueueDeclare(queue: queueName, durable: false, exclusive: false, autoDelete: false);
		simulatorConsumer = new EventingBasicConsumer(channel);
		simulatorConsumer.Received += HandleControllerData;
		channel.BasicConsume(queue: queueName, autoAck: true, consumer: simulatorConsumer);
	}

	private void HandleControllerData(object sender, BasicDeliverEventArgs e)
	{
		Crossing crossing = JsonConvert.DeserializeObject<Crossing>(Encoding.UTF8.GetString((e.Body)));
		Debug.Log(string.Format("Got: \"{0}\" from: \"{1}\"", crossing.Message, crossing.TimeStamp.ToString()));
	}
	
	// Update is called once per frame
	void Update()
	{
		Crossing messageBody = new Crossing() { Message = "Hoi" };
		channel.BasicPublish(exchange: "", routingKey: queueName, mandatory: false, basicProperties: null, body: Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(messageBody)));
	}
}
