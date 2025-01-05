package net.foxes4life.foxclient.gui;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.MainClient;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.util.TextUtils;
import net.foxes4life.foxclient.util.draw.Anchor;
import net.foxes4life.foxclient.util.draw.AnchoredBounds;
import net.foxes4life.foxclient.util.draw.DrawUtils;
import net.foxes4life.foxclient.util.rendering.ItemRender;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;

public class BlockHud {
    private final MinecraftClient client;
    private Block lastBlock;

    private float y = 0;
    private float width = 0;

    public BlockHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(DrawContext context, ItemRender itemRender) {
        Block block = getBlock();
        boolean show = false;

        if (block != null && !(block instanceof AirBlock)) {
            lastBlock = block;
            show = true;
        } else {
            block = lastBlock;
        }

        // if the block is still null, we don't want to render anything
        if (block == null) return;

        boolean showAnimations = Main.config.get(FoxClientSetting.BlockHudAnimations, Boolean.class);

        float delta = MainClient.deltaTime;
//        context.drawText(client.textRenderer, "Draw delta: " + (int)delta + "ms", 10, 10, 0xFFFFFF, false);

        final int padding = 2;
        final int border = 3;
        final int height = 16;

        final int minY = -height - padding * 2 - border - 2;
        int goalY = show ? 0 : minY;

        // transition y position
        if (showAnimations) {
            y = (float) MathHelper.lerp(Math.exp(-0.01 * delta), goalY, y);
        } else {
            y = goalY;
        }

        // don't even bother if its off screen
        if (y == minY) return;

        MutableText text = TextUtils.translatable(block.getTranslationKey());
        int w = client.textRenderer.getWidth(text);

        // transition the width
        if (showAnimations) {
            width = (float) MathHelper.lerp(Math.exp(-0.01 * delta), w, width);
        } else {
            width = w;
        }

        AnchoredBounds bounds = new AnchoredBounds(0, (int)y, (int)width + 20 + padding * 2, height + padding * 2, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), Anchor.TopCenter, Anchor.TopCenter);
        Color backgroundColor = new Color(0, 0, 0, .5f);
        int backgroundColorInt = backgroundColor.getRGB();

        DrawUtils.drawRect(context, bounds, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x - border, bounds.y, border, bounds.height, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x + bounds.width, bounds.y, border, bounds.height, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x, bounds.y + bounds.height, bounds.width, border, backgroundColorInt);

        context.drawText(client.textRenderer, text, bounds.x + 20 + padding, bounds.y + 5 + padding, 0xFFFFFF, false);
        itemRender.render(bounds.x + padding,  bounds.y + padding, new ItemStack(block));
    }

    private Block getBlock() {
        ClientWorld world = client.world;
        Entity camera = client.getCameraEntity();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (world == null || camera == null || interactionManager == null) return null;

        float maxRange = 4.f; //interactionManager.getReachDistance();
        float tickDelta = client.getRenderTickCounter().getTickDelta(false); // todo: idk?

        Vec3d viewVector = camera.getRotationVec(tickDelta);
        Vec3d startVector = camera.getCameraPosVec(tickDelta);
        Vec3d endVector = startVector.add(viewVector.x * maxRange, viewVector.y * maxRange, viewVector.z * maxRange);

        RaycastContext raycastContext = new RaycastContext(startVector, endVector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, camera);
        BlockPos blockPos = world.raycast(raycastContext).getBlockPos();

        return world.getBlockState(blockPos).getBlock();
    }
}
