package net.foxes4life.foxclient.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

public class BackgroundUtils {
    private static int backgroundIndex = 0;
    private static final int backgroundAmount = 6;
    private static Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/" + backgroundIndex + ".png");

    public static void selectBackground() {
        backgroundIndex++;
        if (backgroundIndex >= backgroundAmount) backgroundIndex = 0;
        BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/" + backgroundIndex + ".png");
    }

    public static void drawRandomBackground(DrawContext context, int w, int h) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(BACKGROUND, 0, 0, w, h, 0.0F, 0.0F, 16, 128, 16, 128);
        // todo: figure out if this is correct
    }
}
