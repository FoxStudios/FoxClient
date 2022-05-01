package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.SessionConstants;
import net.foxes4life.foxclient.util.ServerTickUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ServerTickUtils.onJoin();
    }

    @Inject(at = @At("HEAD"), method = "onDisconnected")
    public void onDisconnected(Text reason, CallbackInfo ci) {
        SessionConstants.LAST_SERVER = this.client.getCurrentServerEntry();
    }

    @Inject(at = @At("TAIL"), method = "onWorldTimeUpdate")
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        ServerTickUtils.onWorldTimeUpdate(packet);
    }
}
