package net.foxes4life.foxclient.notification;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class Toaster implements Toast {
    private String icon;
    private final Text title;
    private final Text description;
    @SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
    private Visibility visibility;
    @SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantSuppression"})
    private float progress;
    private long startTime;
    private boolean justUpdated;

    public Toaster(@Nullable String icon, Text title, @Nullable Text description) {
        this.visibility = Visibility.SHOW;
        this.icon = icon;
        this.title = title;
        this.description = description;
    }

    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        manager.getClient().getTextureManager().bindTexture(TEXTURE);
        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());

        if (this.description == null) {
            manager.getClient().textRenderer.draw(matrices, this.title, 30.0F, 12.0F, -11534256);
        } else {
            manager.getClient().textRenderer.draw(matrices, this.title, 30.0F, 7.0F, -11534256);
            manager.getClient().textRenderer.draw(matrices, this.description, 30.0F, 18.0F, -16777216);

            try {
                if(icon == null) {
                    // default steve icon
                    //noinspection SpellCheckingInspection
                    icon = "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";
                }

                Identifier id = manager.getClient().getTextureManager().registerDynamicTexture("toaster_icon_"+icon.hashCode(), new NativeImageBackedTexture(NativeImage.read(icon)));

                RenderSystem.setShaderTexture(0, id);

                manager.getClient().getTextureManager().bindTexture(id);

                // 38
                DrawableHelper.drawTexture(matrices, 5, 5, 0, 0, 0, 22, 22, 22, 22);
                manager.getClient().getTextureManager().destroyTexture(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return startTime - this.startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public void hide() {
        this.visibility = Visibility.HIDE;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public void setProgress(float progress) {
        this.progress = progress;
    }
}

