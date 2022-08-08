package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.ZoomUtils;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method = "onMouseScroll", cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomUtils.zoomin()) {
            ci.cancel();
            if (vertical > 0) {
                if (!(ZoomUtils.zoomModifier < 0.1)) ZoomUtils.zoomModifier -= 0.05;
            } else {
                if (!(ZoomUtils.zoomModifier > 1)) ZoomUtils.zoomModifier += 0.05;
            }
        }
    }
}
