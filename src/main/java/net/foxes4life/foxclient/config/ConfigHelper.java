package net.foxes4life.foxclient.config;

import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.rpc.State;
import net.minecraft.client.MinecraftClient;

public class ConfigHelper {
    // called whenever something needs to be done extra

    public static void onUpdate(String category, String key, Object value) {
        System.out.println("update " + category+"."+key + " to " + value);
        if(category.equals("misc")) {
            if(key.equals("discord-rpc")) {
                boolean enabled = (boolean) value;
                if(!enabled) {
                    System.out.println("ConfigHelper: stopping discord rpc");
                    DiscordInstance.get().stfu();
                } else {
                    System.out.println("ConfigHelper: starting discord rpc");
                    DiscordInstance.get().init();
                    PresenceUpdater.setState(State.IDLE);
                }
            }
        }
    }
}
