package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.minecraft.client.MinecraftClient;

public class MainClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        for (String s : Main.config.getInstance().things.keySet()) {
            System.out.println("key: " + s);

        }

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("Minecraft has started!");
            DiscordInstance.get().init();
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
        });
    }
}
