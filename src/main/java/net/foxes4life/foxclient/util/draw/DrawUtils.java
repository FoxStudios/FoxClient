package net.foxes4life.foxclient.util.draw;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class DrawUtils {
    public static void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
    }

    public static void drawRect(MatrixStack matrices, Bounds bounds, int color) {
        drawRect(matrices, bounds.x, bounds.y, bounds.width, bounds.height, color);
    }
}
