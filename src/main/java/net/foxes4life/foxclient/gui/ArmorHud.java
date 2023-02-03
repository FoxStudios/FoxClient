package net.foxes4life.foxclient.gui;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.util.draw.Anchor;
import net.foxes4life.foxclient.util.draw.AnchoredBounds;
import net.foxes4life.foxclient.util.draw.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class ArmorHud extends DrawableHelper {
    private final MinecraftClient client;

    public ArmorHud(MinecraftClient client) {
        this.client = client;
        Main.LOGGER.info(client.getWindow().getWidth() + " " + client.getWindow().getHeight());
        Main.LOGGER.info(client.getWindow().getScaledWidth() + " " + client.getWindow().getScaledHeight());
    }

    public void render(MatrixStack matrices) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        AnchoredBounds bounds = new AnchoredBounds(0, 0, 230, 80, width, height, Anchor.BottomCenter, Anchor.BottomCenter);
        DrawUtils.drawRect(matrices, bounds, 0x80000000);
    }
}
