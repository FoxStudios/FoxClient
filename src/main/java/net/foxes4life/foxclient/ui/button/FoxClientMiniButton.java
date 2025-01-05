package net.foxes4life.foxclient.ui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FoxClientMiniButton extends TexturedButtonWidget {
    private final Text tooltip;

    public FoxClientMiniButton(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text text, Text tooltip) {
        super(x, y, width, height, textures, pressAction, text);
        this.tooltip = tooltip;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (mouseX > getX() && mouseX < getX() + width && mouseY > getY() && mouseY < getY() + height) {
            MinecraftClient mc = MinecraftClient.getInstance();
            context.drawOrderedTooltip(mc.textRenderer, mc.textRenderer.wrapLines(tooltip, 200), mouseX, mouseY);
        }

        super.renderWidget(context, mouseX, mouseY, delta);
    }
}
