package net.foxes4life.foxclient.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;

public class FoxClientHUD extends DrawableHelper {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;

    private static final Identifier FOXCLIENT_TEXT = new Identifier("foxclient", "textures/ui/foxclient_text.png");

    public FoxClientHUD(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void render(MatrixStack matrices) {
        int width = this.client.getWindow().getScaledWidth();
        int height = this.client.getWindow().getScaledHeight();

        fill(matrices, 0, 0, 69, 69, 0x45454545);

        // draw logo
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FOXCLIENT_TEXT);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 0, 0, 0, 48, 96, 48, 96, 48);

        //this.fontRenderer.draw(matrices, "yes (actually no)", 0, (float)0, 16777215);
    }
}
