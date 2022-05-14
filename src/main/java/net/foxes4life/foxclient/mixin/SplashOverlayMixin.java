package net.foxes4life.foxclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
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

    @Final @Mutable @Shadow
    private static int MOJANG_RED;

    @Final @Mutable @Shadow
    private static int MONOCHROME_BLACK;

    @Mutable
    @Final
    @Shadow static Identifier LOGO;

    @Shadow
    private float progress;

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "init")
    private static void init(MinecraftClient client, CallbackInfo ci) {
        LOGO = new Identifier("foxclient", "textures/foxclientsplash.png");

        MOJANG_RED = ColorHelper.Argb.getArgb(255, 32, 32, 32);
        MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 16, 16, 16);
    }

    @Inject(at = @At("HEAD"), method = "renderProgressBar", cancellable = true)
    private void renderProgressBar(MatrixStack matrices, int minX, int minY, int maxX, int maxY, float opacity, CallbackInfo ci) {
        maxX = this.client.getWindow().getScaledWidth();
        minY = this.client.getWindow().getScaledHeight();
        maxY = this.client.getWindow().getScaledHeight()-4;

        ci.cancel();
        int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
        int j = Math.round(opacity * 255.0F);
        int k = ColorHelper.Argb.getArgb(j, 255, 255, 255);
        fill(matrices, 0, minY, minX + i, maxY, k);
    }
}
