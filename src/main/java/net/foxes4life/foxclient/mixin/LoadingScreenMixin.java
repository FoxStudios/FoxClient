package net.foxes4life.foxclient.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LoadingScreenMixin extends Screen {
    @Shadow
    @Final
    @Mutable
    private WorldGenerationProgressTracker progressProvider;
    @Shadow
    @Final
    @Mutable
    private static Object2IntMap<ChunkStatus> STATUS_TO_COLOR;

    float progress;

    protected LoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        renderBackground(context);
        context.drawTextWithShadow(textRenderer, TextUtils.string("Loading World... " + MathHelper.clamp(progressProvider.getProgressPercentage(), 0, 100) + "%"), 10, this.height - 22, 0xFFFFFF);
        this.progress = MathHelper.lerp(0.88f, progress, this.progress * 0.95F + (progressProvider.getProgressPercentage() / 100f) * 0.2F);
        context.fill(0, this.height - 3, (int) (this.width * progress), this.height, 0xFFFFFFFF);
        drawChunkThing(context, progressProvider);
    }

    public void drawChunkThing(DrawContext context, WorldGenerationProgressTracker progressProvider) {
        int m = progressProvider.getSize() * 2;
        int n = this.width - 5 - m;
        int o = this.height - 7 - m;

        for (int r = 0; r < progressProvider.getSize(); ++r) {
            for (int s = 0; s < progressProvider.getSize(); ++s) {
                ChunkStatus chunkStatus = progressProvider.getChunkStatus(r, s);
                int t = n + r * 2;
                int u = o + s * 2;

                context.fill(t, u, t + 2, u + 2, STATUS_TO_COLOR.getInt(chunkStatus) | -16777216);
            }
        }
    }
}
