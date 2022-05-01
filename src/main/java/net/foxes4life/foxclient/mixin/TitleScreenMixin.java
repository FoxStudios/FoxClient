package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.screen.mainmenu.FoxClientTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Main.config_instance.getBoolean("client", "customMenu")) {
            ci.cancel();
            MinecraftClient.getInstance().setScreen(new FoxClientTitleScreen(false));
        }
    }
}
