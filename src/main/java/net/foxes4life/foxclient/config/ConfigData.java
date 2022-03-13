package net.foxes4life.foxclient.config;

//import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.Map;


public class ConfigData {
    public LinkedHashMap<String, Object> things = new LinkedHashMap<>(); // linked to prevent messing up the config file

    public ConfigData() {
        things.put("comment", "This is the FoxClient config. because yes.");
        things.put("discord-rpc", true);
        things.put("trueorfalse", true);
        things.put("hud_enabled", true);

        //ArrayList<String> test = new ArrayList<>(); test.add("testLOL"); test.add("e"); test.add("poggers");
        //things.put("array-object-test-lol", test);
        LinkedHashMap<String, Object> eggs = new LinkedHashMap<>(); eggs.put("wtf", true); eggs.put("owo", false); eggs.put("yes", false);
        things.put("eggs", eggs);

        things.put("discord-rpc", true);
        things.put("discord-rpc-show-ip", true);
    }

    public ConfigData(LinkedHashMap<String, Object> in) {
        things = new ConfigData().things;

        for (Map.Entry<String, Object> entry : in.entrySet()) {
            things.put(entry.getKey(), entry.getValue());
        }
    }

    public ConfigData getInstance() {
        return this;
    }
}
