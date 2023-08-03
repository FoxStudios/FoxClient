package net.foxes4life.foxclient.util.rendering;

import net.minecraft.item.ItemStack;

public interface ItemRender {
    void render(int x, int y, ItemStack item);
}