package net.foxes4life.foxclient.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.lang.reflect.Field;

public class ClientUtils {
    private static Field fps_field = null;

    public static int getFPS() {
        int fps = 0;
        try {
            if (fps_field == null) {
                String fps_field_name;
                if (FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace().equals("named")) {
                    fps_field_name = "currentFps";
                } else {
                    fps_field_name = "field_1738";
                }
                fps_field = MinecraftClient.class.getDeclaredField(fps_field_name);
                fps_field.setAccessible(true);
            }

            fps = (int) fps_field.get(MinecraftClient.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            String debugFpsString = MinecraftClient.getInstance().fpsDebugString;
            debugFpsString = debugFpsString.substring(0, debugFpsString.indexOf(" "));

            try {
                fps = Integer.parseInt(debugFpsString);
            } catch (NumberFormatException ignored) {
            }
        }

        return fps;
    }


    private static long lastPing = System.currentTimeMillis();
    private static int PING = 0;

    public static int getPing() {
        if (MinecraftClient.getInstance().isInSingleplayer()) {
            return 0;
        }

        if (System.currentTimeMillis() - lastPing > 2000) {
            lastPing = System.currentTimeMillis();
            PING = ping();
        }
        return PING;
    }

    private static int ping() {
        int ping = 0;
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
            PlayerListEntry e = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
            if (e != null) {
                ping = e.getLatency();
            }
        }
        return ping;
    }
}
