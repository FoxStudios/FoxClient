package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.util.UwUfyUtils;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TranslationStorage.class)
public abstract class TranslationStorageMixin {
    @Inject(at = @At("RETURN"), method = "get", cancellable = true)
    private void get(String key, CallbackInfoReturnable<String> cir) {
        if(Main.config_instance.getBoolean("eastereggs", "owo")) {
            String text = cir.getReturnValue();
            text = UwUfyUtils.uwufy(text);
            cir.setReturnValue(text);
        }
    }
}
