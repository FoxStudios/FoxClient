package net.foxes4life.foxclient.gui.settings;

import net.foxes4life.foxclient.gui.FoxClientButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SettingsToggleButton extends FoxClientButton {
    public boolean displayValue;

    public SettingsToggleButton(int x, int y, int width, int height, Text message, boolean displayValue, PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.displayValue = displayValue;
    }

    @Override
    public void onPress() {
        super.onPress();
        displayValue = !displayValue;
        System.out.println("toggled lmao: " + displayValue);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;

        int color = 0x2d22e26f;
        if(!displayValue) color = 0x2dfc2d2d;
        if(this.isHovered()) {
            if(!displayValue) {
                color = 0x45fc2d2d;
            } else {
                color = 0x4522e26f;
            }
        }

        fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, color);
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;

        float text_alpha = 0.6f;
        if(this.isFocused() || this.isMouseOver(mouseX, mouseY)) {
            text_alpha = 0.75f;
        }

        drawCenteredText(matrices, textRenderer, this.getMessage(),
                this.x + this.width / 2,
                this.y + (this.height - 8) / 2,
                j | MathHelper.ceil(text_alpha * 255.0F) << 24);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(this.isMouseOver(mouseX, mouseY)) {
            this.onFocusedChanged(true);
        } else {
            if(!this.isFocused()) {
                this.onFocusedChanged(false);
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
