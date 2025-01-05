package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.screen.TitleScreen;
import net.foxes4life.foxclient.screen.UpdateScreen;
import net.foxes4life.foxclient.util.update.UpdateChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screen.TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (UpdateChecker.updateAvailable() && !UpdateChecker.dismissed) {
            MinecraftClient.getInstance().setScreen(new UpdateScreen());
            ci.cancel();
            return;
        }

        if (Main.config.get(FoxClientSetting.CustomMainMenu, Boolean.class)) {
            ci.cancel();
            MinecraftClient.getInstance().setScreen(new TitleScreen(false));
        }
    }
}
