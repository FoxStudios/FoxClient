package net.foxes4life.foxclient.util.draw;

import com.terraformersmc.modmenu.util.DrawingUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class DrawUtils {
    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawRect(DrawContext context, Bounds bounds, int color) {
        drawRect(context, bounds.x, bounds.y, bounds.width, bounds.height, color);
    }
}
