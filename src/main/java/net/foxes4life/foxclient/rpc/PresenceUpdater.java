package net.foxes4life.foxclient.rpc;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.foxes4life.foxclient.Main;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;

public class PresenceUpdater {
    public static String stateLine = "placeholder";
    public static String largeImage = "main";
    private static State currentState = State.INIT;

    public static void setState(State state) {
        currentState = state;

        switch (state) {
            case INIT -> stateLine = "Initialising";
            case IDLE -> stateLine = I18n.translate("foxclient.rpc.state.idle");
            case SINGLEPLAYER -> stateLine = I18n.translate("foxclient.rpc.state.singleplayer");
            case LAN -> stateLine = I18n.translate("foxclient.rpc.state.lan");
            case MULTIPLAYER -> {
                if ((boolean) Main.konfig.get("misc", "discord-rpc-show-ip")) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    ServerInfo server = client.getCurrentServerEntry();

                    if (server != null) {
                        ClientPlayNetworkHandler handler = client.getNetworkHandler();

                        if (handler != null) {
                            int playerCount = handler.getPlayerList().size();
                            String translationKey = playerCount == 1 ? "foxclient.rpc.state.multiplayer.count.single" : "foxclient.rpc.state.multiplayer.count";

                            stateLine = I18n.translate(translationKey, server.address, playerCount);
                        } else {
                            stateLine = I18n.translate("foxclient.rpc.state.multiplayer", server.address);
                        }
                    } else {
                        stateLine = I18n.translate("foxclient.rpc.state.multiplayer.hide_ip");
                    }
                } else {
                    stateLine = I18n.translate("foxclient.rpc.state.multiplayer.hide_ip");
                }
            }
            case REALMS -> stateLine = I18n.translate("foxclient.rpc.state.realms");
            default -> stateLine = "unimplemented!";
        }

        if ((boolean) Main.konfig.get("misc", "discord-rpc") && Discord.initialised) {
            DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(stateLine)
                    .setDetails("Playing Minecraft " + SharedConstants.getGameVersion().getName())
                    .setBigImage(largeImage, "FoxClient - A private Minecraft Mod")
                    .setSmallImage("", "assets by flustix uwu");
            DiscordInstance.get().setActivity(builder);
        }
    }

    public static State getCurrentState() {
        return currentState;
    }
}