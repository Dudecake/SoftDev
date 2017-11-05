using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

namespace Assets.Logic.Lights
{
    public class Path : MonoBehaviour
    {
        public Vector2[] points;
    }

    [CustomEditor(typeof(Path))]
    public class PathInspector : Editor
    {
        private void OnSceneGUI()
        {
            Path path = target as Path;
            Transform handleTransform = path.transform;
            Quaternion handleRotation = Tools.pivotRotation == PivotRotation.Local
                ? handleTransform.rotation
                : Quaternion.identity;

            Vector2[] pathPoints = path.points;
            for (int i = 0; i < pathPoints?.Length - 1; i++)
            {
                Vector2 p0 = handleTransform.TransformPoint(pathPoints[i]);
                Vector2 p1 = handleTransform.TransformPoint(pathPoints[i + 1]);

                Handles.color = Color.white;
                Handles.DrawLine(p0, p1);
                Handles.DoPositionHandle(p0, handleRotation);
                Handles.DoPositionHandle(p1, handleRotation);
            }
            for (int i = 0; i < pathPoints?.Length; i++)
            {
                EditorGUI.BeginChangeCheck();
                pathPoints[i] = Handles.DoPositionHandle(pathPoints[i], handleRotation);
                if (EditorGUI.EndChangeCheck())
                {
                    Undo.RecordObject(path, "Move Point");
                    EditorUtility.SetDirty(path);
                    path.points[i] = handleTransform.InverseTransformPoint(pathPoints[i]);
                }
            }
        }
    }
}