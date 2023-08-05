package net.foxes4life.foxclient.util.transforms;

import java.util.ArrayList;
import java.util.List;

public class TransformManager {
    private final List<Transform> transforms;

    public TransformManager() {
        transforms = new ArrayList<>();
    }

    public void addTransform(Transform transform) {
        transforms.add(transform);
    }

    public void addIfNotExists(Transform transform) {
        if (!hasTransform(transform))
            addTransform(transform);
    }

    public void removeTransform(Transform transform) {
        transforms.remove(transform);
    }

    public boolean hasTransform(Transform transform) {
        return transforms.contains(transform);
    }

    public void update(double time) {
        for (Transform transform : transforms) {
            transform.addTime(time);

            if (transform.isDone() && transform.removeOnFinish)
                removeTransform(transform);
        }
    }
}
