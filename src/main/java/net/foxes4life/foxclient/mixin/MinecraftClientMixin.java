package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.discord.DiscordMinecraftClient;
import net.foxes4life.foxclient.discord.PresenceUpdater;
import net.foxes4life.foxclient.hud.ClientOverlayHud;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    private IntegratedServer server;

    @Shadow
    private ServerInfo currentServerEntry;

    @Inject(at = @At("HEAD"), method = "getWindowTitle", cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        StringBuilder stringBuilder = new StringBuilder("FoxClient");

        stringBuilder.append(" ");
        if(FabricLoader.getInstance().getModContainer("foxclient").isPresent()) {
            stringBuilder.append(FabricLoader.getInstance().getModContainer("foxclient").get().getMetadata().getVersion());
        }

        stringBuilder.append(" | ");
        stringBuilder.append("Playing Minecraft ");
        stringBuilder.append(SharedConstants.getGameVersion().getName());
        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer"));
            } else if (MinecraftClient.getInstance().isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms"));
            } else if (this.server == null && (this.currentServerEntry == null || !this.currentServerEntry.isLocal())) {
                try {
                    if(Objects.requireNonNull(this.currentServerEntry).address != null) {
                        stringBuilder.append(I18n.translate("title.multiplayer.other2", this.currentServerEntry.address));
                    } else {
                        stringBuilder.append(I18n.translate("title.multiplayer.other"));
                    }
                } catch (NullPointerException ignored) {
                    stringBuilder.append(I18n.translate("title.multiplayer.other"));
                }
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.lan"));
            }
        }
        PresenceUpdater.setState(DiscordMinecraftClient.getState(clientPlayNetworkHandler));
        stringBuilder.append(" ");
        stringBuilder.append("(Logged in as ");
        stringBuilder.append(MinecraftClient.getInstance().getSession().getUsername());
        stringBuilder.append(")");

        cir.setReturnValue(stringBuilder.toString());
    }

    private int this_tick = 0;

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if(MinecraftClient.getInstance() != null && !MinecraftClient.getInstance().fpsDebugString.equals("")) {
            this_tick++;
            if(this_tick >= 20) {
                this_tick = 0;
                PresenceUpdater.setState(DiscordMinecraftClient.getState(MinecraftClient.getInstance().getNetworkHandler()));
            }
            ClientOverlayHud.tick();
        }
    }
}