using UnityEngine;

namespace Assets.Scripts
{
    public class BloodSplatter : MonoBehaviour
    {
        public float DespawnDurationSeconds = 30;

        private SpriteRenderer _spriteRenderer;
        private float _spawnTime;

        // Use this for initialization
        private void Start ()
        {
            _spriteRenderer = transform.GetComponent<SpriteRenderer>();
            _spawnTime = Time.time;
        }

        // Update is called once per frame
        private void Update ()
        {
            _spriteRenderer.color = new Color(1, 1, 1, Mathf.Lerp(1.0f, 0.0f, (Time.time - _spawnTime) / DespawnDurationSeconds));

            if (Time.time - _spawnTime > DespawnDurationSeconds)
            {
                Destroy(gameObject);
            }
        }
    }
}
