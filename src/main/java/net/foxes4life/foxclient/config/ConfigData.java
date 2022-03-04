package net.foxes4life.foxclient.config;

import java.util.LinkedHashMap;

public class ConfigData {
    public LinkedHashMap<String, Object> things = new LinkedHashMap<>(); // linked to prevent messing up the config file

    public ConfigData() {
        things.put("discord-rpc", true);
        things.put("hud_enabled", true);

        LinkedHashMap<String, Object> eggs = new LinkedHashMap<>();
        eggs.put("owo", false);
        things.put("eastereggs", eggs);

        things.put("discord-rpc", true);
        things.put("discord-rpc-show-ip", true);
    }

    public ConfigData(LinkedHashMap<String, Object> in) {
        things = new ConfigData().things;

        things.putAll(in);
    }

    public ConfigData getInstance() {
        return this;
    }
}