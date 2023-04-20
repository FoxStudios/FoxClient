package net.foxes4life.foxclient.util;

import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;

public class TextUtils {
    public static MutableText string(String content) {
        return MutableText.of(new LiteralTextContent(content));
    }

    public static MutableText translatable(String content) {
        return MutableText.of(new TranslatableTextContent(content, content, new Object[0]));
    }
}
