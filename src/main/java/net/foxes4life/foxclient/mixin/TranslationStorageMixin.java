package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.UwUfyUtils;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TranslationStorage.class)
public abstract class TranslationStorageMixin {
    @Inject(at = @At("RETURN"), method = "get", cancellable = true)
    private void get(String key, String fallback, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(UwUfyUtils.uwufy(cir.getReturnValue()));
    }
}
