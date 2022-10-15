package net.foxes4life.foxclient.util.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.foxes4life.foxclient.Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/FoxStudios/FoxClient/releases/latest";
    private static final String GITHUB_RELEASE_URL = "https://github.com/FoxStudios/FoxClient/releases/tag/";
    private static boolean updateAvailable = false;
    private static boolean checked = false;
    private static String latestVersion = "unknown";
    public static boolean dismissed = false;

    private static void checkForUpdates() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(GITHUB_API_URL))
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        String version = jsonObject.get("tag_name").getAsString();

        if (!version.equals(Main.SIMPLE_VERSION)) {
            updateAvailable = true;
            latestVersion = version;
        }

        checked = true;
    }

    public static boolean updateAvailable() {
        if (!checked) {
            try {
                checkForUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (latestVersion.equals("unknown")) return false; // If we can't get the latest version, don't show the update screen

        return updateAvailable;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static String getReleaseUrl() {
        return GITHUB_RELEASE_URL + latestVersion;
    }
}
