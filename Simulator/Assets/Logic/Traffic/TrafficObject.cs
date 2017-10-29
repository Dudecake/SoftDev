﻿using System.Collections;
using UnityEngine;
using Assets.Logic.Lights;

namespace Assets.Logic.Traffic
{
    
    public abstract class TrafficObject : MonoBehaviour
    {
        public TrafficLight ParentLight { get; set; }
        public bool Leaving { get; protected set; }

        private void Awake()
        {
            Debug.Log(name + " spawned");
        }

        protected IEnumerator Leave(float afterSeconds)
        {
            yield return new WaitForSeconds(afterSeconds);
            ParentLight.RemoveTrafficObject(this);
            Destroy(this.gameObject);
        }
    }
}
