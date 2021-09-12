package net.ddns.rootrobo.foxclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TexturedButtonWidget.class)
public abstract class TexturedButtonWidgetMixin extends ClickableWidget {
    public TexturedButtonWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Final @Shadow private Identifier texture;
    @Final @Shadow private int v;
    @Final @Shadow private int u;
    @Final @Shadow private int hoveredVOffset;
    @Final @Shadow private int textureWidth;
    @Final @Shadow private int textureHeight;

    @Inject(at = @At("INVOKE"), method = "renderButton", cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        int offset = this.v;
        if (this.isHovered() && this.active) {
            offset += this.hoveredVOffset;
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend(); // allow transparency
        drawTexture(matrices, this.x, this.y, (float)this.u, offset, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }
}
