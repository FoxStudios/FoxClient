package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.ZoomUtils;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomUtils.zoomin()) {
            ci.cancel(); // Prevent default scroll behavior while zooming

            // Adjust zoomed FOV modifier within safe bounds
            if (vertical > 0) {
                ZoomUtils.zoomedFov = Math.max(0.05F, ZoomUtils.zoomedFov - 0.05F);
            } else {
                ZoomUtils.zoomedFov = Math.min(1.0F, ZoomUtils.zoomedFov + 0.05F);
            }

            // Update target zoom level to reflect scroll
            ZoomUtils.currentZoomLevel = ZoomUtils.actualZoomLevel * ZoomUtils.zoomedFov;
        }
    }
}