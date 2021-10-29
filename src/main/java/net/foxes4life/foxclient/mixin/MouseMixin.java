package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.client.Client;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if(action != 1) return;
        if(button == 0) { // left click
            Client.clicks_left.add(System.currentTimeMillis());
            Client.clicks_left.removeIf(clickTime -> System.currentTimeMillis() - clickTime >= 1000);
            return;
        }
        if(button == 1) { // right click
            Client.clicks_right.add(System.currentTimeMillis());
            Client.clicks_right.removeIf(clickTime -> System.currentTimeMillis() - clickTime >= 1000);
        }
    }
}
