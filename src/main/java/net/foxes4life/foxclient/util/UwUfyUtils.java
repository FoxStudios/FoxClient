package net.foxes4life.foxclient.util;

public class UwUfyUtils {
    public static String uwufy(String in) {
        in = in
                .replace("R", "W")
                .replace("r", "w")
                .replace("L", "W")
                .replace("l", "w");
        return in;
    }
}
