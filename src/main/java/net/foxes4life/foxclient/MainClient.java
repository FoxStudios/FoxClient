package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {
    private static final KeyBinding zoomKey = new KeyBinding("key.foxclient.zoom", GLFW.GLFW_KEY_C, "category.foxclient.main");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(zoomKey);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("Minecraft has started!");
            DiscordInstance.get().init();
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
        });
    }

    public static boolean zoomEnabled() {
        return zoomKey.isPressed();
    }
}
