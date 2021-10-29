package net.foxes4life.foxclient.discord;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class DiscordMinecraftClient {
    public static State getState(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            if (MinecraftClient.getInstance().getServer() != null && !MinecraftClient.getInstance().getServer().isRemote()) {
                return State.SINGLEPLAYER;
            } else if (MinecraftClient.getInstance().isConnectedToRealms()) {
                return State.REALMS;
            } else if (MinecraftClient.getInstance().getServer() == null && (MinecraftClient.getInstance().getCurrentServerEntry() == null || !MinecraftClient.getInstance().getCurrentServerEntry().isLocal())) {
                return State.MULTIPLAYER;
            } else {
                return State.LAN;
            }
        } else {
            return State.IDLE;
        }
    }
}
