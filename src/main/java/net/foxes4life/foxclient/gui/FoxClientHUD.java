package net.foxes4life.foxclient.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.util.ClientUtils;
import net.foxes4life.foxclient.util.ServerTickUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FoxClientHUD {
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

    public void render(DrawContext context) {
        boolean drawLogo = Main.config.get(FoxClientSetting.HudLogo, Boolean.class);
        loadList(drawLogo);

        if (Main.config.get(FoxClientSetting.HudBackground, Boolean.class)) {
            context.fill(0, 0, boxWidth - 5, boxHeight - 5, 0x45454545);
            context.fill(boxWidth - 5, 0, boxWidth, boxHeight - 5, 0x45454545);
            context.fill(0, boxHeight - 5, boxWidth - 5, boxHeight, 0x45454545);
        }

        // draw logo
        if (drawLogo) {
            boxHeight += 42;
            context.drawTexture(FOXCLIENT_TEXT, 4, 4, 0, 48, 96, 48, 96, 48); // todo: figure this out

            renderList(context, 42);
        } else {
            renderList(context, 2);
        }
    }

    void loadList(boolean drawLogo) {
        textList.clear();
        boxHeight = drawLogo ? 42 : 2;
        boxWidth = 98;

        boolean version = Main.config.get(FoxClientSetting.HudVersion, Boolean.class);
        boolean coords = Main.config.get(FoxClientSetting.HudCoordinates, Boolean.class);
        boolean colorcoords = Main.config.get(FoxClientSetting.HudCoordinatesColor, Boolean.class);
        boolean fps = Main.config.get(FoxClientSetting.HudFPS, Boolean.class);
        boolean ping = Main.config.get(FoxClientSetting.HudPing, Boolean.class);
        boolean tps = Main.config.get(FoxClientSetting.HudTps, Boolean.class);
        boolean server = Main.config.get(FoxClientSetting.HudServerIP, Boolean.class);
        boolean biome = Main.config.get(FoxClientSetting.HudBiome, Boolean.class);


        if (version)
            textList.add(TextUtils.string(Main.SIMPLE_VERSION));

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

    void renderList(DrawContext context, int offset) {
        int i = 0;
        for (Text text : textList) {
            int y = offset + (10 * i);
            context.drawText(this.fontRenderer, text, 4, y, 0xFFFFFFFF, false);
            i++;
        }
    }
}
