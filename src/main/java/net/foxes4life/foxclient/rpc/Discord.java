package net.foxes4life.foxclient.rpc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Discord implements ReadyCallback {
    private static final String playerIconAPI = "https://visage.surgeplay.com/face/128/";

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    public Thread CALLBACK_THREAD = null;
    public long START_TIME = 0L;
    public static boolean initialised = false;

    public void init() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }

        Thread initDiscord = new Thread(() -> {
            while (!(MinecraftClient.getInstance() != null && !MinecraftClient.getInstance().fpsDebugString.isEmpty())) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            if (Main.config.get(FoxClientSetting.DiscordEnabled, Boolean.class)) {
                try {
                    initDC();
                } catch (Exception e) {
                    Main.LOGGER.warn("failed to initialise Discord RPC: {}", e.getMessage());
                }
            }
        });
        initDiscord.setName("initDiscord");
        initDiscord.start();
    }

    public void stfu() {
        DiscordRPC.discordClearPresence();
        DiscordRPC.discordShutdown();

        if (CALLBACK_THREAD != null) {
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }

            CALLBACK_THREAD.interrupt();
        } // calling .stop() will throw and crash now

        initialised = false;
    }

    public void initDC() {
        START_TIME = System.currentTimeMillis();
        DiscordRPC.discordInitialize("805552222985388063", new DiscordEventHandlers.Builder().setReadyEventHandler(this).build(), true);
        DiscordRPC.discordRunCallbacks();

        System.out.println("starting rpc");

        CALLBACK_THREAD = new Thread(() -> {
            executorService.scheduleAtFixedRate(() -> {
                if (Thread.interrupted()) {
                    return;
                }
                runCallback();
            }, 100, 1024, TimeUnit.MILLISECONDS);
        });
        CALLBACK_THREAD.setName("discordCallbackThread");
        CALLBACK_THREAD.start();
    }

    public void setActivity(DiscordRichPresence.Builder presenceBuilder) {
        if (!initialised) {
            Main.LOGGER.warn("setActivity called before RPC initialized!");
            return;
        }

        if (Main.config.get(FoxClientSetting.DiscordEnabled, Boolean.class)) {
            if (START_TIME == 0L) {
                START_TIME = System.currentTimeMillis();
            }

            presenceBuilder.setDetails("Playing Minecraft " + SharedConstants.getGameVersion().getName());
            presenceBuilder.setBigImage(PresenceUpdater.largeImage, "FoxClient " + Main.VERSION);
            presenceBuilder.setStartTimestamps(START_TIME);

            if (Main.config.get(FoxClientSetting.DiscordShowPlayer, Boolean.class)) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    presenceBuilder.setSmallImage(playerIconAPI + player.getUuidAsString(), player.getName().getString());
                }
            }

            DiscordRPC.discordUpdatePresence(presenceBuilder.build());
        }
    }

    private void runCallback() {
        if (Main.config.get(FoxClientSetting.DiscordEnabled, Boolean.class)) {
            DiscordRPC.discordRunCallbacks();
        } else {
            System.out.println("killing rpc");
            Thread.currentThread().interrupt();
            stfu();
        }
    }

    @Override
    public void apply(DiscordUser user) {
        Main.LOGGER.info("RPC loaded!");
        Main.LOGGER.info("Username: {}#{}", user.username, user.discriminator);
        initialised = true;

        DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder("Initialising")
                .setStartTimestamps(START_TIME)
                .setBigImage("main", null).build());
    }
}