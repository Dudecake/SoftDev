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
            public static readonly string SendQueueName = "controller";
            public static readonly string ReceiveQueueName = "simulator";

            public static readonly string HostName = "localhost";
            public static readonly string VirtualHost = "/6";
            public static readonly string UserName = "guest";
            public static readonly string Password = "guest";

            public static readonly Dictionary<string, object> QueueDeclareArguments = new Dictionary<string, object>
            {
                { "x-message-ttl", 10000 }
            };
        }
        
    }
}
