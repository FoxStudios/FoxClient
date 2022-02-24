package net.foxes4life.foxclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Main implements ModInitializer {
    public static final String FOXCLIENT_MOD_ID = "foxclient";
    public static String VERSION = "";
    public static String JAVA_VERSION = "unknown";

    @Override
    public void onInitialize() {
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
