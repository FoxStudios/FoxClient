package net.foxes4life.foxclient.util.draw;

public class AnchoredBounds extends Bounds {
    public final Anchor anchor;
    public final Anchor origin;

    public AnchoredBounds(int x, int y, int width, int height, int parentWidth, int parentHeight, Anchor anchor, Anchor origin) {
        super(x + (int) (parentWidth * anchor.getX()) - (int) (width * origin.getX()), y + (int) (parentHeight * anchor.getY()) - (int) (height * origin.getY()), width, height);
        this.anchor = anchor;
        this.origin = origin;
    }
}
