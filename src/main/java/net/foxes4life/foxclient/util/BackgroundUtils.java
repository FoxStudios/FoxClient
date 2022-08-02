package net.foxes4life.foxclient.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

public class BackgroundUtils {
    private static int backgroundIndex = 0;
    private static final int backgroundAmount = 3;
    private static Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/" + backgroundIndex + ".png");

    public static void selectBackground() {
        backgroundIndex++;
        if(backgroundIndex >= backgroundAmount) backgroundIndex = 0;
        BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/" + backgroundIndex + ".png");
    }

    public static void drawRandomBackground(MatrixStack matrices, int w, int h) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 0, 0, w, h, 0.0F, 0.0F, 16, 128, 16, 128);
    }
}
