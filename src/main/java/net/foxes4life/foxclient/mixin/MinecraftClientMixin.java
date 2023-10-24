package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.MainClient;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.rpc.DiscordMinecraftClient;
import net.foxes4life.foxclient.rpc.PresenceUpdater;
import net.foxes4life.foxclient.screen.pause.PauseScreen;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    private IntegratedServer server;

    /*
    @ModifyArgs(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/Window;setIcon(Ljava/io/InputStream;Ljava/io/InputStream;)V"),
            method = "<init>")
    private void setWindowIcon(Args args) {
        try {
            InputStream inputStream = MiscUtil.getInputStreamFromModJar("assets/foxclient/icons/icon_16x16.png");
            InputStream inputStream2 = MiscUtil.getInputStreamFromModJar("assets/foxclient/icons/icon_32x32.png");

            args.setAll(inputStream, inputStream2);
            Main.LOGGER.debug("[FoxClient] set icon");
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    */

    @Inject(at = @At("HEAD"), method = "getWindowTitle", cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        String title = "FoxClient " + Main.SIMPLE_VERSION;

        title += " | Minecraft " + SharedConstants.getGameVersion().getName();

        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
        ServerInfo currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();

        if (Main.config.get(FoxClientSetting.DiscordEnabled, Boolean.class)) {
            PresenceUpdater.setState(DiscordMinecraftClient.getState(clientPlayNetworkHandler));
        }

        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            title += " - ";
            if (this.server != null && !this.server.isRemote()) {
                title += I18n.translate("title.singleplayer");
            } else if (currentServerEntry != null && currentServerEntry.isRealm()) {
                title += I18n.translate("title.multiplayer.realms");
            } else if (this.server == null && (currentServerEntry == null || !currentServerEntry.isLocal())) {
                if (currentServerEntry != null && currentServerEntry.address != null || Main.config.get(FoxClientSetting.DiscordShowIP, Boolean.class)) {
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

    @Inject(at = @At("HEAD"), method = "openGameMenu", cancellable = true)
    public void openPauseMenu(boolean pause, CallbackInfo ci) {
        if (Main.config.get(FoxClientSetting.CustomPauseMenu, Boolean.class)) {
            ci.cancel();
            if (MinecraftClient.getInstance().currentScreen == null) {
                MinecraftClient.getInstance().setScreen(new PauseScreen());
            }
        }
    }

    @Unique
    private long lastTime = 0;

    @Inject(at = @At("HEAD"), method = "render")
    public void render(boolean tick, CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        MainClient.deltaTime = currentTime - lastTime;
        MainClient.transformManager.update(MainClient.deltaTime);
        lastTime = currentTime;
    }
}
