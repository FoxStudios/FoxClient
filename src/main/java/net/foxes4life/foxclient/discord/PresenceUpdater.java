package net.foxes4life.foxclient.discord;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.foxes4life.foxclient.Main;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class PresenceUpdater {
    private static String stateLine = "placeholder";
    private static String largeImage = "main";
    private static State currentState = State.INIT;

    public static void setState(State state) {
        switch (state) {
            case INIT -> stateLine = "Initialising";
            case IDLE -> stateLine = I18n.translate("foxclient.rpc.state.idle");
            case SINGLEPLAYER -> stateLine = I18n.translate("foxclient.rpc.state.singleplayer");
            case LAN -> stateLine = I18n.translate("foxclient.rpc.state.lan");
            case MULTIPLAYER -> {
                if (Main.config_instance.getBoolean("discord-rpc-show-ip")) {
                    if(MinecraftClient.getInstance().getCurrentServerEntry() != null) {
                        stateLine = I18n.translate("foxclient.rpc.state.multiplayer", MinecraftClient.getInstance().getCurrentServerEntry().address);
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

        if(Main.config_instance.getBoolean("discord-rpc") && Discord.initialised) {
            // update our discord rpc thingy lol
            DiscordRichPresence rpc = new DiscordRichPresence.Builder(stateLine)
                    .setDetails("Playing Minecraft "+SharedConstants.getGameVersion().getName())
                    .setBigImage(largeImage, "FoxClient - A private Minecraft Mod")
                    .setSmallImage("", "assets by flustix uwu")
                    .build();
            DiscordInstance.get().setActivity(rpc);
        }
    }

    public static State getCurrentState() {
        return currentState;
    }
}
