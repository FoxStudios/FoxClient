package net.foxes4life.foxclient.ui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

public class FoxClientButton extends ButtonWidget {
    public FoxClientButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, Supplier::get);
    }

    private static final Identifier WIDGET_TEXTURE = Identifier.of("foxclient", "textures/ui/widgets.png");

    // todo: dont steal mojang code, bad
    int getTextureY() {
        int lvInt2 = 1;
        if (!this.active) {
            lvInt2 = 0;
        } else if (this.isHovered()) {
            lvInt2 = 2;
        }
        return lvInt2;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();


        int i = getTextureY();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.drawTexture(WIDGET_TEXTURE, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        context.drawTexture(WIDGET_TEXTURE, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

        //this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;

        float text_alpha = 0.6f;
        if (this.isFocused() || this.isMouseOver(mouseX, mouseY)) {
            text_alpha = 0.75f;
        }

        this.drawScrollableText(context, minecraftClient.textRenderer, 0, j | MathHelper.ceil(text_alpha * 255.0F) << 24);
    }

    /*@Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //        if (this.isMouseOver(mouseX, mouseY)) {
        //            this.onFocusedChanged(true);
        //        } else {
        //            if (!this.isFocused()) {
        //                this.onFocusedChanged(false);
        //            }
        //        }
    }*/
}