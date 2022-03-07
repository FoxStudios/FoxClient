package net.foxes4life.foxclient.config;

import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;

public class ConfigData {
    public LinkedHashMap<String, Category> things = new LinkedHashMap<>(); // linked to prevent messing up the config file

    public ConfigData() {
        Category misc = new Category("Miscellaneous");
        misc.addSetting("discord-rpc", new CategoryEntry<>(true, "Discord RPC"));
        misc.addSetting("discord-rpc-show-ip", new CategoryEntry<>(true, "RPC: Show Server IP"));
        misc.addSetting("hud-enabled", new CategoryEntry<>(true, "Show HUD"));
        things.put("misc", misc);

        Category debug = new Category("Debug");
        debug.addSetting("boolean-uwu", new CategoryEntry<>(true, "Boolean Test"));
        debug.addSetting("string-owo", new CategoryEntry<>("hewwo wowwd", "String Test"));
        debug.addSetting("float-rawr", new CategoryEntry<>(0.1F, "Float Test"));
        debug.addSetting("int-nya", new CategoryEntry<>(621, "Integer Test"));
        debug.addSetting("identifier", new CategoryEntry<>(new Identifier("among", "us"), "ID Test"));
        things.put("debug", debug);

        Category eastereggs = new Category("easter eggs");
        eastereggs.addSetting("owo", new CategoryEntry<>(false, "owo whats this?"));
        things.put("eastereggs", eastereggs);

        Category ingameHUD = new Category("Overlay HUD");
        ingameHUD.addSetting("coords", new CategoryEntry<>(true, "Coordinates"));
        ingameHUD.addSetting("version", new CategoryEntry<>(true, "Version"));
        ingameHUD.addSetting("fps", new CategoryEntry<>(true, "FPS"));
        things.put("ingame-hud", ingameHUD);
    }

    public ConfigData(LinkedHashMap<String, Category> in) {
        things = new ConfigData().things;

        things.putAll(in);
    }

    public ConfigData getInstance() {
        return this;
    }
}