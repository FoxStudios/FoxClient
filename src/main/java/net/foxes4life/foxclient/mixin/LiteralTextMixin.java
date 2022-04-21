package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.util.UwUfyUtils;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LiteralText.class)
public class LiteralTextMixin {
    @Inject(at = @At("RETURN"), method = "asString", cancellable = true)
    private void asString(CallbackInfoReturnable<String> cir) {
        if(Main.config_instance.getBoolean("eastereggs", "owo")) {
            String text = cir.getReturnValue();
            text = UwUfyUtils.uwufy(text);
            cir.setReturnValue(text);
        }
    }

    @Inject(at = @At("RETURN"), method = "getRawString", cancellable = true)
    private void getRawString(CallbackInfoReturnable<String> cir) {
        String text = cir.getReturnValue();
        text = UwUfyUtils.uwufy(text);
        cir.setReturnValue(text);
    }
}
