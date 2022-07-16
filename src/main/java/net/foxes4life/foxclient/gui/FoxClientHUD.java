package net.foxes4life.foxclient.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.util.ClientUtils;
import net.foxes4life.foxclient.util.ServerTickUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FoxClientHUD extends DrawableHelper {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;

    private int boxHeight = 44;
    private int boxWidth = 98;
    private final List<Text> textList = Lists.newArrayList();

    private static final Identifier FOXCLIENT_TEXT = new Identifier("foxclient", "textures/ui/foxclient_text.png");

    public FoxClientHUD(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void render(MatrixStack matrices) {
        loadList();

        fill(matrices, 2, 2, boxWidth, boxHeight, 0x45454545);

        // draw logo
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FOXCLIENT_TEXT);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 4, 4, 0, 48, 96, 48, 96, 48);

        renderList(matrices);
    }

    void loadList () {
        boolean version = (boolean) Main.konfig.get("ingame-hud", "version");
        boolean coords = (boolean) Main.konfig.get("ingame-hud", "coords");
        boolean fps = (boolean) Main.konfig.get("ingame-hud", "fps");
        boolean ping = (boolean) Main.konfig.get("ingame-hud", "ping");
        boolean tps = (boolean) Main.konfig.get("ingame-hud", "tps");


        if (version)
            textList.add(TextUtils.string(Main.VERSION));

        if (coords) {
            assert this.client.player != null;
            textList.add(TextUtils.string(String.format("[XYZ] %s %s %s", this.client.player.getBlockPos().getX(), this.client.player.getBlockPos().getY(), this.client.player.getBlockPos().getZ())));
        }

        if (fps)
            textList.add(TextUtils.string("[FPS] " + ClientUtils.getFPS()));

        if (ping)
            textList.add(TextUtils.string(String.format("[Ping] " + ClientUtils.getPing() + "ms")));

        if (tps)
            textList.add(TextUtils.string(String.format("[TPS] " + ServerTickUtils.calculateServerTPS())));

        for (Text text : textList) {
            boxHeight += 10;

            if (this.client.textRenderer.getWidth(text.getString()) > boxWidth - 8) {
                boxWidth = this.client.textRenderer.getWidth(text.getString()) + 8;
            }
        }
    }

    void renderList (MatrixStack matrices) {
        int i = 0;
        for (Text text : textList) {
            int y = 42 + (10 * i);
            this.fontRenderer.draw(matrices, text, 4, y, 0xFFFFFFFF);
            i++;
        }
    }
}
