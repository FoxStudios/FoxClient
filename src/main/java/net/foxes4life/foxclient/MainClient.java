package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.networking.Networking;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.screen.settings.FoxClientSettingsScreen;
import net.foxes4life.foxclient.util.ZoomUtils;
import net.foxes4life.foxclient.util.freelook.FreelookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.InputSupplier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;

public class MainClient implements ClientModInitializer {
    private static final KeyBinding toggleHud = new KeyBinding("key.foxclient.toggle_hud", GLFW.GLFW_KEY_F6, "category.foxclient.main");
    private static final KeyBinding clientConfig = new KeyBinding("key.foxclient.configKey", GLFW.GLFW_KEY_RIGHT_CONTROL, "category.foxclient.main");

    @Override
    public void onInitializeClient() {
        ZoomUtils.initZoom();
        FreelookUtils.init();
        KeyBindingHelper.registerKeyBinding(toggleHud);
        KeyBindingHelper.registerKeyBinding(clientConfig);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            DiscordInstance.get().init();
            PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
            Thread networkingThread = new Thread(Networking::init);
            networkingThread.start();

            try {
                setIcon();
            } catch (Exception e) {
                Main.LOGGER.error("Failed to set icon. solution: cry", e);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                Main.konfig.set("client", "hud-enabled", !(boolean) Main.konfig.get("client", "hud-enabled"));
                try {
                    Main.konfig.save();
                } catch (IOException ignored) {}
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            while (clientConfig.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new FoxClientSettingsScreen());
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            FreelookUtils.tick();
        });
    }

    private void setIcon() {
        Main.LOGGER.info("Setting icon...");

        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        DefaultResourcePack pack = client.getDefaultResourcePack();

        InputSupplier<InputStream> icon16 = getResourceSupplier(pack, "icon", "icon_16x16.png");
        InputSupplier<InputStream> icon32 = getResourceSupplier(pack, "icon", "icon_32x32.png");

        window.setIcon(icon16, icon32);
    }

    private InputSupplier<InputStream> getResourceSupplier(DefaultResourcePack pack, String... segments) {
        InputSupplier<InputStream> inputSupplier = pack.openRoot(segments);

        if (inputSupplier == null) {
            throw new RuntimeException("Failed to get resource supplier for " + String.join("/", segments));
        } else {
            return inputSupplier;
        }
    }
}
