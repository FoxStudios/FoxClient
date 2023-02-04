package net.foxes4life.foxclient.util.draw;

public enum Anchor {
    TopLeft(0, 0),
    TopCenter(0.5f, 0),
    TopRight(1, 0),
    CenterLeft(0, 0.5f),
    Center(0.5f, 0.5f),
    CenterRight(1, 0.5f),
    BottomLeft(0, 1),
    BottomCenter(0.5f, 1),
    BottomRight(1, 1);

    private final float x;
    private final float y;

    Anchor(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
