package net.ddns.rootrobo.foxclient.gui;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ddns.rootrobo.foxclient.gui.widgets.NicerButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxClientTitleScreen extends Screen {

    // ui
    private static final Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg.png");
    private static final Identifier BUTTON_BOX = new Identifier("foxclient", "textures/ui/main_box.png");
    private static final Identifier FOMX = new Identifier("foxclient", "textures/ui/title/fomx.png");
    // bg
    private static final Identifier EXIT_BUTTON = new Identifier("foxclient", "textures/ui/buttons/exit.png");
    private static final Identifier OPTIONS_BUTTON = new Identifier("foxclient", "textures/ui/buttons/options.png");


    private static final String mojangCopyrightText = "Copyright Mojang AB. Do not distribute!";
    private int mojangCopyrightTextWidth;
    private int mojangCopyrightTextX;

    private final boolean doBackgroundFade;

    private long backgroundFadeStart;

    public FoxClientTitleScreen(boolean doBackgroundFade) {
        super(new LiteralText("FoxClient"));
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

        // quit button
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 100 - 20, y + spacingY * 2, 20, 20, 0, 0, 20, EXIT_BUTTON, 32, 64, (button) -> {
            this.client.scheduleStop();
        }, new TranslatableText("menu.quit")));

        // options button
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 100 - 20*2 - 8, y + spacingY * 2, 20, 20, 0, 0, 20, OPTIONS_BUTTON, 32, 64, (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }, new TranslatableText("options.title")));

        // copyright
        this.mojangCopyrightTextWidth = this.textRenderer.getWidth(mojangCopyrightText);
        this.mojangCopyrightTextX = this.width - this.mojangCopyrightTextWidth - 2;
    }

    public void onClose() {

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;

        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(g * 255.0F) << 24;

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
        drawTexture(matrices, (width/2) - (128/2), height/2 - 128 + 32, 0, 0, 128, 128, 128, 128);

        // draw copyright
        int transparent = MathHelper.ceil(0.5f * 255.0F) << 24;

        drawStringWithShadow(matrices, this.textRenderer, mojangCopyrightText, this.mojangCopyrightTextX, this.height - 10,
                16777215 | transparent);

        if (mouseX > this.mojangCopyrightTextX && mouseX < this.mojangCopyrightTextX + this.mojangCopyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
            fill(matrices, this.mojangCopyrightTextX, this.height - 1, this.mojangCopyrightTextX + this.mojangCopyrightTextWidth, this.height, 16777215 | transparent);
        }

        // draw buttons
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // stolen from mojang code
        // TODO: IMPORTANT - recode this
        if(super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseX > (double)this.mojangCopyrightTextX && mouseX < (double)(this.mojangCopyrightTextX + this.mojangCopyrightTextWidth) && mouseY > (double)(this.height - 10) && mouseY < (double)this.height) {
            this.client.setScreen(new CreditsScreen(false, Runnables.doNothing()));
        }
        return false;
    }
}