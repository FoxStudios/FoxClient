package net.foxes4life.foxclient;

import net.fabricmc.loader.api.FabricLoader;
import net.foxes4life.foxclient.config.Config;
import net.foxes4life.foxclient.config.ConfigData;
import net.foxes4life.foxclient.discord.Discord;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

@Environment(EnvType.CLIENT)
public class Main implements ModInitializer {
	public static final String MOD_ID = "foxclient";
	public static String VERSION = null;
	public static String JAVA_VERSION = "unknown";
	public static Config config_instance;
	public static ConfigData config;

	public static boolean hudEnabled = true;

	@Override
	public void onInitialize() {
		Config.load("config.json");
		config_instance = Config.getInstance();
		config = Config.getData();
		config_instance.toFile("config.json");

		if(net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(Main.MOD_ID).isPresent()) {
			VERSION = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(Main.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
			System.out.println("FoxClient v"+VERSION);
		}
		if(FabricLoader.getInstance().getModContainer("java").isPresent()) {
			JAVA_VERSION = FabricLoader.getInstance().getModContainer("java").get().getMetadata().getVersion().getFriendlyString();
		}

		hudEnabled = config_instance.getBoolean("hud_enabled");

		// start discord rpc
		Discord.init();
	}
}
