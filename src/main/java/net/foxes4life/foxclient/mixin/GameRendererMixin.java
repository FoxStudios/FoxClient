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
    @Inject(at = @At("TAIL"), method = "getFov", cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if(ZoomUtils.zoomin()) {
            cir.setReturnValue(cir.getReturnValue() * ZoomUtils.zoomModifier);
        } else {
            ZoomUtils.zoomModifier = 0.2F;
        }
        ZoomUtils.smoothCam();
    }
}
