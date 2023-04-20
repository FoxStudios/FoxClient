package net.foxes4life.foxclient.util.draw;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class DrawUtils {
    public static void drawRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
    }

    public static void drawRect(MatrixStack matrices, Bounds bounds, int color) {
        drawRect(matrices, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, color);
    }

    // fuck you mojang i am stealing this
    public static void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, Text text, int centerX, int y, int color) {
        OrderedText lvOrderedText6 = text.asOrderedText();
        textRenderer.drawWithShadow(matrices, lvOrderedText6, (float)(centerX - textRenderer.getWidth(lvOrderedText6) / 2), (float)y, color);
    }
}
