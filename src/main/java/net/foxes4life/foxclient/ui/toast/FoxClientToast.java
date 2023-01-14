package net.foxes4life.foxclient.ui.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class FoxClientToast implements Toast {
    private final Text title;
    private final Text description;
    private final long duration;
    private long startTime;
    private boolean justUpdated;

    public FoxClientToast(Text title, @Nullable Text description) {
        this.title = title;
        this.description = description;
        duration = 2500L;
    }

    public FoxClientToast(Text title, @Nullable Text description, long duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        int color = new Color(46, 34, 26, 102).getRGB();
        int cornerSize = 3;

        RenderSystem.enableBlend();
        DrawableHelper.fill(matrices, 0, cornerSize, cornerSize, getHeight() - cornerSize, color);
        DrawableHelper.fill(matrices, cornerSize, 0, getWidth(), getHeight(), color);

        manager.getClient().textRenderer.draw(matrices, this.title, 7, 7, 0xf77622);
        manager.getClient().textRenderer.draw(matrices, this.description, 7, 18, 0xffffff);

        return startTime - this.startTime >= duration ? Visibility.HIDE : Visibility.SHOW;
    }
}