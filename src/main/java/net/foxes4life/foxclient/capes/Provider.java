/*
 The entire cape code is sto- borrowed from Kappa (https://github.com/Hibiii/Kappa)
 Kappa is licensed under the UNLICENSE.
*/
package net.foxes4life.foxclient.capes;

import com.mojang.authlib.GameProfile;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.util.Http;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Provider {

    // This loads the cape for one player, doesn't matter if it's the player or not.
    // Requires a callback, that receives the id for the cape
    public static void loadCape(GameProfile player, CapeTextureAvailableCallback callback) {
        Runnable runnable = () -> {
            // Check if the player doesn't already have a cape.
            CapeTexture existingCape = capes.get(player.getId());
            if (existingCape != null) {
                callback.onTexAvail(existingCape);
                return;
            }

            // put a null cape to prevent multiple requests
            capes.put(player.getId(), new CapeTexture(null, null));

            if (!Provider.tryUrl(player, callback, "https://client.foxes4life.net/api/v0/capes/get/" + player.getId().toString().replace("-", ""))) {
                Provider.tryUrl(player, callback, "http://client.foxes4life.net/api/v0/capes/get/" + player.getId().toString().replace("-", ""));
            }
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }

    public interface CapeTextureAvailableCallback {
        void onTexAvail(CapeTexture id);
    }

    // This is where capes will be stored
    private static final Map<UUID, CapeTexture> capes = new HashMap<>();

    // Try to load a cape from an URL.
    // If this fails, it'll return false, and let us try another url.
    private static boolean tryUrl(GameProfile player, CapeTextureAvailableCallback callback, String urlFrom) {
        try {
            //Main.LOGGER.debug("[FoxClient/Cape/Provider] trying url: " + urlFrom);
            HttpResponse response = Http.get(urlFrom);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                NativeImage cape = NativeImage.read(response.getEntity().getContent());
                Identifier id = MinecraftClient
                        .getInstance()
                        .getTextureManager()
                        .registerDynamicTexture("foxclient_" + player.getId().toString().replace("-", ""),
                                new NativeImageBackedTexture(cape));

                // todo: elytra?
                CapeTexture texture = new CapeTexture(id, id);

                capes.put(player.getId(), texture);
                Main.LOGGER.debug("put " + id.toString() + " for player " + player.getName());
                callback.onTexAvail(texture);
            } else {
                if (response == null || response.getStatusLine().getStatusCode() != 404) {
                    Main.LOGGER.debug("[FoxClient/Cape/Provider] request failed: " + player.getId().toString());
                }

                // player has no cape
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Provider() {
    }
}