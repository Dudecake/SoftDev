using UnityEditor;
using UnityEngine;

namespace Assets.Scripts.Lanes
{
    [CustomEditor(typeof(Path), true)]
    public class PathInspector : Editor
    {
        public override void OnInspectorGUI()
        {
            Path path = target as Path;
            DrawDefaultInspector();

            if (path == null) return;

            PathPoint[] pathPoints = path.PathPoints;
            for (int i = 0; i < pathPoints?.Length; i++)
            {
                path.PathPoints[i].Point.y = 0f;
            }
        }

        private void OnSceneGUI()
        {
            Path path = target as Path;

            if (path == null) return;

            Transform handleTransform = path.transform;
            Quaternion handleRotation = Tools.pivotRotation == PivotRotation.Local
                ? handleTransform.rotation
                : Quaternion.identity;

            PathPoint[] pathPoints = path.PathPoints;
            for (int i = 0; i < pathPoints?.Length - 1; i++)
            {
                Vector3 p0 = handleTransform.TransformPoint(pathPoints[i].Point);
                Vector3 p1 = handleTransform.TransformPoint(pathPoints[i + 1].Point);

                Handles.color = Color.white;
                Handles.DrawLine(p0, p1);
                Handles.DoPositionHandle(p0, handleRotation);
                Handles.DoPositionHandle(p1, handleRotation);
            }
            for (int i = 0; i < pathPoints?.Length; i++)
            {
                EditorGUI.BeginChangeCheck();
                pathPoints[i].Point = Handles.DoPositionHandle(pathPoints[i].Point, handleRotation);
                if (EditorGUI.EndChangeCheck())
                {
                    Undo.RecordObject(path, "TryMove Point");
                    EditorUtility.SetDirty(path);
                    path.PathPoints[i].Point = handleTransform.InverseTransformPoint(pathPoints[i].Point);
                }
            }
        }
    }
}