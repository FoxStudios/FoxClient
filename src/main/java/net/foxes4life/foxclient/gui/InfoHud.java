package net.foxes4life.foxclient.gui;

import com.google.common.collect.Lists;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.util.ClientUtils;
import net.foxes4life.foxclient.util.ServerTickUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InfoHud {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;

    private int boxHeight = 2;
    private int boxWidth = 98;
    private final LinkedHashMap<String, Text> textList = new LinkedHashMap<>();

    private static final Identifier FOXCLIENT_TEXT = Identifier.of("foxclient", "textures/ui/branding/text.png");

    public InfoHud(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void render(DrawContext context) {
        boolean drawLogo = Main.config.get(FoxClientSetting.HudLogo, Boolean.class);
        loadList(drawLogo);

        int padding = 2;
        int border = 4;

        if (Main.config.get(FoxClientSetting.HudBackground, Boolean.class)) {
            context.fill(0, 0, boxWidth + padding * 2, boxHeight + padding * 2, 0x80000000);
            context.fill(0, boxHeight + padding * 2, boxWidth + padding * 2, boxHeight + padding * 2 + border, 0x80000000);
            context.fill(boxWidth + padding * 2, 0, boxWidth + padding * 2 + border, boxHeight + padding * 2, 0x80000000);
        }

        // draw logo
        if (drawLogo) {
            context.drawTexture(FOXCLIENT_TEXT, padding, padding, 0, 48, 96, 48, 96, 48); // todo: figure this out
            renderList(context, 40 + padding);
        } else {
            renderList(context, padding);
        }
    }

    void loadList(boolean drawLogo) {
        textList.clear();
        boxHeight = drawLogo ? 40 : 0;
        boxWidth = 100;

        boolean version = Main.config.get(FoxClientSetting.HudVersion, Boolean.class);
        boolean coords = Main.config.get(FoxClientSetting.HudCoordinates, Boolean.class);
        boolean colorcoords = Main.config.get(FoxClientSetting.HudCoordinatesColor, Boolean.class);
        boolean fps = Main.config.get(FoxClientSetting.HudFPS, Boolean.class);
        boolean ping = Main.config.get(FoxClientSetting.HudPing, Boolean.class);
        boolean tps = Main.config.get(FoxClientSetting.HudTps, Boolean.class);
        boolean server = Main.config.get(FoxClientSetting.HudServerIP, Boolean.class);
        boolean biome = Main.config.get(FoxClientSetting.HudBiome, Boolean.class);


        if (version)
            textList.put("version", TextUtils.string(Main.SIMPLE_VERSION));

        if (coords) {
            assert this.client.player != null;
            textList.put("xyz", TextUtils.string(String.format((colorcoords ? "§c%s §a%s §9%s" : "%s %s %s"), this.client.player.getBlockPos().getX(), this.client.player.getBlockPos().getY(), this.client.player.getBlockPos().getZ())));
        }

        if (fps)
            textList.put("fps", TextUtils.string(ClientUtils.getFPS() + "fps"));

        if (ping)
            textList.put("ping", TextUtils.string(ClientUtils.getPing() + "ms"));

        if (tps)
            textList.put("tps", TextUtils.string(ServerTickUtils.calculateServerTPS() + "tps"));

        if (biome)
            if (client.world != null) {
                assert client.getCameraEntity() != null;
                final BlockPos blockPos = client.getCameraEntity().getBlockPos();
                textList.put("biome", TextUtils.string(String.format(client.world.getBiome(blockPos).getKey().get().getValue().toString())));
            }

        if (server) {
            if (client.getCurrentServerEntry() != null) {
                textList.put("ip", TextUtils.string(String.format(client.getCurrentServerEntry().address)));
            } else {
                textList.put("ip", TextUtils.string(I18n.translate("menu.singleplayer")));
            }
        }

        for (Map.Entry<String, Text> entry : textList.entrySet()) {
            boxHeight += 14;
            Text text = entry.getValue();
            boxWidth = Math.max(boxWidth, this.client.textRenderer.getWidth(text.getString()) + 20);
        }
    }

    void renderList(DrawContext context, int offset) {
        int i = 0;
        for (Map.Entry<String, Text> entry : textList.entrySet()) {
            Identifier identifier = Identifier.of("foxclient", "textures/ui/info-hud/icons/" + entry.getKey() + ".png");
            Text text = entry.getValue();

            int y = offset + (14 * i);
            context.drawTexture(identifier, 2, y - 1, 0, 0, 16, 16, 16, 16);
            context.drawText(this.fontRenderer, text, 22, y + 3, 0xFFFFFFFF, true);
            i++;
        }
    }
}
