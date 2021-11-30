package net.foxes4life.foxclient.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.foxes4life.foxclient.Main;
import net.minecraft.client.MinecraftClient;

import java.util.Timer;
import java.util.TimerTask;

public class Discord implements ReadyCallback {
    public Thread CALLBACK_THREAD = null;
    public long START_TIME = 0L;
    public static boolean initialised = false;

    public void init() {
            Thread initDiscord = new Thread(() -> {
                while(!(MinecraftClient.getInstance() != null && !MinecraftClient.getInstance().fpsDebugString.equals(""))) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }

                if(Main.config_instance.getBoolean("discord-rpc")) {
                    try {
                        System.out.println("init dc");
                        initDC();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    initialised = true;
                }
            });
            initDiscord.setName("initDiscord");
            initDiscord.start();
    }

    public void stfu() {
        DiscordRPC.discordClearPresence();
        DiscordRPC.discordShutdown();
        CALLBACK_THREAD.stop();
        initialised = false;
    }

    public void initDC() {
        System.out.println("init");
        START_TIME = System.currentTimeMillis();
        DiscordRPC.discordInitialize("805552222985388063", new DiscordEventHandlers.Builder().setReadyEventHandler(this).build(), true);
        DiscordRPC.discordRunCallbacks();

        System.out.println("discord - set activity");
        DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder("Initialising")
                .setStartTimestamps(START_TIME)
                .setBigImage("main", null).build());

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

    public void setActivity(DiscordRichPresence presence) {
        if(Main.config_instance.getBoolean("discord-rpc")) {
            if (START_TIME == 0L) {
                START_TIME = System.currentTimeMillis();
            }

            presence.largeImageText = "FoxClient " + Main.VERSION;
            presence.startTimestamp = START_TIME;
            DiscordRPC.discordUpdatePresence(presence);
        }
    }

    private void runCallback() {
        if(Main.config_instance.getBoolean("discord-rpc")) {
            DiscordRPC.discordRunCallbacks();
        }
    }

    @Override
    public void apply(DiscordUser user) {
        System.out.println("RPC loaded!");
        System.out.println("Username: " + user.username + "#" + user.discriminator);
    }
}
