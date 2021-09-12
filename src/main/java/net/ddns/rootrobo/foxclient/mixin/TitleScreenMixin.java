package net.ddns.rootrobo.foxclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ddns.rootrobo.foxclient.Main;
import net.ddns.rootrobo.foxclient.client.Updater;
import net.ddns.rootrobo.foxclient.gui.ConfigTestScreen;
import net.ddns.rootrobo.foxclient.gui.FoxClientTitleScreen;
import net.ddns.rootrobo.foxclient.gui.widgets.NicerButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow @Final
    private boolean doBackgroundFade;

    @Shadow
    private long backgroundFadeStart;

    @Final
    @Shadow @Mutable
    private boolean isMinceraft;

    @Shadow
    private String splashText;

    @Final
    @Shadow
    private static Identifier ACCESSIBILITY_ICON_TEXTURE;

    private static Identifier menu_box_texture;

    private static int menu_box_texture_image_width;

    private static int menu_box_texture_image_height;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        assert this.client != null;
        this.clearChildren();

        int width = 20;
        int height = 20;

        int y = this.height / 4 + 48;
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

        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, new TranslatableText("menu.online"), (button) -> {
            this.client.setScreen(new RealmsMainScreen(this));
        }));

        try {
            InputStream in = MinecraftClient.getInstance().getResourcePackProvider().getPack().open(
                    ResourceType.CLIENT_RESOURCES,
                    new Identifier("foxclient", "textures/ui/main_box.png"));

            NativeImage menu_box_texture_image = NativeImage.read(in);

            menu_box_texture_image_width = menu_box_texture_image.getWidth();

            menu_box_texture_image_height = menu_box_texture_image.getHeight();
            menu_box_texture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("menu_box_texture",
                    new NativeImageBackedTexture(menu_box_texture_image));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // things
        /*
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, y + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, (button) -> {
            this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
        }, new TranslatableText("narrator.button.language")));
        */

        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, y + 72 + 12, 98, 20, new TranslatableText("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));

        this.addDrawableChild(new NicerButtonWidget(this.width / 2 + 2, y + 72 + 12, 98, 20, new TranslatableText("menu.quit"), (button) -> {
            this.client.scheduleStop();
        }));
        /*
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, y + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, (button) -> {
            this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
        }, new TranslatableText("narrator.button.accessibility")));
        */
        // yes

        /*
        // FOXCLIENT BUTTONS
        this.addDrawableChild(new ButtonWidget(this.width - 10 - width, y - spacingY, width, height, new TranslatableText("foxclient.gui.button.update"), this::updateButton));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y - spacingY, 200, 20, new TranslatableText("foxclient.debug.gui.button.configtest"), (buttonWidget) -> {
            assert this.client != null;
            this.client.setScreen(new ConfigTestScreen(this));
        }));
        */
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        MinecraftClient.getInstance().setScreen(new FoxClientTitleScreen(true));

        int y = this.height / 4 + 48;

        float fadeTime = this.doBackgroundFade ? (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        float fadeColor = this.doBackgroundFade ? MathHelper.clamp(fadeTime - 1.0F, 0.0F, 1.0F) : 1.0F;

        int alpha = MathHelper.ceil(fadeColor * 255.0F) << 24;
        if ((alpha & 0xFC000000) != 0) {
            if(FabricLoader.getInstance().isModLoaded("optifabric")) {
                textRenderer.drawWithShadow(matrices, Text.of("FoxClient "+Main.VERSION), 2, this.height - 30, 0xFF7700 | alpha);
            } else {
                textRenderer.drawWithShadow(matrices, Text.of("FoxClient "+Main.VERSION), 2, this.height - 20, 0xFF7700 | alpha);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, menu_box_texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        MinecraftClient.getInstance().getTextureManager().bindTexture(menu_box_texture);
        this.drawTexture(matrices, (width/2) - (menu_box_texture_image_width/2/2), y, 0, 0, menu_box_texture_image_width / 2, this.height/3);

        /*
        this.drawTexture(matrices, (width/2)-menu_box_texture_image_width, 5, 0, 0, 0,
                menu_box_texture_image_width/2,
                menu_box_texture_image_height/2,
                menu_box_texture_image_width, menu_box_texture_image_height);

         */
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
