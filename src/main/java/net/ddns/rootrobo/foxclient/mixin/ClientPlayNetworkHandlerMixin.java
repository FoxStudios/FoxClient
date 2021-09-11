package net.ddns.rootrobo.foxclient.mixin;

import io.netty.buffer.ByteBuf;
import net.ddns.rootrobo.foxclient.egg.EggManager;
import net.ddns.rootrobo.foxclient.egg.UwUifier;
import net.ddns.rootrobo.foxclient.networking.ClientNetworking;
import net.ddns.rootrobo.foxclient.networking.ServerTickStuff;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
	//private static int oldviewdistance;
	//private static boolean revert = false;

	@Shadow @Final private MinecraftClient client;

	@Inject(at = @At("RETURN"), method = "onGameJoin")
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
		System.out.println("Server Render Distance: "+packet.getViewDistance());
		ServerTickStuff.onJoin();
		System.out.println("Registering \"foxclient:msgreceive\" payload.");
		ClientPlayNetworking.registerReceiver(new Identifier("foxclient", "msgreceive"), ClientNetworking::onClientMessageReceived);
	}

	@Inject(at = {@At("HEAD")},
			method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
			cancellable = true)
	private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
		if(packet instanceof ChatMessageC2SPacket) {
			System.out.println(((ChatMessageC2SPacket)packet).getChatMessage());
			if(EggManager.isOwOEnabled()) {
				if(((ChatMessageC2SPacket)packet).getChatMessage().startsWith("owo.")) {
					if(MinecraftClient.getInstance().player != null) {
						MinecraftClient.getInstance().player.
								sendChatMessage(UwUifier.OwOify(
										((ChatMessageC2SPacket)packet).getChatMessage().substring("owo.".length())
								));
						ci.cancel();
					}
				}
			}
			/*
			if(((ChatMessageC2SPacket)packet).getChatMessage().startsWith("c.")) {
				if(!ClientChat.send(((ChatMessageC2SPacket)packet).getChatMessage().substring("c.".length()))) {
					if (MinecraftClient.getInstance().player != null) {
						MinecraftClient.getInstance().player.sendMessage(new TranslatableText("foxclient.chat.clientchat.send_failed"), false);
					}
				}
				ci.cancel();
			}
			 */
		}
	}

	@Inject(at = @At("TAIL"), method = "onWorldTimeUpdate")
	private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ServerTickStuff.onWorldTimeUpdate(packet);
	}

	@Inject(at = @At("HEAD"), method = "onCustomPayload")
	private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		ByteBuf clonedBuf = new PacketByteBuf(packet.getData().duplicate()).asReadOnly();
		System.out.println("[IN] CHANNEL: "+packet.getChannel());
		System.out.println("[IN] DATA: "+clonedBuf.toString(StandardCharsets.UTF_8));
	}
}
