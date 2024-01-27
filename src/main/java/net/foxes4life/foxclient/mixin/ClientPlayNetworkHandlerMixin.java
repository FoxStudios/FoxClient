package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.SessionConstants;
import net.foxes4life.foxclient.networking.Networking;
import net.foxes4life.foxclient.networking.shared.HashUtils;
import net.foxes4life.foxclient.util.ServerTickUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import net.minecraft.network.ClientConnection;
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
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(at = @At("RETURN"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ServerTickUtils.onJoin();

        ServerInfo serverEntry = this.client.getCurrentServerEntry();
        if (serverEntry != null) {
            System.out.println("Joined server " + serverEntry.address);
            Networking.joinServer(serverEntry.address);
            if (this.client.world != null) {
                for (AbstractClientPlayerEntity player : this.client.world.getPlayers()) {
                    String uuid = player.getUuidAsString().replace("-", "");
                    String hash = HashUtils.hash(uuid);
                    SessionConstants.UUID_HASHES.put(uuid, hash);
                }
            } else {
                System.out.println("World is null");
            }
        }
    }

    /* todo: update this
    @Inject(at = @At("HEAD"), method = "onDisconnected")
    public void onDisconnected(Text reason, CallbackInfo ci) {
        SessionConstants.LAST_SERVER = this.client.getCurrentServerEntry();

        // todo: make this trigger on all types of disconnects
        ServerInfo serverEntry = this.client.getCurrentServerEntry();
        if (serverEntry != null) {
            System.out.println("Left server " + serverEntry.address);
            Networking.leaveServer(serverEntry.address);
        }
    }
    */

    @Inject(at = @At("TAIL"), method = "onWorldTimeUpdate")
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        ServerTickUtils.onWorldTimeUpdate(packet);
    }
}