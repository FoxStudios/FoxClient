package net.foxes4life.foxclient.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;

public class ClientUtils {
    private static Field fps_field = null;

    public static int getFPS() {
        int fps = 0;
        try {
            if(fps_field == null) {
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
}
