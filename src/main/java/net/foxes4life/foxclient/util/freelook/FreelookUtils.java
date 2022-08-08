package net.foxes4life.foxclient.util.freelook;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FreelookUtils {
    private static boolean freeLooking = false;
    private static Perspective freeLookLastPerspective;

    private static final KeyBinding freelookKey = new KeyBinding("key.foxclient.freelook", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "category.foxclient.main");

    public static void init() {
        KeyBindingHelper.registerKeyBinding(freelookKey);
    }

    public static boolean active() {
        return freelookKey.isPressed();
    }

    public static void tick() {
        if (FreelookUtils.active()) {
            if (!freeLooking) {
                freeLookLastPerspective = MinecraftClient.getInstance().options.getPerspective();
                if (freeLookLastPerspective == Perspective.FIRST_PERSON)
                    MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
                freeLooking = true;
            }
        } else {
            if (MinecraftClient.getInstance().player != null) {
                ((Freelook) (MinecraftClient.getInstance().player)).setCameraX(MinecraftClient.getInstance().player.getYaw());
                ((Freelook) (MinecraftClient.getInstance().player)).setCameraY(MinecraftClient.getInstance().player.getPitch());
            }

            if (freeLooking) {
                MinecraftClient.getInstance().options.setPerspective(freeLookLastPerspective);
                freeLooking = false;
            }
        }
    }
}

