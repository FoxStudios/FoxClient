package net.foxes4life.foxclient.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class ZoomUtils {
    private static final KeyBinding zoomKey = new KeyBinding("key.foxclient.zoom", GLFW.GLFW_KEY_C, "category.foxclient.main");
    public static boolean isZoomin = false;

    public static float zoomedFovDefault = 0.2F;
    public static float zoomedFov = 0.2F;       // Zoomed-in FOV scale
    public static float defaultFov = 1.0F;      // Normal FOV scale

    public static double currentZoomLevel = 1.0F;   // The target zoom level
    public static double actualZoomLevel = 1.0F;    // The lerped FOV value

    public static void initZoom() {
        KeyBindingHelper.registerKeyBinding(zoomKey);
    }

    public static boolean zoomin() {
        return zoomKey.isPressed();
    }

    public static void smoothCam() {
        if (zoomin()) {
            if (!isZoomin) {
                isZoomin = true;
                MinecraftClient.getInstance().options.smoothCameraEnabled = true;
            }

            currentZoomLevel = zoomedFov;
        } else {
            if (isZoomin) {
                isZoomin = false;
                MinecraftClient.getInstance().options.smoothCameraEnabled = false;

                zoomedFov = zoomedFovDefault;
            }
            currentZoomLevel = defaultFov;
        }
    }


    public static void calculateZoom() {
        ZoomUtils.actualZoomLevel = Main.config.get(FoxClientSetting.SmoothZoom, Boolean.class) ?
                MathHelper.lerp(0.1f, (float) actualZoomLevel, (float) currentZoomLevel) :
                ZoomUtils.currentZoomLevel;
    }
}
