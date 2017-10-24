using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Assets.Logic.Models
{
    public class TrafficUpdate
    {
        public int LightId { get; set; }
        public int Count { get; set; }
        public int[] DirectionRequests { get; set; }
    }
    
    public class Light
    {
        public int Id { get; set; }
        public int Status { get; set; }
    }
}
