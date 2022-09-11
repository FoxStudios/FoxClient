package net.foxes4life.foxclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.networking.WebSocketClientImpl;
import net.foxes4life.foxclient.networking.shared.LowWebsocketPacket;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.screen.settings.FoxClientSettingsScreen;
import net.foxes4life.foxclient.util.ZoomUtils;
import net.foxes4life.foxclient.util.freelook.FreelookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Session;
import org.java_websocket.client.WebSocketClient;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
            connectToBackend();
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

    private void connectToBackend() {
        Session playerSession = MinecraftClient.getInstance().getSession();
        try {
            URI uri = new URI("wss://backend.client.foxes4life.net");
            WebSocketClient client = new WebSocketClientImpl(uri);
            client.connectBlocking();

            List<Byte> packet = new ArrayList<>();
            packet.add(LowWebsocketPacket.C2S_LOGIN_REQUEST.getId()); // packet id

            byte[] balls = (playerSession.getUsername() + "\0" + playerSession.getUuid()).getBytes();
            for (byte b : balls) {
                packet.add(b);
            }

            byte[] packetArray = new byte[packet.size()];
            for (int i = 0; i < packet.size(); i++) {
                packetArray[i] = packet.get(i);
            }
            client.send(packetArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
