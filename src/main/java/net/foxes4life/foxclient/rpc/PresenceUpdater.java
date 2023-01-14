package net.foxes4life.foxclient.rpc;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;

public class PresenceUpdater {
    public static String stateLine = "placeholder";
    public static String largeImage = "main";

    public static void setState(State state) {
        switch (state) {
            case INIT -> stateLine = "Initialising";
            case IDLE -> stateLine = I18n.translate("foxclient.rpc.state.idle");
            case SINGLEPLAYER -> stateLine = I18n.translate("foxclient.rpc.state.singleplayer");
            case LAN -> stateLine = I18n.translate("foxclient.rpc.state.lan");
            case MULTIPLAYER -> {
                if (Main.config.get(FoxClientSetting.DiscordShowIP, Boolean.class)) {
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

        if (Main.config.get(FoxClientSetting.DiscordEnabled, Boolean.class) && Discord.initialised) {
            DiscordInstance.get().setActivity(new DiscordRichPresence.Builder(stateLine));
        }
    }
}