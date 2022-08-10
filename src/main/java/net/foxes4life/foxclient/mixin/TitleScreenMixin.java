package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.screen.title.TitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screen.TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if ((boolean) Main.konfig.get("menus", "mainmenu")) {
            ci.cancel();
            MinecraftClient.getInstance().setScreen(new TitleScreen(false));
        }
    }
}
