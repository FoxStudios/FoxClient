package net.ddns.rootrobo.foxclient.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ddns.rootrobo.foxclient.gui.widgets.NicerButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FoxClientTitleScreen extends Screen {

    private static final Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg.png");
    private static final Identifier BUTTON_BOX = new Identifier("foxclient", "textures/ui/main_box.png");
    private static final Identifier FOMX = new Identifier("foxclient", "textures/ui/title/fomx.png");

    private List<OrderedText> tooltipText;

    private final boolean doBackgroundFade;

    private long backgroundFadeStart;

    public FoxClientTitleScreen(boolean doBackgroundFade) {
        super(new TranslatableText("FoxClient"));
        this.doBackgroundFade = doBackgroundFade;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void tick() {

    }

    protected void init() {
        assert this.client != null;
        this.client.keyboard.setRepeatEvents(true);

        int y = this.height / 2 + 20;
        int spacingY = 24;

        // VANILLA BUTTONS
        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"),
                (button) -> {
                    assert this.client != null;
                    this.client.setScreen(new SelectWorldScreen(this));
                })
        );

        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, y + spacingY, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> {
            this.client.setScreen(new MultiplayerScreen(this));
        }));
    }

    public void onClose() {

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;

        // draw background
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

        // draw button box
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BUTTON_BOX);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices, (width/2) - (250/2), height/2 - (250/3), 0, 0, 250, 175, 250, 175);

        // draw fomx
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FOMX);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices,
                (width/2) - (128/2),
                height/2 - 128 + 32,
                0,
                0,
                128,
                128,
                128,
                128);

        this.tooltipText = null;
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderOrderedTooltip(matrices, this.tooltipText, mouseX, mouseY);
        }
    }

    public void setTooltip(List<OrderedText> list) {
        this.tooltipText = list;
    }
}