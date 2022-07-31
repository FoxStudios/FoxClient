package net.foxes4life.foxclient.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.foxes4life.foxclient.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ZoomUtils {
    private static final KeyBinding zoomKey = new KeyBinding("key.foxclient.zoom", GLFW.GLFW_KEY_C, "category.foxclient.main");
    static boolean isZoomin = false;
    public static float zoomModifier = 0.2F;
    public static double currentZoomLevel = 0.2F;
    public static double actualZoomLevel = 0.2F;

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

    public static void calculateZoom(CallbackInfoReturnable<Double> cir) {
        ZoomUtils.actualZoomLevel = (boolean)Main.konfig.get("misc", "zoom") ?
                MathHelper.lerp(0.05f, ZoomUtils.actualZoomLevel, ZoomUtils.currentZoomLevel) :
                ZoomUtils.currentZoomLevel;
    }
}
