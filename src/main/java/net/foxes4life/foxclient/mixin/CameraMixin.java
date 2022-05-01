package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.freelook.Freelook;
import net.foxes4life.foxclient.util.freelook.FreelookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    private boolean startFreelook = true;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER))
    public void update(net.minecraft.world.BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if(!(focusedEntity instanceof PlayerEntity)) return;

        if(FreelookUtils.active()) {
            Freelook fl = (Freelook) focusedEntity;

            if(MinecraftClient.getInstance().player != null && startFreelook) {
                fl.setCameraX(MinecraftClient.getInstance().player.getYaw());
                fl.setCameraY(MinecraftClient.getInstance().player.getPitch());
                startFreelook = false;
            }

            this.setRotation(fl.getCameraX(), fl.getCameraY());
        }
    }
}