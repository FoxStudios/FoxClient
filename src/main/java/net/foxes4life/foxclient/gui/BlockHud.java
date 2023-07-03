package net.foxes4life.foxclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.foxes4life.foxclient.util.TextUtils;
import net.foxes4life.foxclient.util.draw.Anchor;
import net.foxes4life.foxclient.util.draw.AnchoredBounds;
import net.foxes4life.foxclient.util.draw.DrawUtils;
import net.foxes4life.foxclient.util.rendering.ItemRender;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

    private float alpha = 0;
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
        }
        else {
            block = lastBlock;
        }

        // if the block is still null, we don't want to render anything
        if (block == null) return;

        float delta = client.getTickDelta();
        float alphaDelta = show ? .4f : -.4f;
        alpha = Math.max(0, Math.min(1, alpha + alphaDelta * delta));

        // don't even bother
        if (alpha == 0) return;

        String translationKey = block.getTranslationKey();
        MutableText text = TextUtils.translatable(translationKey);
        int w = client.textRenderer.getWidth(text);

        // transition the width
        width = MathHelper.lerp(.8f * delta, width, w);

        final int padding = 2;
        final int border = 3;

        AnchoredBounds bounds = new AnchoredBounds(0, 0, (int)width + 20 + padding * 2, 16 + padding * 2, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), Anchor.TopCenter, Anchor.TopCenter);

        Color backgroundColor = new Color(0, 0, 0, .5f * alpha);
        int backgroundColorInt = backgroundColor.getRGB();

        RenderSystem.setShaderColor(1, 1, 1, alpha);
        DrawUtils.drawRect(context, bounds, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x - border, bounds.y, border, bounds.height, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x + bounds.width, bounds.y, border, bounds.height, backgroundColorInt);
        DrawUtils.drawRect(context, bounds.x, bounds.y + bounds.height, bounds.width, border, backgroundColorInt);

        Color textColor = new Color(1, 1, 1, alpha);
        context.drawText(client.textRenderer, text, bounds.x + 20 + padding, 5 + padding, textColor.getRGB(), false);

        itemRender.render(bounds.x + padding, padding, new ItemStack(block));
    }

    private Block getBlock() {
        ClientWorld world = client.world;
        if (world == null) return null;

        Entity camera = client.getCameraEntity();
        if (camera == null) return null;

        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return null;

        float maxRange = interactionManager.getReachDistance();

        Vec3d viewVector = camera.getRotationVec(client.getTickDelta());
        Vec3d startVector = camera.getCameraPosVec(client.getTickDelta());
        Vec3d endVector = startVector.add(viewVector.x * maxRange, viewVector.y * maxRange, viewVector.z * maxRange);

        RaycastContext raycastContext = new RaycastContext(startVector, endVector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, camera);
        BlockPos blockPos = world.raycast(raycastContext).getBlockPos();

        return world.getBlockState(blockPos).getBlock();
    }
}
