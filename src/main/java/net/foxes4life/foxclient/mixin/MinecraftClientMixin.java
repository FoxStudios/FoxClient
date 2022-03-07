package net.foxes4life.foxclient.mixin;

import net.arikia.dev.drpc.DiscordRPC;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.InputStream;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable private IntegratedServer server;

    @Shadow @Nullable public abstract ServerInfo getCurrentServerEntry();

    @Shadow @Nullable private ServerInfo currentServerEntry;

    @ModifyArgs(at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/util/Window;setIcon(Ljava/io/InputStream;Ljava/io/InputStream;)V"),
            method = "<init>")
    private void setWindowIcon(Args args) {
        try {
            InputStream inputStream = MiscUtil.getInputStreamFromModJar("assets/foxclient/icons/icon_16x16.png");
            InputStream inputStream2 = MiscUtil.getInputStreamFromModJar("assets/foxclient/icons/icon_32x32.png");

            args.setAll(inputStream, inputStream2);
            System.out.println("[FoxClient] set icon");
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "getWindowTitle", cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        String title = "FoxClient " + Main.VERSION;

        title += " | Minecraft " + SharedConstants.getGameVersion().getName();

        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if(Main.config_instance.getBoolean("misc", "discord-rpc")) {
            System.out.println("yes");
            PresenceUpdater.setState(DiscordMinecraftClient.getState(clientPlayNetworkHandler));
        }

        if(clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            title += " - ";
            if(this.server != null && !this.server.isRemote()) {
                title += I18n.translate("title.singleplayer");
            } else if(MinecraftClient.getInstance().isConnectedToRealms()) {
                title += I18n.translate("title.multiplayer.realms");
            } else if(this.server == null && (this.currentServerEntry == null || ! this.currentServerEntry.isLocal())) {
                if(this.currentServerEntry != null && this.currentServerEntry.address != null || Main.config_instance.getBoolean("misc", "discord-rpc-show-ip")) {
                    title += I18n.translate("title.multiplayer.other2", this.currentServerEntry.address);
                } else {
                    title += I18n.translate("title.multiplayer.other");
                }
            } else {
                title += I18n.translate("title.multiplayer.lan");
            }
        }

        cir.setReturnValue(title);
    }
}
