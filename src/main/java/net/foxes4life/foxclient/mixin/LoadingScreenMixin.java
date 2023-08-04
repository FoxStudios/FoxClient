package net.foxes4life.foxclient.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LoadingScreenMixin extends Screen {
    @Shadow @Final @Mutable
    private WorldGenerationProgressTracker progressProvider;
    @Shadow
    @Final
    @Mutable
    private static Object2IntMap<ChunkStatus> STATUS_TO_COLOR;

    float progress;

    @Unique
    private double progress = 0.0;

    @Unique
    private Progressbar progressbar;

    protected LoadingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        if (progressbar == null)
            progressbar = new Progressbar(this.width / 2 - 75, this.height / 2 + 5, 150, 10);
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, TextUtils.string("Loading World... " + progressProvider.getProgressPercentage() + "%"), this.width / 2, this.height / 2 - 10, 0xFFFFFF);

        for (int r = 0; r < progressProvider.getSize(); ++r) {
            for (int s = 0; s < progressProvider.getSize(); ++s) {
                ChunkStatus chunkStatus = progressProvider.getChunkStatus(r, s);
                int t = n + r * 2;
                int u = o + s * 2;
                fill(matrices, t, u, t + 2, u + 2, STATUS_TO_COLOR.getInt(chunkStatus) | -16777216);
            }
        }
    }
}
