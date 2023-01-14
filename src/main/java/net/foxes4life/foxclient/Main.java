package net.foxes4life.foxclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.foxes4life.foxclient.configuration.FoxClientConfigManager;
import net.foxes4life.foxclient.util.update.UpdateChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String FOXCLIENT_MOD_ID = "foxclient";
    public static String VERSION = "";
    public static String SIMPLE_VERSION = "";
    public static String JAVA_VERSION = "unknown";
    public static String DISCORD_INVITE = "";

    public static FoxClientConfigManager config;

    public static final Logger LOGGER = LoggerFactory.getLogger(FOXCLIENT_MOD_ID);

    @Override
    public void onInitialize() {
        config = new FoxClientConfigManager();

        if (FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).isPresent()) {
            ModContainer modContainer = FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).get();

            VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
            SIMPLE_VERSION = VERSION.substring(0, VERSION.indexOf("-rev"));
            DISCORD_INVITE = modContainer.getMetadata().getCustomValue("modmenu").getAsObject().get("links").getAsObject().get("modmenu.discord").getAsString();
        }

        if (FabricLoader.getInstance().getModContainer("java").isPresent()) {
            JAVA_VERSION = FabricLoader.getInstance().getModContainer("java").get().getMetadata().getVersion().getFriendlyString();
        }

        LOGGER.info("FoxClient " + Main.VERSION + " by FoxStudios");

        Thread updateChecker = new Thread(() -> {
            LOGGER.info("Checking for updates...");
            if (UpdateChecker.updateAvailable()) {
                LOGGER.info("Update available! Latest version: " + UpdateChecker.getLatestVersion());
            } else {
                LOGGER.info("Running latest version!");
            }
        });
        updateChecker.setName("FoxClient Update Checker");
        updateChecker.start();
    }
}
