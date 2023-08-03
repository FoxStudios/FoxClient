package net.foxes4life.foxclient.gui;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.util.draw.Anchor;
import net.foxes4life.foxclient.util.draw.AnchoredBounds;
import net.foxes4life.foxclient.util.draw.Bounds;
import net.foxes4life.foxclient.util.rendering.ItemRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ArmorHud {
    private final MinecraftClient client;

    private final TextRenderer fontRenderer;

    public ArmorHud(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;

        Main.LOGGER.info(client.getWindow().getWidth() + " " + client.getWindow().getHeight());
        Main.LOGGER.info(client.getWindow().getScaledWidth() + " " + client.getWindow().getScaledHeight());
    }

    public void render(DrawContext context, ItemRender itemRender) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        AnchoredBounds bounds = new AnchoredBounds(0, 0, 220, 85, width, height, Anchor.BottomCenter, Anchor.BottomCenter);

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        renderItem(context, itemRender, player, EquipmentSlot.MAINHAND, bounds);
        renderItem(context, itemRender, player, EquipmentSlot.OFFHAND, bounds);
        renderItem(context, itemRender, player, EquipmentSlot.HEAD, bounds);
        renderItem(context, itemRender, player, EquipmentSlot.CHEST, bounds);
        renderItem(context, itemRender, player, EquipmentSlot.LEGS, bounds);
        renderItem(context, itemRender, player, EquipmentSlot.FEET, bounds);
    }

    private void renderItem(DrawContext context, ItemRender itemRender, ClientPlayerEntity player, EquipmentSlot slot, Bounds bounds) {
        int x = bounds.x;
        int y = bounds.y;
        boolean right = slot == EquipmentSlot.OFFHAND || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;

        switch (slot) {
            case MAINHAND:
                break;
            case OFFHAND:
                x += bounds.width - 16;
                break;
            case HEAD:
                y += 20;
                break;
            case CHEST:
                y += 40;
                break;
            case LEGS:
                x += bounds.width - 16;
                y += 20;
                break;
            case FEET:
                x += bounds.width - 16;
                y += 40;
                break;
        }

        ItemStack stack = player.getEquippedStack(slot);
        if (!stack.isEmpty()) {
            itemRender.render(x, y, stack);

            String text = "";
            int color = 0xFFFFFF;

            if (stack.getMaxDamage() > 0) {
                boolean showPercent = Main.config.get(FoxClientSetting.ArmorHudDisplayPercentage, Boolean.class);

                float percent = (float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();

                if (percent < 0.1f) {
                    color = 0xFF5555;
                } else if (percent < 0.25f) {
                    color = 0xFFFF55;
                } else {
                    color = 0x00FF55;
                }

                if (showPercent)
                    text = (int) (percent * 100) + "%";
                else
                    text = String.valueOf(stack.getMaxDamage() - stack.getDamage());
            } else {
                int totalCount = 0;

                for (ItemStack itemStack : player.getInventory().main) {
                    if (itemStack.getItem().getTranslationKey().equals(stack.getItem().getTranslationKey())) {
                        totalCount += itemStack.getCount();
                    }
                }

                // offhand is not in the main inventory
                if (player.getEquippedStack(EquipmentSlot.OFFHAND).getItem().getTranslationKey().equals(stack.getItem().getTranslationKey()))
                    totalCount += player.getEquippedStack(slot).getCount();

                if (totalCount > 1) text = String.valueOf(totalCount);
            }

            int countWidth = fontRenderer.getWidth(text);

            int x2 = x + (right ? 20 : -4 - countWidth);
            context.drawText(fontRenderer, text, x2, y + 3, color, true);
        }
    }
}
