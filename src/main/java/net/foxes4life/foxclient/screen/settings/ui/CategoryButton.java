package net.foxes4life.foxclient.screen.settings.ui;

import net.foxes4life.foxclient.ui.button.FoxClientButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class CategoryButton extends FoxClientButton {
    public boolean selected;
    public String categoryId;

    public CategoryButton(int x, int y, int width, int height, Text message, boolean selected, String categoryId, PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.selected = selected;
        this.categoryId = categoryId;
    }

    @Override
    public void onPress() {
        super.onPress();
    }

    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }*/

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        int color = 0x11ffffff;
        //if(this.isHovered() || this.selected) color = 0x45ffffff;
        fill(matrices, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;

        float text_alpha = 0.6f;
        if (this.isFocused() || this.isMouseOver(mouseX, mouseY)) {
            text_alpha = 0.75f;
        }

        drawCenteredText(matrices, textRenderer, this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                j | MathHelper.ceil(text_alpha * 255.0F) << 24);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.onFocusedChanged(true);
        } else {
            if (!this.isFocused()) {
                this.onFocusedChanged(false);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}