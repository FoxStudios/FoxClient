package net.foxes4life.foxclient.util;

import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.rpc.Discord;
import net.foxes4life.foxclient.rpc.DiscordInstance;
import net.minecraft.text.MutableText;

public class ConfigUtils {
    public static MutableText getTranslation(FoxClientSetting setting) {
        return TextUtils.translatable("foxclient.config." + setting.name().toLowerCase());
    }

    public static MutableText translatableCategory(String c) {
        return TextUtils.translatable("foxclient.config.category." + c);
    }

    public static void onOptionChanged(FoxClientSetting setting, Object value) {
        if (setting == FoxClientSetting.DiscordEnabled) {
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