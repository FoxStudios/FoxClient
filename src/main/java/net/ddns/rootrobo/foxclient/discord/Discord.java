package net.ddns.rootrobo.foxclient.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.ddns.rootrobo.foxclient.Main;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class Discord {
    public static Core CORE = null;
    public static Thread CALLBACK_THREAD = null;
    public static Instant START_TIME = null;
    public static File dcLib = null;
    public static boolean initialised = false;
    public static void init() {
            Thread initDiscordLib = new Thread(() -> {
                while(!(MinecraftClient.getInstance() != null && !MinecraftClient.getInstance().fpsDebugString.equals(""))) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }

                File discordLibrary = null;
                try {
                    discordLibrary = DownloadNativeLibrary.getNativeLibrary();
                } catch (IOException ignored) {
                }

                if(discordLibrary == null) {
                    System.err.println("Error getting Discord SDK.");
                    return;
                }
                dcLib = discordLibrary;
                if(Main.config_instance.getBoolean("discord-rpc")) {
                    initDC();
                    try {
                        Thread.sleep(6969);
                    } catch (InterruptedException ignored) {
                    }
                    initialised = true;
                }
            });
            initDiscordLib.setName("initDiscord");
            initDiscordLib.start();
    }

    public static void stfu() {
        CORE.close();
        initialised = false;
    }

    public static void initDC() {
        Core.init(dcLib);

        try(CreateParams params = new CreateParams()) {
            params.setClientID(805552222985388063L);
            params.setFlags(CreateParams.getDefaultFlags());

            CORE = new Core(params);

            try(Activity activity = new Activity()) {
                activity.setDetails("FoxClient");
                activity.setState("Initialising ...");
                START_TIME = Instant.now();
                activity.timestamps().setStart(START_TIME);
                activity.assets().setLargeImage("main");
                CORE.activityManager().updateActivity(activity);
            }

            // Run callbacks forever
            CALLBACK_THREAD = new Thread(() -> new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runCallback();
                }
            }, 100, 256));
            CALLBACK_THREAD.setName("discordCallbackThread");
            CALLBACK_THREAD.start();
        }
    }

    public static void setActivity(Activity activity) {
        if(Main.config_instance.getBoolean("discord-rpc")) {
            if(CORE == null) return;
            if (START_TIME == null) {
                START_TIME = Instant.now();
            }
            activity.assets().setLargeText("FoxClient " + Main.VERSION);
            activity.timestamps().setStart(START_TIME);
            CORE.activityManager().updateActivity(activity);
        }
    }

    private static void runCallback() {
        if(Main.config_instance.getBoolean("discord-rpc")) {
            CORE.runCallbacks();
        }
    }
}
