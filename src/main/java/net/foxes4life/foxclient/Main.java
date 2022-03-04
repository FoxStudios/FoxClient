package net.foxes4life.foxclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.foxes4life.foxclient.config.Config;
import net.foxes4life.foxclient.config.ConfigData;
import net.foxes4life.foxclient.rpc.DiscordInstance;

public class Main implements ModInitializer {
    public static final String FOXCLIENT_MOD_ID = "foxclient";
    public static String VERSION = "";
    public static String JAVA_VERSION = "unknown";

    public static Config config_instance;
    public static ConfigData config;

    @Override
    public void onInitialize() {
        Config.load("config.json");
        config_instance = Config.getInstance();
        config = Config.getData();
        config_instance.toFile("config.json");

        if(FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).isPresent()) {
            VERSION = FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        }
        if(FabricLoader.getInstance().getModContainer("java").isPresent()) {
            JAVA_VERSION = FabricLoader.getInstance().getModContainer("java").get().getMetadata().getVersion().getFriendlyString();
        }

        System.out.println("FoxClient (recode) by Rooot and Flustix");
        System.out.println(Main.VERSION);
    }
}
