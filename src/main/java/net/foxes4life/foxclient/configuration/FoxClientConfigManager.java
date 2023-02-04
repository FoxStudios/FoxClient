package net.foxes4life.foxclient.configuration;

import net.foxes4life.konfig.Konfig;

public class FoxClientConfigManager extends Konfig<FoxClientSetting> {
    public FoxClientConfigManager() {
        super("foxclient");
    }

    public void initializeDefaults() {
        setDefault(FoxClientSetting.HudEnabled, true);

        setDefault(FoxClientSetting.CustomMainMenu, true);
        setDefault(FoxClientSetting.CustomPauseMenu, false);

        setDefault(FoxClientSetting.DiscordEnabled, true);
        setDefault(FoxClientSetting.DiscordShowIP, true);
        setDefault(FoxClientSetting.DiscordShowPlayer, true);

        setDefault(FoxClientSetting.SmoothZoom, false);

        setDefault(FoxClientSetting.UwUfy, false);

        setDefault(FoxClientSetting.HudBackground, true);
        setDefault(FoxClientSetting.HudLogo, true);
        setDefault(FoxClientSetting.HudVersion, true);
        setDefault(FoxClientSetting.HudCoordinates, true);
        setDefault(FoxClientSetting.HudCoordinatesColor, false);
        setDefault(FoxClientSetting.HudFPS, true);
        setDefault(FoxClientSetting.HudPing, true);
        setDefault(FoxClientSetting.HudTps, true);
        setDefault(FoxClientSetting.HudServerIP, true);
        setDefault(FoxClientSetting.HudBiome, false);

        setDefault(FoxClientSetting.ArmorHudEnabled, false);
    }
}
