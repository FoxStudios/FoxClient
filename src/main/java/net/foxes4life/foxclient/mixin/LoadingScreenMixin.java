package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.MainClient;
import net.foxes4life.foxclient.ui.Progressbar;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.WorldGenerationProgressTracker;
//import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*
@Mixin(LevelLoadingScreen.class)
public abstract class LoadingScreenMixin extends Screen {
    @Shadow @Final @Mutable
    private WorldGenerationProgressTracker progressProvider;

    @Unique
    private double progress = 0.0;

    @Unique
    private Progressbar progressbar;

    protected LoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();

        if (progressbar == null)
            progressbar = new Progressbar(this.width / 2 - 75, this.height / 2 + 5, 150, 10);

        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, TextUtils.string("Loading World... " + progressProvider.getProgressPercentage() + "%"), this.width / 2, this.height / 2 - 10, 0xFFFFFF);

        progress = MathHelper.lerp(Math.exp(-0.03 * MainClient.deltaTime), progressProvider.getProgressPercentage() / 100f, progress);
        progressbar.render(context, progress);
    }
}
*/