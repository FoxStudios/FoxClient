package net.foxes4life.foxclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.foxes4life.foxclient.capes.Provider;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public final class PlayerListEntryMixin {
    @Shadow
    @Final
    private GameProfile profile;

    // Note that loadTextures()V might be called like a foxton,
    // so rejecting to run it has to be really fast
    /*
    @Inject(at = @At("TAIL"), method = "getSkinTextures", cancellable = true)
    private void loadTextures(CallbackInfoReturnable<SkinTextures> cir) {
        // this is broken as hell, todo: fix
        Provider.loadCape(this.profile, texture -> {
            if (texture.capeTexture == null && texture.elytraTexture == null) {
                return;
            }

            SkinTextures original = cir.getReturnValue();
            SkinTextures modified = new SkinTextures(original.texture(),
                    original.textureUrl(),
                    texture.capeTexture,
                    texture.elytraTexture,
                    original.model(),
                    original.secure());
            cir.setReturnValue(modified);
        });
    }
    */
}