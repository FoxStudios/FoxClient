package net.ddns.rootrobo.foxclient.mixin;
import java.util.Map;

import net.ddns.rootrobo.foxclient.egg.EggManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.ddns.rootrobo.foxclient.egg.UwUifier;

import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {
    @Final
    @Shadow
    @Mutable
    private Map<String, String> translations;

    @Inject(at = @At("HEAD"), method = "get", cancellable = true)
    public void get(String key, CallbackInfoReturnable<String> cir) {
        if(EggManager.isOwOEnabled()) {
            cir.setReturnValue(UwUifier.OwOify(translations.getOrDefault(key, key)));
        }
    }
}