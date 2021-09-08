package net.ddns.rootrobo.foxclient.discord;

import de.jcm.discordgamesdk.activity.Activity;
import net.ddns.rootrobo.foxclient.Main;
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

        //noinspection unchecked
        if(Main.config_instance.getBoolean("discord-rpc") && Discord.initialised) {
            // update our discord rpc thingy lol
            Activity dca = new Activity();
            dca.setDetails("Playing Minecraft "+SharedConstants.getGameVersion().getName());
            dca.setState(stateLine);
            dca.assets().setLargeImage(largeImage);
            dca.assets().setLargeText("FoxClient - A private Minecraft Mod");
            dca.assets().setSmallText("assets by flustix uwu");
            Discord.setActivity(dca);
        }
    }

    public static State getCurrentState() {
        return currentState;
    }
}
