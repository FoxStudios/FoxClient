package net.foxes4life.foxclient;

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

    @Override
    public void onInitializeClient() {
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.foxclient.toggle_hud", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F6, // The keycode of the key
                "category.foxclient.hud" // The translation key of the keybinding's category.
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

        //Registry.register(Registry.SOUND_EVENT, MOJANG_INTRO_SOUND_ID, MOJANG_INTRO_SOUND_EVENT);
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
            ServerTickStuff.onClientTick();
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            ServerTickStuff.onClientTickEnd();
        });
    }

    /*
    private void playMojangIntroSound() {
        try {
            InputStream thing = MinecraftClient.getInstance().getResourcePackDownloader().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("foxclient", "sounds/mojang_intro.wav"));
            if(thing == null) {
                System.out.println("NO INTRO SOUND");
            } else {
                BufferedInputStream bis = new BufferedInputStream(thing);

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bis);

                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float range = vol.getMaximum() - vol.getMinimum();
                float volume = (range * MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER)) + vol.getMinimum();

                vol.setValue(volume);

                clip.start();
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    */

    /*
    private SoundEvent getLoadingSound() {
        return Registry.SOUND_EVENT.get(MOJANG_INTRO_SOUND_EVENT.getId());
    }
    */
}
