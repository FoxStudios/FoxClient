package net.foxes4life.foxclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.foxes4life.konfig.Konfig;
import net.foxes4life.konfig.data.KonfigCategory;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String FOXCLIENT_MOD_ID = "foxclient";
    public static String VERSION = "";
    public static String JAVA_VERSION = "unknown";

    public static Konfig konfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(FOXCLIENT_MOD_ID);

    @Override
    public void onInitialize() {
        initConfig();

        if(FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).isPresent()) {
            VERSION = FabricLoader.getInstance().getModContainer(Main.FOXCLIENT_MOD_ID).get().getMetadata().getVersion().getFriendlyString();
        }

        if(FabricLoader.getInstance().getModContainer("java").isPresent()) {
            JAVA_VERSION = FabricLoader.getInstance().getModContainer("java").get().getMetadata().getVersion().getFriendlyString();
        }

        LOGGER.info("FoxClient " + Main.VERSION + " by FoxStudios");
    }

    void initConfig () {
        konfig = new Konfig("foxclient");

        KonfigCategory client = new KonfigCategory("client");
        client.addEntry("hud-enabled", true);

        KonfigCategory menus = new KonfigCategory("menus");
        menus.addEntry("mainmenu", true);
        menus.addEntry("pause", false);

        KonfigCategory misc = new KonfigCategory("misc");
        misc.addEntry("discord-rpc", true);
        misc.addEntry("discord-rpc-show-ip", true);
        misc.addEntry("smoothzoom", true);

        KonfigCategory eastereggs = new KonfigCategory("eastereggs");
        eastereggs.addEntry("owo", false);

        KonfigCategory ingameHUD = new KonfigCategory("ingame-hud");
        ingameHUD.addEntry("version", true);
        ingameHUD.addEntry("coords", true);
        ingameHUD.addEntry("colored-coords", false);
        ingameHUD.addEntry("fps", true);
        ingameHUD.addEntry("ping", true);
        ingameHUD.addEntry("tps", true);
        ingameHUD.addEntry("server", true);

        konfig.addCategory(client);
        konfig.addCategory(menus);
        konfig.addCategory(ingameHUD);
        konfig.addCategory(misc);
        konfig.addCategory(eastereggs);
    }
}
