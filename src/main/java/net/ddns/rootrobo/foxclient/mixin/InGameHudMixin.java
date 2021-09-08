package net.ddns.rootrobo.foxclient.mixin;

import net.ddns.rootrobo.foxclient.Main;
import net.ddns.rootrobo.foxclient.hud.ClientOverlayHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "render")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(Main.hudEnabled) {
            ClientOverlayHud foxhud = new ClientOverlayHud(this.client);
            foxhud.render(matrices);
        }
    }
}
