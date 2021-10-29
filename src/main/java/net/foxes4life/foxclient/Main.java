package net.foxes4life.foxclient;

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

		hudEnabled = config_instance.getBoolean("hud_enabled");

		//TODO: fix client chat lag issue when server is offline/other server running on same port
		//TODO: -> ISSUE #1
		/*
		Thread ClientChatThread = new Thread(() -> {
			try {
				ClientChat.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		ClientChatThread.setName("FoxClientChatThread");
		ClientChatThread.start();
		 */

		// start discord rpc
		Discord.init();
	}
}
