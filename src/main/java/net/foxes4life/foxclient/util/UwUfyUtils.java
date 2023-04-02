package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;

import java.util.HashMap;
import java.util.Random;

public class UwUfyUtils {
    private static final HashMap<String, String> stringMap = new HashMap<>();
    private static final String[] faces = {";;w;;", "owo", "UwU", ">w<", "^w^", "(owo)", "ÓwÓ", "ÕwÕ", "Owo", "owO"};
    private static final String[] endings = {"OwO *what's this*", "uwu yu so warm~", "owo pounces on you~~"};
    private static final Random random = new Random();

    public static String uwufy(String originalText) {
        if (!Main.config.get(FoxClientSetting.UwUfy, Boolean.class))
            return originalText;

        if (stringMap.containsKey(originalText))
            return stringMap.get(originalText);

        String text = originalText;
        text = text.replace("R", "W").replace("r", "w").replace("L", "W").replace("l", "w");

        text = switch (random.nextInt(3)) {
            case 1 -> text.replace("n", "ny");
            case 2 -> text.replace("n", "nya");
            default -> text;
        };

        if (random.nextInt(3) == 1)
            text = text.replace("!", " " + faces[random.nextInt(faces.length)]);

        switch (random.nextInt(3)) {
            case 1 -> text = text.replace("?", "?!");
            case 2 -> text = text.replace("?", " " + faces[random.nextInt(faces.length)]);
        }

        if (random.nextInt(32) == 12)
            text += " " + endings[random.nextInt(endings.length)];

        stringMap.put(originalText, text);
        return text;
    }
}
