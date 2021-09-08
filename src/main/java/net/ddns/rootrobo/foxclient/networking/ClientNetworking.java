package net.ddns.rootrobo.foxclient.networking;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import net.ddns.rootrobo.foxclient.notification.Notification;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientNetworking {
    @SuppressWarnings("FieldMayBeFinal")
    private static HashMap<String, Map<Long, String>> avatarCache = new HashMap<>();

    public static void onClientMessageReceived(MinecraftClient client, @SuppressWarnings({"unused", "RedundantSuppression"}) ClientPlayNetworkHandler handler, PacketByteBuf buf, @SuppressWarnings({"unused", "RedundantSuppression"}) PacketSender responseSender) {
        ByteBuf clonedBuf = new PacketByteBuf(buf.duplicate()).asReadOnly();
        String pmsg = clonedBuf.toString(StandardCharsets.UTF_8);
        JsonParser jsonParser = new JsonParser();
        JsonObject data = jsonParser.parse(pmsg).getAsJsonObject();
        String sender = data.get("s").getAsString();
        String message = data.get("m").getAsString();
        String sender_uuid = data.get("u").getAsString();

        assert client.player != null;
        client.player.sendMessage(new TranslatableText("§b"+sender+":§3 "+message), false);

        String img_uri;
        sender_uuid = sender_uuid.replace("-", "");

        if(!avatarCache.containsKey(sender_uuid)) {
            img_uri = getAndAddToCache(sender_uuid);
        } else {
            Map<Long, String> entry = avatarCache.get(sender_uuid);
            Iterator<Map.Entry<Long, String>> it = entry.entrySet().iterator();
            if(it.hasNext()) {
                Map.Entry<Long, String> e = it.next();
                long timeDiff = Instant.now().getEpochSecond() - e.getKey();
                System.out.println("diff: "+timeDiff);
                if(timeDiff >= 120) {
                    it.remove();
                    removeFromCache(sender_uuid);
                    img_uri = getAndAddToCache(sender_uuid);
                } else {
                    img_uri = e.getValue();
                }
            } else {
                it.remove();
                img_uri = getAndAddToCache(sender_uuid);
            }
        }
        Notification.showNotification(Text.of("§a"+sender), Text.of("§b"+message), img_uri);
    }

    private static void removeFromCache(String uuid) {
        System.out.println("removed "+uuid);
        avatarCache.entrySet().removeIf(entry -> entry.getKey().equals(uuid));
    }

    private static String getAndAddToCache(String uuid) {
        System.out.println("added "+uuid);
        String img_uri = getBase64FromImageURL("https://visage.surgeplay.com/face/256/"+uuid);
        long now = Instant.now().getEpochSecond();
        HashMap<Long, String> cache_data = new HashMap<>();
        cache_data.put(now, img_uri);
        avatarCache.put(uuid, cache_data);
        return img_uri;
    }

    private static String getBase64FromImageURL(String url) {
        try {
            InputStream is = new URL(url).openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }

            baos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            System.out.println("ERROR!");
        }
        return null;
    }
}
