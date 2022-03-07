package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.util.ZoomUtils;
import net.foxes4life.foxclient.util.freelook.Freelook;
import net.foxes4life.foxclient.util.freelook.FreelookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {
    private static final KeyBinding toggleHud = new KeyBinding("key.foxclient.toggle_hud", GLFW.GLFW_KEY_F6, "category.foxclient.main");

    @Override
    public void onInitializeClient() {
        ZoomUtils.initZoom();
        FreelookUtils.init();
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

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            FreelookUtils.tick();
        });
    }
}
