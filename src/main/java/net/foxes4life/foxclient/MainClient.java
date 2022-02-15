package net.foxes4life.foxclient;

import net.foxes4life.foxclient.client.Client;
import net.foxes4life.foxclient.discord.DiscordMinecraftClient;
import net.foxes4life.foxclient.discord.PresenceUpdater;
import net.foxes4life.foxclient.hud.ClientOverlayHud;
import net.foxes4life.foxclient.networking.ServerTickStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

public class MainClient implements ClientModInitializer {

    public static KeyBinding toggleHud;
    public static KeyBinding freeLook;

    @Override
    public void onInitializeClient() {
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.foxclient.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "category.foxclient.main"
        ));

        freeLook = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.foxclient.freelook",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_5,
                "category.foxclient.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                if(Main.hudEnabled) {
                    Main.config_instance.set("hud_enabled", false);
                    Main.hudEnabled = false;
                } else {
                    Main.config_instance.set("hud_enabled", true);
                    Main.hudEnabled = true;
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            System.out.println("Minecraft has started!");
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
        });

        ClientOverlayHud.colors = new ArrayList<>();
        int steps = 120;
        for (int r=0; r<steps; r++) ClientOverlayHud.colors.add(new Color(r*255/steps,       255,         0));
        for (int g=steps; g>0; g--) ClientOverlayHud.colors.add(new Color(      255, g*255/steps,         0));
        for (int b=0; b<steps; b++) ClientOverlayHud.colors.add(new Color(      255,         0, b*255/steps));
        for (int r=steps; r>0; r--) ClientOverlayHud.colors.add(new Color(r*255/steps,         0,       255));
        for (int g=0; g<steps; g++) ClientOverlayHud.colors.add(new Color(        0, g*255/steps,       255));
        for (int b=steps; b>0; b--) ClientOverlayHud.colors.add(new Color(        0,       255, b*255/steps));
        ClientOverlayHud.colors.add(                            new Color(        0,       255,         0  ));
        System.out.println("Color array size: "+ClientOverlayHud.colors.size());

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            Client.clientTick();
            ServerTickStuff.onClientTick();
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            ServerTickStuff.onClientTickEnd();
        });
    }
}
