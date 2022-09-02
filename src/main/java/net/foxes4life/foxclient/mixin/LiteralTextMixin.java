package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.util.UwUfyUtils;
import net.minecraft.text.LiteralTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiteralTextContent.class)
public class LiteralTextMixin {
    @Inject(at = @At("RETURN"), method = "string", cancellable = true)
    private void asString(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(UwUfyUtils.uwufy(cir.getReturnValue()));
    }
}