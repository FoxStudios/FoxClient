package net.foxes4life.foxclient.rpc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;

public class DiscordMinecraftClient {
    public static State getState(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverEntry = client.getCurrentServerEntry();

        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            if (client.getServer() != null && !client.getServer().isRemote()) {
                return State.SINGLEPLAYER;
            } else if (serverEntry != null && serverEntry.isRealm()) {
                return State.REALMS;
            } else if (client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal())) {
                return State.MULTIPLAYER;
            } else {
                return State.LAN;
            }
        } else {
            return State.IDLE;
        }
    }
}
