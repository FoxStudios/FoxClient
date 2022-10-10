package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.rpc.Discord;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.foxes4life.konfig.data.KonfigCategory;
import net.foxes4life.konfig.data.KonfigEntry;
import net.minecraft.text.MutableText;

public class ConfigUtils {
    public static MutableText translatableEntry(KonfigCategory c, KonfigEntry e) {
        return TextUtils.translatable("foxclient.config." + c.catName + "." + e.getEntryName());
    }

    public static MutableText translatableCategory(KonfigCategory c) {
        return TextUtils.translatable("foxclient.config." + c.catName);
    }

    public static void onOptionChanged(String category, String entry, Object value) {
        if (category.equals("misc")) {
            if (entry.equals("discord-rpc")) {
                Discord discordInstance = DiscordInstance.get();

                if (!(boolean) value) {
                    if (!Discord.initialised) {
                        discordInstance.init();
                    }
                } else {
                    discordInstance.stfu();
                }
            }
        }
    }
}