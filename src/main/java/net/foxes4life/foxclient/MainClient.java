package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {
    private static final KeyBinding zoomKey = new KeyBinding("key.foxclient.zoom", GLFW.GLFW_KEY_C, "category.foxclient.main");
    private static final KeyBinding toggleHud = new KeyBinding("key.foxclient.toggle_hud", GLFW.GLFW_KEY_F6, "category.foxclient.main");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(zoomKey);
        KeyBindingHelper.registerKeyBinding(toggleHud);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("Minecraft has started!");
            DiscordInstance.get().init();
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                if(Main.config_instance.getBoolean("misc", "hud-enabled")) {
                    Main.config_instance.set("misc", "hud-enabled",
                            Main.config_instance.getEntry("misc", "hud-enabled").setValue(false));
                } else {
                    Main.config_instance.set("misc", "hud-enabled",
                            Main.config_instance.getEntry("misc", "hud-enabled").setValue(true));
                }
            }
        });
    }

    public static boolean zoomEnabled() {
        return zoomKey.isPressed();
    }
}
