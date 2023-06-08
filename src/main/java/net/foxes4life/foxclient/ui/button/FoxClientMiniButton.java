package net.foxes4life.foxclient.ui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FoxClientMiniButton extends TexturedButtonWidget {
    private final Screen parent;
    private final Text tooltip;

    public FoxClientMiniButton(Screen parent, int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction, Text text, Text tooltip) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, text);
        this.parent = parent;
        this.tooltip = tooltip;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (mouseX > getX() && mouseX < getX() + width && mouseY > getY() && mouseY < getY() + height) {
            MinecraftClient mc = MinecraftClient.getInstance();
            parent.renderWithTooltip(context, mouseX, mouseY, delta); // todo: figure out if this is correct
            // original: parent.renderOrderedTooltip(context, mc.textRenderer.wrapLines(tooltip, 200), mouseX, mouseY);
        }

        super.renderButton(context, mouseX, mouseY, delta);
    }
}
