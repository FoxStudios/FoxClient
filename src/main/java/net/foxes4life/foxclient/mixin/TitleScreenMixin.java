package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.client.Updater;
import net.foxes4life.foxclient.gui.FoxClientTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init", cancellable = true)
    private void init(CallbackInfo ci) {
        ci.cancel();
        assert this.client != null;
        this.client.setScreen(new FoxClientTitleScreen(false));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        MinecraftClient.getInstance().setScreen(new FoxClientTitleScreen(true));
    }

    private void updateButton(ButtonWidget buttonWidget) {
        assert this.client != null;
        buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.checking"));
        buttonWidget.active = false;
        new Thread(() -> {
            int status = Updater.prepareUpdate();

            if(status == 0) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.downloading.pre"));
                try {
                    Updater.update(buttonWidget);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(status == 1) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.latest"));
            } else if(status == 2) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.dev"));
            } else if(status == -1) {
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.button.update.failed"));
            }
        }).start();
    }
}
