package net.foxes4life.foxclient.util.transforms;

public class Transform {
    public double duration;
    public Easing easing;
    public double startValue;
    public double endValue;
    public boolean removeOnFinish = true;

    private double currentTime = 0.0D;

    public Transform(double duration, Easing easing, double startValue, double endValue) {
        this.duration = duration;
        this.easing = easing;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public double getValue() {
        double progress = Math.min(Math.max(currentTime / duration, 0.0D), 1.0D);
        return Easing.applyEasing(easing, progress) * (endValue - startValue) + startValue;
    }

    public void addTime(double time) {
        currentTime += time;
    }

    public boolean isDone() {
        return currentTime >= duration;
    }

    public void reset() {
        currentTime = 0.0D;
    }

    public void restartAt() {
        double progress = Math.min(Math.max(currentTime / duration, 0.0D), 1.0D);
        duration = duration - (duration * (1 - progress));
        reset();
    }
}
