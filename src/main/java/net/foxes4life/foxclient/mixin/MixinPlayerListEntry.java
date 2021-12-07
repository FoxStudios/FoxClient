package net.foxes4life.foxclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.foxes4life.foxclient.client.Capes;
import net.foxes4life.foxclient.client.PlayerCape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow private boolean texturesLoaded;

    @Inject(method = "loadTextures", at = @At("HEAD"))
    private void loadTextures(CallbackInfo ci) {
        assert MinecraftClient.getInstance().player != null;
        UUID uuid = profile.getId();

        if(!texturesLoaded) {
            System.out.println("Loading cape for " + uuid.toString());
        }

        PlayerCape cape = Capes.getCape(uuid);
        new Thread(() -> {
            if(cape != null) {
                Identifier capeTexture = cape.getTexture();
                if(capeTexture != null) {
                    this.textures.put(MinecraftProfileTexture.Type.CAPE, capeTexture);
                }
            }
        }).start();
    }
}