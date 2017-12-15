using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Assets.Logic.Traffic;
using Assets.Logic.ViewModels;
using Newtonsoft.Json;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class TrafficLight : MonoBehaviour
    {
        public int TrafficCount { get; set; }

        public int Id;
        private SpriteRenderer sr;
        public int Status { get; set; }
        public int Time { get; set; } = -1;

        private void Start()
        {
            sr = gameObject.GetComponent<SpriteRenderer>();
        }

        private void Update()
        {
            switch (Status)
            {
                case 0:
                    this.sr.color = Color.red;
                    break;
                case 1:
                    this.sr.color = Color.yellow;
                    break;
                case 2:
                    this.sr.color = Color.green;
                    break;
                default:
                    this.sr.color = Color.red;
                    break;
            }
        }
    }
}