using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Assets.Logic;
using Assets.Logic.Lights;

namespace Assets
{
    public static class Constants
    {
        public static class Communication
        {
            public readonly static string SendQueueName = "controller";
            public readonly static string ReceiveQueueName = "simulator";

            public readonly static string HostName = "localhost";
            public readonly static string VirtualHost = "/6";
            public readonly static string UserName = "guest";
            public readonly static string Password = "guest";

            public readonly static Dictionary<string, object> QueueDeclareArguments = new Dictionary<string, object>
            {
                { "x-message-ttl", 10000 }
            };
        }

        public readonly static Dictionary<int, TrafficLight> lights = new Dictionary<int, TrafficLight>
        {
            {101, new RegularTrafficLight(101)},
            {102, new RegularTrafficLight(102)},
            {103, new RegularTrafficLight(103)},
            {104, new RegularTrafficLight(104)},
            {105, new RegularTrafficLight(105)},
            {106, new RegularTrafficLight(106)},
            {107, new RegularTrafficLight(107)},
            {108, new RegularTrafficLight(108)},
            {109, new RegularTrafficLight(109)},
            {110, new RegularTrafficLight(110)},

            {201, new BusTrafficLight(201)},

            {301, new BicycleTrafficLight(301)},
            {302, new BicycleTrafficLight(302)},
            {303, new BicycleTrafficLight(303)},
            {305, new BicycleTrafficLight(305)},
            {306, new BicycleTrafficLight(306)},

            {401, new PedestrianTrafficLight(401)},
            {402, new PedestrianTrafficLight(402)},
            {403, new PedestrianTrafficLight(403)},
            {404, new PedestrianTrafficLight(404)},
            {405, new PedestrianTrafficLight(405)},
            {406, new PedestrianTrafficLight(406)},

            {501, new TrainTrafficLight(501)},

            {601, new LevelCrossingTrafficLight(601)},
        };
    }
}
