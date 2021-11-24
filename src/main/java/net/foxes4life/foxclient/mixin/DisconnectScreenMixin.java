package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.gui.widgets.NicerButtonWidget;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectScreenMixin extends Screen {
    protected DisconnectScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, this.height - 110, 200, 20, new TranslatableText("foxclient.gui.button.reconnect"), (buttonWidget) -> {
            buttonWidget.setMessage(Text.of("soon"));
        }));
    }
}
