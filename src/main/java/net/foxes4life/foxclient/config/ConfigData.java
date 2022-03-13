package net.foxes4life.foxclient.config;

import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;

public class ConfigData {
    public LinkedHashMap<String, Category> things = new LinkedHashMap<>(); // linked to prevent messing up the config file

    public ConfigData() {
        Category client = new Category("Client");
        client.addSetting("hud-enabled", new CategoryEntry<>(true, "Show HUD"));
        client.addSetting("customMenu", new CategoryEntry<>(true, "FoxClient Menus"));

        Category misc = new Category("Miscellaneous");
        misc.addSetting("discord-rpc", new CategoryEntry<>(true, "Discord RPC"));
        misc.addSetting("discord-rpc-show-ip", new CategoryEntry<>(true, "RPC: Show Server IP"));

        Category debug = new Category("Debug");
        debug.addSetting("boolean-uwu", new CategoryEntry<>(true, "Boolean Test"));
        debug.addSetting("string-owo", new CategoryEntry<>("hewwo wowwd", "String Test"));
        debug.addSetting("float-rawr", new CategoryEntry<>(0.1F, "Float Test"));
        debug.addSetting("int-nya", new CategoryEntry<>(621, "Integer Test"));
        debug.addSetting("identifier", new CategoryEntry<>(new Identifier("among", "us"), "ID Test"));

        Category eastereggs = new Category("Easter Eggs");
        eastereggs.addSetting("owo", new CategoryEntry<>(false, "owo whats this?"));

        Category ingameHUD = new Category("Overlay HUD");
        ingameHUD.addSetting("version", new CategoryEntry<>(true, "Version"));
        ingameHUD.addSetting("coords", new CategoryEntry<>(true, "Coordinates"));
        ingameHUD.addSetting("fps", new CategoryEntry<>(true, "FPS"));
        ingameHUD.addSetting("ping", new CategoryEntry<>(true, "Ping"));
        ingameHUD.addSetting("tps", new CategoryEntry<>(true, "TPS"));

        things.put("client", client);
        things.put("ingame-hud", ingameHUD);
        things.put("misc", misc);
        things.put("eastereggs", eastereggs);
//        things.put("debug", debug);
    }

    public ConfigData(LinkedHashMap<String, Category> in) {
        things = new ConfigData().things;

        things.putAll(in);
    }

    public ConfigData getInstance() {
        return this;
    }
}