package net.ddns.rootrobo.foxclient.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class NicerButtonWidget extends ButtonWidget {
    /*
    private final int x_o;
    private final int y_o;
    private final int width_o;
    */

    public NicerButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
        /*
        x_o = x;
        y_o = y;
        width_o = width;
         */
    }

    private static final Identifier WIDGET_TEXTURE = new Identifier("foxclient", "textures/ui/widgets.png");

    @Override
    public void onPress() {
        super.onPress();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    /*
    @Override
    protected void onFocusedChanged(boolean newFocused) {
        super.onFocusedChanged(newFocused);
        if(newFocused) {
            x = x_o-2;
            y = y_o;
            width = width_o+4;
        } else {
            x = x_o;
            y = y_o;
            width = width_o;
        }
    }
    */

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGET_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
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
