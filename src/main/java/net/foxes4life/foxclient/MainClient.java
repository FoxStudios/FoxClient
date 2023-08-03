package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.screen.settings.FoxClientSettingsScreen;
import net.foxes4life.foxclient.util.ZoomUtils;
import net.foxes4life.foxclient.util.freelook.FreelookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;


public class MainClient implements ClientModInitializer {
    private static final KeyBinding toggleInfoHud = new KeyBinding("key.foxclient.toggle-info-hud", GLFW.GLFW_KEY_F6, "category.foxclient.main");
    private static final KeyBinding clientConfig = new KeyBinding("key.foxclient.configKey", GLFW.GLFW_KEY_RIGHT_CONTROL, "category.foxclient.main");

    public static long deltaTime = 0;

    @Override
    public void onInitializeClient() {
        ZoomUtils.initZoom();
        FreelookUtils.init();
        KeyBindingHelper.registerKeyBinding(toggleInfoHud);
        KeyBindingHelper.registerKeyBinding(clientConfig);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            DiscordInstance.get().init();
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
            //Thread networkingThread = new Thread(Networking::init);
            //networkingThread.start();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleInfoHud.wasPressed()) {
                Main.config.set(FoxClientSetting.HudEnabled, !Main.config.get(FoxClientSetting.HudEnabled, Boolean.class));
                Main.config.save();
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (clientConfig.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new FoxClientSettingsScreen(false));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            FreelookUtils.tick();
        });
    }
}
