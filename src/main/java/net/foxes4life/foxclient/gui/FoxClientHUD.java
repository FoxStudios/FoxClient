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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FoxClientHUD extends DrawableHelper {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;

    private int boxHeight = 2;
    private int boxWidth = 98;
    private final List<Text> textList = Lists.newArrayList();

    private static final Identifier FOXCLIENT_TEXT = new Identifier("foxclient", "textures/ui/branding/text.png");

    public FoxClientHUD(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void render(MatrixStack matrices) {
        boolean drawLogo = (boolean) Main.konfig.get("ingame-hud", "logo");
        loadList(drawLogo);

        if ((boolean) Main.konfig.get("ingame-hud", "background")) {
            fill(matrices, 0, 0, boxWidth - 5, boxHeight - 5, 0x45454545);
            fill(matrices, boxWidth - 5, 0, boxWidth, boxHeight - 5, 0x45454545);
            fill(matrices, 0, boxHeight - 5, boxWidth - 5, boxHeight, 0x45454545);
        }

        // draw logo
        if (drawLogo) {
            boxHeight += 42;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, FOXCLIENT_TEXT);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexture(matrices, 4, 4, 0, 48, 96, 48, 96, 48);

            renderList(matrices, 42);
        } else {
            renderList(matrices, 2);
        }
    }

    void loadList(boolean drawLogo) {
        textList.clear();
        boxHeight = drawLogo ? 42 : 2;
        boxWidth = 98;

        boolean version = (boolean) Main.konfig.get("ingame-hud", "version");
        boolean coords = (boolean) Main.konfig.get("ingame-hud", "coords");
        boolean colorcoords = (boolean) Main.konfig.get("ingame-hud", "colored-coords");
        boolean fps = (boolean) Main.konfig.get("ingame-hud", "fps");
        boolean ping = (boolean) Main.konfig.get("ingame-hud", "ping");
        boolean tps = (boolean) Main.konfig.get("ingame-hud", "tps");
        boolean biome = (boolean) Main.konfig.get("ingame-hud", "biome");
        boolean server = (boolean) Main.konfig.get("ingame-hud", "server");


        if (version)
            textList.add(TextUtils.string(Main.VERSION));

        if (coords) {
            assert this.client.player != null;
            textList.add(TextUtils.string(String.format("[XYZ] " + (colorcoords ? "§c%s §a%s §9%s" : "%s %s %s"), this.client.player.getBlockPos().getX(), this.client.player.getBlockPos().getY(), this.client.player.getBlockPos().getZ())));
        }

        if (fps)
            textList.add(TextUtils.string("[FPS] " + ClientUtils.getFPS()));

        if (ping)
            textList.add(TextUtils.string(String.format("[Ping] " + ClientUtils.getPing() + "ms")));

        if (tps)
            textList.add(TextUtils.string(String.format("[TPS] " + ServerTickUtils.calculateServerTPS())));

        if (biome)
            if (client.world != null) {
                assert client.getCameraEntity() != null;
                final BlockPos blockPos = client.getCameraEntity().getBlockPos();
                textList.add(TextUtils.string(String.format("[BIOME] " + client.world.getBiome(blockPos).getKey().get().getValue().toString())));
            }

        if (server) {
            if (client.getCurrentServerEntry() != null) {
                textList.add(TextUtils.string(String.format("[IP] " + client.getCurrentServerEntry().address)));
            } else {
                textList.add(TextUtils.string("[IP] " + I18n.translate("menu.singleplayer")));
            }
        }

        for (Text text : textList) {
            boxHeight += 10;

            if (this.client.textRenderer.getWidth(text.getString()) > boxWidth - 8) {
                boxWidth = this.client.textRenderer.getWidth(text.getString()) + 8;
            }
        }
    }

    void renderList(MatrixStack matrices, int offset) {
        int i = 0;
        for (Text text : textList) {
            int y = offset + (10 * i);
            this.fontRenderer.draw(matrices, text, 4, y, 0xFFFFFFFF);
            i++;
        }
    }
}
