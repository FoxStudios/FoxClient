package net.ddns.rootrobo.foxclient.egg;

import com.google.gson.internal.LinkedTreeMap;
import net.ddns.rootrobo.foxclient.Main;

import java.util.LinkedHashMap;
import java.util.Map;

public class EggManager {
    public static boolean isOwOEnabled() {
        return isEnabled("owo");
    }

    private static boolean isEnabled(String egg) {
        try {
            //noinspection unchecked
            return (boolean)((LinkedTreeMap<String, Object>)Main.config_instance.getObject("eggs")).get(egg);
        } catch (ClassCastException ignored) {
            try {
                //noinspection unchecked
                return (boolean)((LinkedHashMap<String, Object>)Main.config_instance.getObject("eggs")).get(egg);
            } catch (ClassCastException ignored2) {
                return false;
            }
        }
    }

    public static void setEnabled(String egg, boolean enabled) {
        //noinspection unchecked
        Map<String, Object> eggs = (Map<String, Object>) Main.config_instance.getObject("eggs");
        eggs.put(egg, enabled);
        Main.config_instance.set("eggs", eggs);
    }
}
