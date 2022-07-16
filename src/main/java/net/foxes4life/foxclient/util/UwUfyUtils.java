package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.Main;

public class UwUfyUtils {
    public static String uwufy(String in) {
        if(!(boolean)Main.konfig.get("eastereggs", "owo"))
            return in;

        in = in
                .replace("R", "W")
                .replace("r", "w")
                .replace("L", "W")
                .replace("l", "w")
                .replace(":D", ":3")
                .replace("XD", "X3")
                .replace(":)", ":3")
                .replace("N", "Ny")
                .replace("n", "ny");

        /*int rng = new Random().nextInt(3);

        if (rng == 0) {
            if (in.length() >= 2) {
                char charac = in.charAt(0);

                if (invalidCheck(String.valueOf(charac)))
                    return in;

                in = charac + "-" + in;
            }
        }*/

        return in;
    }

    static Boolean invalidCheck(String a) {
        if (a.equals("%"))
            return true;

        if (a.equals("<"))
            return true;

        if (a.equals("["))
            return true;

        if (a.equals("("))
            return true;

        return false;
    }
}
