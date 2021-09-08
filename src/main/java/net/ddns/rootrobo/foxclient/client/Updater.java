package net.ddns.rootrobo.foxclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ddns.rootrobo.foxclient.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Updater {
    public static final String updateUrl = "https://pastebin.com/raw/RpdBrtFF";

    private static String downloadUrl = "";
    private static String name;
    public static int prepareUpdate() {
        if(!FabricLoader.getInstance().getModContainer(Main.MOD_ID).isPresent()) return -1;
        //String thisVersion = FabricLoader.getInstance().getModContainer(Main.MODID).get().getMetadata().getVersion().getFriendlyString();

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(updateUrl);
        HttpResponse response = null;

        try {
            response = client.execute(request);
        } catch (IOException ignored) {
        }

        BufferedReader rd = null;
        StringBuffer result;

        try {
            assert response != null;
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = new StringBuffer();
        String line;
        try {
            try {
                while (true) {
                    assert rd != null;
                    if ((line = rd.readLine()) == null) break;
                    result.append(line);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException ignored) {
        }

        String body = result.toString();
        JsonParser parser = new JsonParser();
        JsonElement bodyJson = parser.parse(body);

        if(!bodyJson.isJsonObject()) return -1;
        JsonObject json = bodyJson.getAsJsonObject();

        String latestHash = json.get("hash").getAsString();
        String jarPath = Client.getJarPath();

        if(jarPath == null) {
            System.out.println("Could not update!");
            return 2; // dev env
        }

        File jar = new File(jarPath);
        if(!jar.exists()) {
            System.out.println("Could not update! (JAR doesn't exist)");
            return -1;
        }

        String hash = null;
        try (InputStream is = Files.newInputStream(Paths.get(jarPath))) {
            hash = org.apache.commons.codec.digest.DigestUtils.sha1Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Latest Hash: " + latestHash);
        System.out.println("Hash: " + hash);

        if(hash == null) {
            System.out.println("Couldn't Hash JAR!");
            return -1;
        }

        if(hash.equalsIgnoreCase(latestHash)) {
            System.out.println("Latest!");
            return 1;
        }

        System.out.println("Hash is different, updating!");
        //int tv = Integer.parseInt(thisVersion.replace(".", ""));
        //int lv = Integer.parseInt(version.replace(".", ""));
        //if(tv >= lv) return false;

        name = json.get("name").getAsString();
        //version = json.get("version").getAsJsonObject().get("id").getAsString();
        downloadUrl = json.get("download").getAsString();
        return 0;
    }

    public static void update(@Nullable ButtonWidget buttonWidget) throws IOException {
        if(getDownloadUrl().equals("")) return;
        if (buttonWidget != null) {
            buttonWidget.active = false;
            buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.downloading"));
        }

        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new URL(downloadUrl).openStream());
        } catch (IOException e) {
            if(buttonWidget != null) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.failed"));
            }
            return;
        }

        File file;
        try {
            file = Client.cacheFileToConfig(in, name, true);
        } catch (IOException e) {
            if(buttonWidget != null) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.failed"));
            }
            e.printStackTrace();
            return;
        }

        String filePath = file.getAbsolutePath();

        // delete JAR
        File updater = null;
        try {
            updater = Client.cacheFileToConfig(Client.getStreamFromJar("updater.jar"), "updater.jar", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(updater != null && updater.exists()) {
            System.out.println("Updater: "+updater.getPath());
            System.out.println("Updater exists!");
            String cmd = "java -jar \"" + updater.getPath() + "\" \""+Client.getJarPath()+"\" \""+filePath+"\"";
            System.out.println("CMD: "+cmd);
            Process updaterProcess = Runtime.getRuntime().exec(cmd);
            System.out.println("Starting updater");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            if(updaterProcess.isAlive()) {
                System.out.println("CLOSING GAME");
                MinecraftClient.getInstance().scheduleStop();
            }
        }
    }

    public static String getDownloadUrl() {
        return downloadUrl;
    }
}
