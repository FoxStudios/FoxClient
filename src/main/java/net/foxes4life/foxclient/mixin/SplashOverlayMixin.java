package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.ui.Progressbar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin extends Overlay {
    protected SplashOverlayMixin() {
    }

    @Final
    @Mutable
    @Shadow
    private static int MOJANG_RED;

    @Final
    @Mutable
    @Shadow
    private static int MONOCHROME_BLACK;

    @Mutable
    @Final
    @Shadow
    static Identifier LOGO;

    @Shadow
    private float progress;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    private static void init(MinecraftClient client, CallbackInfo ci) {
        ci.cancel();
        LOGO = new Identifier("foxclient", "textures/ui/splash/foxclient.png");

        MOJANG_RED = ColorHelper.Argb.getArgb(255, 32, 32, 32);
        MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 16, 16, 16);
    }

    @Unique
    private Progressbar progressbar;

    @Inject(at = @At("HEAD"), method = "renderProgressBar", cancellable = true)
    private void renderProgressBar(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity, CallbackInfo ci) {
        ci.cancel();

        if (progressbar == null)
            progressbar = new Progressbar(minX, minY, maxX - minX, maxY - minY);

        progressbar.x = minX;
        progressbar.y = minY;
        progressbar.width = maxX - minX;
        progressbar.height = maxY - minY;
        progressbar.render(context, progress);
    }

    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendEquation(I)V", ordinal = 0))
    private void blendEquationLogo(int mode) {
        // do nothing
    }*/

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFunc(II)V", ordinal = 0))
    private void blendFuncLogo(int srcFactor, int dstFactor) {
        // do nothing
    }
}
