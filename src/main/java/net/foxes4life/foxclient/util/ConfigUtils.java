package net.foxes4life.foxclient.util;

import net.foxes4life.konfig.data.KonfigCategory;
import net.foxes4life.konfig.data.KonfigEntry;
import net.minecraft.text.MutableText;

public class ConfigUtils {
    public static MutableText translatableEntry(KonfigCategory c, KonfigEntry e) {
        return TextUtils.translatable("foxclient.config." + c.catName + "." + e.entryName);
    }

    public static MutableText translatableCategory(KonfigCategory c) {
        return TextUtils.translatable("foxclient.config." + c.catName);
    }
}