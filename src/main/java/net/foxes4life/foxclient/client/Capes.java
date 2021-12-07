package net.foxes4life.foxclient.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Capes {
    private static Map<UUID, PlayerCape> capes = new HashMap<>();

    public static PlayerCape getCape(UUID uuid) {
        if(capes.containsKey(uuid)) {
            return capes.get(uuid);
        } else {
            System.out.println("[Capes] obtaining new cape");
            PlayerCape cape = new PlayerCape(uuid);
            try {
                cape.updateCapeTexture();
            } catch (NullPointerException ignored) {
                System.out.println("yoo nullpointer!!11");
            }
            capes.put(uuid, cape);
            return cape;
        }
    }
}
