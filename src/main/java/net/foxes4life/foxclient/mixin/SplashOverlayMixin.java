package net.foxes4life.foxclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;

import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.function.IntSupplier;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
    private static final Color bgColor = new Color(0x101318);

    @Mutable
    @Final
    @Shadow
    private static int MONOCHROME_BLACK;

    @Mutable
    @Final
    @Shadow
    static Identifier LOGO;

    @Final
    @Mutable @Shadow
    private static
    int MOJANG_RED;

    @Final
    @Mutable @Shadow
    private static
    IntSupplier BRAND_ARGB;

    @Inject(at = @At("TAIL"), method = "init")
    private static void init(MinecraftClient client, CallbackInfo ci) {
        LOGO = new Identifier("foxclient", "textures/mojangstudios2.png");
        MOJANG_RED = bgColor.getRGB();
        BRAND_ARGB = () -> MinecraftClient.getInstance().options.monochromeLogo ? MONOCHROME_BLACK : MOJANG_RED;
    }
}
