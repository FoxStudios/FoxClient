/*
 The entire cape code is sto- borrowed from Kappa (https://github.com/Hibiii/Kappa)
 Kappa is licensed under the UNLICENSE.
*/
package net.foxes4life.foxclient.capes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.foxes4life.foxclient.util.Http;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.http.HttpResponse;

public final class Provider {

    // This loads the cape for one player, doesn't matter if it's the player or not.
    // Requires a callback, that receives the id for the cape
    public static void loadCape(GameProfile player, CapeTextureAvailableCallback callback) {
        Runnable runnable = () -> {
            // Check if the player doesn't already have a cape.
            Identifier existingCape = capes.get(player.getName());
            if(existingCape != null) {
                System.out.println("hi");
                callback.onTexAvail(existingCape);
                return;
            }

            if(!Provider.tryUrl(player, callback, "https://client.foxes4life.net/capes/get.php?uuid=" + player.getId().toString().replace("-", ""))) {
                Provider.tryUrl(player, callback, "http://client.foxes4life.net/capes/get.php?uuid=" + player.getId().toString().replace("-", ""));
            }
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }

    public interface CapeTextureAvailableCallback {
        void onTexAvail(Identifier id);
    }

    // This is where capes will be stored
    private static final Map<String, Identifier> capes = new HashMap<>();

    // Try to load a cape from an URL.
    // If this fails, it'll return false, and let us try another url.
    private static boolean tryUrl(GameProfile player, CapeTextureAvailableCallback callback, String urlFrom) {
        try {
            System.out.println("[FoxClient/Cape/Provider] trying url: " + urlFrom);
            HttpResponse response = Http.get(urlFrom);
            if(response != null && response.getStatusLine().getStatusCode() == 200) {
                String responseBody = Http.getResponseBody(response);
                JsonObject capeJson = JsonParser.parseString(responseBody).getAsJsonObject();
                if(capeJson.has("textures") && capeJson.get("textures").getAsJsonObject().get("cape") != null) {
                    // set the cape
                    String capeBase64 = capeJson.get("textures").getAsJsonObject().get("cape").getAsString();
                    byte[] img = Base64.getDecoder().decode(capeBase64);
                    NativeImage cape = NativeImage.read(new ByteArrayInputStream(img));
                    Identifier id = MinecraftClient
                            .getInstance()
                            .getTextureManager()
                            .registerDynamicTexture("foxclient_" + player.getId().toString().replace("-", ""),
                            new NativeImageBackedTexture(cape));
                    capes.put(player.getName(), id);
                    System.out.println("put " + id.toString() + " for player " + player.getName());
                    callback.onTexAvail(id);
                }
            } else {
                if(response != null && response.getStatusLine().getStatusCode() == 404) {
                    System.out.println("[FoxClient/Cape/Provider] no cape");
                    // player has no cape
                    return true;
                }
                System.out.println("[FoxClient/Cape/Provider] request failed: " + player.getId().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Provider() { }
}