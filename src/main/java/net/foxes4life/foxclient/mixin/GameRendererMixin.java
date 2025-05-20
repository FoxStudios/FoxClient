package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.ZoomUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("TAIL"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double baseFov = cir.getReturnValue();

        // Handle key state and smooth camera toggle
        ZoomUtils.smoothCam();

        // Set the desired target zoom level based on key state
        if (ZoomUtils.zoomin()) {
            ZoomUtils.currentZoomLevel = baseFov * ZoomUtils.zoomedFov;
        } else {
            ZoomUtils.currentZoomLevel = baseFov;
        }

        // Smoothly interpolate toward target
        ZoomUtils.calculateZoom();

        // Override FOV with interpolated zoom value
        cir.setReturnValue(ZoomUtils.actualZoomLevel);
    }
}