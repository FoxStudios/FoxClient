package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.screen.pause.PauseScreen;
import net.foxes4life.foxclient.util.MiscUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.InputStream;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    private IntegratedServer server;

    @Inject(at = @At("HEAD"), method = "getWindowTitle", cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        String title = "FoxClient " + Main.SIMPLE_VERSION;

        title += " | Minecraft " + SharedConstants.getGameVersion().getName();

        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
        ServerInfo currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();

        if ((boolean) Main.konfig.get("misc", "discord-rpc")) {
            PresenceUpdater.setState(DiscordMinecraftClient.getState(clientPlayNetworkHandler));
        }

        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            title += " - ";
            if (this.server != null && !this.server.isRemote()) {
                title += I18n.translate("title.singleplayer");
            } else if (MinecraftClient.getInstance().isConnectedToRealms()) {
                title += I18n.translate("title.multiplayer.realms");
            } else if (this.server == null && (currentServerEntry == null || !currentServerEntry.isLocal())) {
                if (currentServerEntry != null && currentServerEntry.address != null || (boolean) Main.konfig.get("misc", "discord-rpc-show-ip")) {
                    title += I18n.translate("title.multiplayer.other2", currentServerEntry.address);
                } else {
                    title += I18n.translate("title.multiplayer.other");
                }
            } else {
                title += I18n.translate("title.multiplayer.lan");
            }
        }

        cir.setReturnValue(title);
    }

    @Inject(at = @At("HEAD"), method = "openPauseMenu", cancellable = true)
    public void openPauseMenu(boolean pause, CallbackInfo ci) {
        if ((boolean) Main.konfig.get("menus", "pause")) {
            ci.cancel();
            if (MinecraftClient.getInstance().currentScreen == null) {
                MinecraftClient.getInstance().setScreen(new PauseScreen());
            }
        }
    }
}
