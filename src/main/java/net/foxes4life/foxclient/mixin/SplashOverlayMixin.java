package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin extends Overlay {
    protected SplashOverlayMixin() {
    }

    private static final Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg.png");

    @Mutable
    @Final
    @Shadow static Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "init")
    private static void init(MinecraftClient client, CallbackInfo ci) {
        LOGO = new Identifier("foxclient", "textures/foxclientsplash.png");
        client.getTextureManager().registerTexture(BACKGROUND, TextureUtil.fromIdentifier(BACKGROUND));
    }

    /*
    @Inject(at = @At("HEAD"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int height = this.client.getWindow().getScaledHeight();
        int width = this.client.getWindow().getScaledWidth();
        //ci.cancel();

        MinecraftClient.getInstance().getTextureManager().bindTexture(BACKGROUND);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(1, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, 0, 0, width, height, 0.0F, 0.0F, 16, 128, 16, 128);
    }
    */
}
