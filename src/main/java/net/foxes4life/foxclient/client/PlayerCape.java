package net.foxes4life.foxclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.foxes4life.foxclient.util.Http;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class PlayerCape {
    private UUID uuid;
    private Identifier texture = null;
    private boolean loaded = false;

    public PlayerCape(UUID uuid) {
        this.uuid = uuid;
        //System.out.println("contructor: " + uuid);
    }

    public Identifier getTexture() {
        if(this.texture == null && !loaded) {
            updateCapeTexture();
        }

        return this.texture;
    }

    public void updateCapeTexture() {
        try {
            System.out.println("[PlayerCape] updating cape texture "+this.uuid.toString());
            HttpResponse response = Http.get("https://foxes4life.net/capes/get.php?uuid=" + this.uuid.toString().replace("-", ""));
            if(response != null && response.getStatusLine().getStatusCode() == 200) {
                String responseBody = Http.getResponseBody(response);
                JsonParser jsonParser = new JsonParser();
                JsonObject capeJson = jsonParser.parse(responseBody).getAsJsonObject();
                if(capeJson.has("textures") && capeJson.get("textures").getAsJsonObject().get("cape") != null) {
                    String capeBase64 = capeJson.get("textures").getAsJsonObject().get("cape").getAsString();
                    setCapeBase64(capeBase64);
                }
            } else {
                System.out.println("[PlayerCape] request failed " + this.uuid);
            }
            loaded = true;
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    private void setCapeBase64(String base64) {
        byte[] img = Base64.getDecoder().decode(base64);
        setCapeTexture(new ByteArrayInputStream(img));
    }

    private void setCapeTexture(InputStream img) {
        try {
            NativeImage capeImg = NativeImage.read(img);
            this.texture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(
                    "fcc_"+uuid.toString().replace("-", ""),
                    new NativeImageBackedTexture(capeImg)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}