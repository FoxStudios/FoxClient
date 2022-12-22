package net.foxes4life.foxclient.rpc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.foxes4life.foxclient.Main;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Timer;
import java.util.TimerTask;

public class Discord implements ReadyCallback {
    private static final String playerIconAPI = "https://visage.surgeplay.com/face/128/";

    public Thread CALLBACK_THREAD = null;
    public long START_TIME = 0L;
    public static boolean initialised = false;

    public void init() {
        Thread initDiscord = new Thread(() -> {
            while (!(MinecraftClient.getInstance() != null && !MinecraftClient.getInstance().fpsDebugString.equals(""))) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            if ((boolean) Main.konfig.get("misc", "discord-rpc")) {
                try {
                    initDC();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        initDiscord.setName("initDiscord");
        initDiscord.start();
    }

    public void stfu() {
        DiscordRPC.discordClearPresence();
        DiscordRPC.discordShutdown();

        if (CALLBACK_THREAD != null) //noinspection deprecation
            CALLBACK_THREAD.stop();
        initialised = false;
    }

    public void initDC() {
        START_TIME = System.currentTimeMillis();
        DiscordRPC.discordInitialize("805552222985388063", new DiscordEventHandlers.Builder().setReadyEventHandler(this).build(), true);
        DiscordRPC.discordRunCallbacks();

        // Run callbacks forever
        CALLBACK_THREAD = new Thread(() -> new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runCallback();
            }
        }, 100, 1024));
        CALLBACK_THREAD.setName("discordCallbackThread");
        CALLBACK_THREAD.start();
    }

    public void setActivity(DiscordRichPresence.Builder presenceBuilder) {
        if (!initialised) {
            Main.LOGGER.warn("setActivity called before RPC initialized!");
            return;
        }

        if ((boolean) Main.konfig.get("misc", "discord-rpc")) {
            if (START_TIME == 0L) {
                START_TIME = System.currentTimeMillis();
            }

            presenceBuilder.setDetails("Playing Minecraft " + SharedConstants.getGameVersion().getName());
            presenceBuilder.setBigImage(PresenceUpdater.largeImage, "FoxClient " + Main.VERSION);
            presenceBuilder.setStartTimestamps(START_TIME);

            if ((boolean) Main.konfig.get("misc", "discord-rpc-show-player")) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    presenceBuilder.setSmallImage(playerIconAPI + player.getUuidAsString(), player.getName().getString());
                }
            }

            DiscordRPC.discordUpdatePresence(presenceBuilder.build());
        }
    }

    private void runCallback() {
        if ((boolean) Main.konfig.get("misc", "discord-rpc")) {
            DiscordRPC.discordRunCallbacks();
        }
    }

    @Override
    public void apply(DiscordUser user) {
        Main.LOGGER.info("RPC loaded!");
        Main.LOGGER.info("Username: " + user.username + "#" + user.discriminator);
        initialised = true;

        DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder("Initialising")
                .setStartTimestamps(START_TIME)
                .setBigImage("main", null).build());
    }
}