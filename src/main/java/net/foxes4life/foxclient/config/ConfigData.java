package net.foxes4life.foxclient.config;

import java.util.LinkedHashMap;

public class ConfigData {
    public LinkedHashMap<String, Category> things = new LinkedHashMap<>(); // linked to prevent messing up the config file

    public ConfigData() {
        Category misc = new Category("Miscellaneous");
        misc.addSetting("discord-rpc", true);
        misc.addSetting("discord-rpc-show-ip", true);
        misc.addSetting("hud_enabled", true);
        things.put("misc", misc);

        Category eastereggs = new Category("easter eggs");
        eastereggs.addSetting("owo", false);
        things.put("eastereggs", eastereggs);
    }

    public ConfigData(LinkedHashMap<String, Category> in) {
        things = new ConfigData().things;

        things.putAll(in);
    }

    public ConfigData getInstance() {
        return this;
    }
}