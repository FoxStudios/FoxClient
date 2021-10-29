package net.foxes4life.foxclient.discord;

import net.foxes4life.foxclient.client.Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DownloadNativeLibrary {
    public static File getNativeLibrary() throws IOException {
        String name = "discord_game_sdk";
        String suffix;
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            suffix = ".dll";
        } else {
            suffix = ".so";
        }

        String libname = name+suffix;

        InputStream stream = Client.getStreamFromJar(libname);

        return Client.cacheFileToConfig(stream, libname, true);
    }
}