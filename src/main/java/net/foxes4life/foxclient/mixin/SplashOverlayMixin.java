package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
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

    private static final Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg.png");

    @Mutable
    @Final
    @Shadow static Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");

    @Inject(at = @At("TAIL"), method = "init")
    private static void init(MinecraftClient client, CallbackInfo ci) {
        LOGO = new Identifier("foxclient", "textures/foxclientsplash.png");
        client.getTextureManager().registerTexture(BACKGROUND, TextureUtil.fromIdentifier(BACKGROUND));

        MOJANG_RED = ColorHelper.Argb.getArgb(255, 32, 32, 32);
        MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 16, 16, 16);
    }
}
