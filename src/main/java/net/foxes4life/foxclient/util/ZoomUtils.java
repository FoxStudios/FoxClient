package net.foxes4life.foxclient.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ZoomUtils {
    private static final KeyBinding zoomKey = new KeyBinding("key.foxclient.zoom", GLFW.GLFW_KEY_C, "category.foxclient.main");
    static boolean isZoomin = false;
    public static float zoomModifier = 0.2F;

    public static void initZoom () {
        KeyBindingHelper.registerKeyBinding(zoomKey);
        isZoomin = false;
    }

    public static boolean zoomin() {
        return zoomKey.isPressed();
    }

    public static void smoothCam () {
        //start holdin
        if (zoomin() && !isZoomin) {
            isZoomin = true;
            MinecraftClient.getInstance().options.smoothCameraEnabled = true;
        }

        //stop holdin
        if (!zoomin() && isZoomin) {
            isZoomin = false;
            MinecraftClient.getInstance().options.smoothCameraEnabled = false;
        }
    }
}
