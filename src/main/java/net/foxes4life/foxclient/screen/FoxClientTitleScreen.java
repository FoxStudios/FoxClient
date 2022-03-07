package net.foxes4life.foxclient.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.gui.FoxClientButton;
import net.foxes4life.foxclient.gui.FoxClientMiniButton;
import net.foxes4life.foxclient.gui.bundering.GayToaster;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.toast.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

@Environment(EnvType.CLIENT)
public class FoxClientTitleScreen extends Screen {
    private static int backgroundIndex = 0;
    private static final int backgroundAmount = 2;

    // ui
    private static Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/0.png");
    private static final Identifier BUTTON_BOX = new Identifier("foxclient", "textures/ui/main_box.png");
    private static final Identifier FOMX = new Identifier("foxclient", "textures/ui/title/fomx.png");
    // bg
    private static final Identifier EXIT_BUTTON = new Identifier("foxclient", "textures/ui/buttons/exit.png");
    private static final Identifier OPTIONS_BUTTON = new Identifier("foxclient", "textures/ui/buttons/options.png");
    private static final Identifier EMPTY_BUTTON = new Identifier("foxclient", "textures/ui/buttons/empty.png");
    private static final Identifier ACCESSIBILITY_BUTTON = new Identifier("foxclient", "textures/ui/buttons/accessibility.png");
    private static final Identifier MODS_BUTTON = new Identifier("foxclient", "textures/ui/buttons/modmenu.png");
    private static final Identifier REALMS_BUTTON = new Identifier("foxclient", "textures/ui/buttons/realms.png");
    private static final Identifier FOXCLIENT_OPTIONS_BUTTON = new Identifier("foxclient", "textures/ui/buttons/tail.png");
    private static final Identifier DISCORD_BUTTON = new Identifier("foxclient", "textures/ui/buttons/discord.png");
    //private static final Identifier REPLAYMOD_BUTTON = new Identifier("foxclient", "textures/ui/buttons/empty.png");
    //private static final Identifier UPDATE_BUTTON = new Identifier("foxclient", "textures/ui/buttons/empty.png");


    private static final Text mojangCopyrightText = TitleScreen.COPYRIGHT;
    private int mojangCopyrightTextWidth;
    private int mojangCopyrightTextX;

    private static final String foxclientCopyrightText = "© FoxClient 2021-2022";
    private int foxclientCopyrightTextWidth;
    private int foxclientCopyrightTextX;

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

        int y = this.height / 2 + 10;
        int spacingY = 24;

        backgroundIndex++;
        if(backgroundIndex >= backgroundAmount) backgroundIndex = 0;
        BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/" + backgroundIndex + ".png");

        // VANILLA BUTTONS
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"),
                (button) -> {
                    assert this.client != null;
                    this.client.setScreen(new SelectWorldScreen(this));
                })
        );

        this.addDrawableChild(new FoxClientButton(this.width / 2 - 100, y + spacingY, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> this.client.setScreen(new MultiplayerScreen(this))));

        // - small buttons
        int button_id = 7; // set amount of buttons here

        button_id++;
        spacingY = spacingY + 2;

        // discord button
        button_id--;
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, DISCORD_BUTTON, 32, 64, (button) -> {
            try {
                Desktop.getDesktop().browse(new URL("https://discord.gg/JG99fvjCtU").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }, new TranslatableText("discord")));

        // accessibility button
        button_id--;
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, ACCESSIBILITY_BUTTON, 32, 64, (button) -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), new TranslatableText("narrator.button.accessibility")));

        // mods button
        button_id--;
        FoxClientMiniButton modButton = this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, MODS_BUTTON, 32, 64, (button) -> {
            if(FabricLoader.getInstance().isModLoaded("modmenu")) {
                try {
                    Class<?> modMenuGui = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");

                    this.client.setScreen((Screen) modMenuGui.getDeclaredConstructor(Screen.class).newInstance(this));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                    e.printStackTrace();
                }
            } else {
                // todo: add a popup of some sort to inform the user that mod menu is required for this
                // amogus balls

                GayToaster toaster = MinecraftClient.getInstance().getToastManager().getToast(GayToaster.class, Toast.TYPE);
                if (toaster == null) {
                    MinecraftClient.getInstance().getToastManager().add(new GayToaster(
                            Text.of("FoxClient"), new TranslatableText("foxclient.gui.toast.modmenu.missing")));
                } else {
                    System.out.println("""
                            we do a little trolling
                            QQQQQQQQQQQQQQQQQQQWQQQQQWWWBBBHHHHHHHHHBWWWQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ
                            QQQQQQQQQQQQQQQD!`__ssaaaaaaaaaass_ass_s____.  -~""??9VWQQQQQQQQQQQQQQQQQQQ
                            QQQQQQQQQQQQQP'_wmQQQWWBWV?GwwwmmWQmwwwwwgmZUVVHAqwaaaac,"?9$QQQQQQQQQQQQQQ
                            QQQQQQQQQQQW! aQWQQQQW?qw#TTSgwawwggywawwpY?T?TYTYTXmwwgZ$ma/-?4QQQQQQQQQQQ
                            QQQQQQQQQQW' jQQQQWTqwDYauT9mmwwawww?WWWWQQQQQ@TT?TVTT9HQQQQQQw,-4QQQQQQQQQ
                            QQQQQQQQQQ[ jQQQQQyWVw2$wWWQQQWWQWWWW7WQQQQQQQQPWWQQQWQQw7WQQQWWc)WWQQQQQQQ
                            QQQQQQQQQf jQQQQQWWmWmmQWU???????9WWQmWQQQQQQQWjWQQQQQQQWQmQQQQWL 4QQQQQQQQ
                            QQQQQQQP'.yQQQQQQQQQQQP"       <wa,.!4WQQQQQQQWdWP??!"??4WWQQQWQQc ?QWQQQQQ
                            QQQQQP'_a.<aamQQQW!<yF "!` ..  "??$Qa "WQQQWTVP'    "??' =QQmWWV?46/ ?QQQQQ
                            QQQP'sdyWQP?!`.-"?46mQQQQQQT!mQQgaa. <wWQQWQaa _aawmWWQQQQQQQQQWP4a7g -WWQQ
                            QQ[ j@mQP'adQQP4ga, -????" <jQQQQQWQQQQQQQQQWW;)WQWWWW9QQP?"`  -?QzQ7L ]QQQ
                            QW jQkQ@ jWQQD'-?$QQQQQQQQQQQQQQQQQWWQWQQQWQQQc "4QQQQa   .QP4QQQQfWkl jQQQ
                            QE ]QkQk $D?`  waa "?9WWQQQP??T?47`_aamQQQQQQWWQw,-?QWWQQQQQ`"QQQD\\Qf(.QWQQ
                            QQ,-Qm4Q/-QmQ6 "WWQma/  "??QQQQQQL 4W"- -?$QQQQWP`s,awT$QQQ@  "QW@?$:.yQQQQ
                            QQm/-4wTQgQWQQ,  ?4WWk 4waac -???$waQQQQQQQQF??'<mWWWWWQW?^  ` ]6QQ' yQQQQQ
                            QQQQw,-?QmWQQQQw  a,    ?QWWQQQw _.  "????9VWaamQWV???"  a j/  ]QQf jQQQQQQ
                            QQQQQQw,"4QQQQQQm,-$Qa     ???4F jQQQQQwc <aaas _aaaaa 4QW ]E  )WQ`=QQQQQQQ
                            QQQQQQWQ/ $QQQQQQQa ?H ]Wwa,     ???9WWWh dQWWW,=QWWU?  ?!     )WQ ]QQQQQQQ
                            QQQQQQQQQc-QWQQQQQW6,  QWQWQQQk <c                             jWQ ]QQQQQQQ
                            QQQQQQQQQQ,"$WQQWQQQQg,."?QQQQ'.mQQQmaa,.,                . .; QWQ.]QQQQQQQ
                            QQQQQQQQQWQa ?$WQQWQQQQQa,."?( mQQQQQQW[:QQQQm[ ammF jy! j( } jQQQ(:QQQQQQQ
                            QQQQQQQQQQWWma "9gw?9gdB?QQwa, -??T$WQQ;:QQQWQ ]WWD _Qf +?! _jQQQWf QQQQQQQ
                            QQQQQQQQQQQQQQQws "Tqau?9maZ?WQmaas,,    --~-- ---  . _ssawmQQQQQQk 3QQQQWQ
                            QQQQQQQQQQQQQQQQWQga,-?9mwad?1wdT9WQQQQQWVVTTYY?YTVWQQQQWWD5mQQPQQQ ]QQQQQQ
                            QQQQQQQWQQQQQQQQQQQWQQwa,-??$QwadV}<wBHHVHWWBHHUWWBVTTTV5awBQQD6QQQ ]QQQQQQ
                            QQQQQQQQQQQQQQQQQQQQQQWWQQga,-"9$WQQmmwwmBUUHTTVWBWQQQQWVT?96aQWQQQ ]QQQQQQ
                            QQQQQQQQQQWQQQQWQQQQQQQQQQQWQQma,-?9$QQWWQQQQQQQWmQmmmmmQWQQQQWQQW(.yQQQQQW
                            QQQQQQQQQQQQQWQQQQQQWQQQQQQQQQQQQQga%,.  -??9$QQQQQQQQQQQWQQWQQV? sWQQQQQQQ
                            QQQQQQQQQWQQQQQQQQQQQQQQWQQQQQQQQQQQWQQQQmywaa,;~^"!???????!^`_saQWWQQQQQQQ
                            QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQWWWWQQQQQmwywwwwwwmQQWQQQQQQQQQQQ""");
                }
            }
        }, new TranslatableText("mods")));

        // realms button
        button_id--;
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, REALMS_BUTTON, 32, 64, (button) -> {
            this.client.setScreen(new RealmsMainScreen(this));
        }, new TranslatableText("menu.online")));

        // foxclient options button (debug)
        button_id--;
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, FOXCLIENT_OPTIONS_BUTTON, 32, 64, (button) -> {
            this.client.setScreen(new SettingsMenuScreen());
        }, new TranslatableText("foxclient.debug.gui.button.configtest")));

        // options button
        button_id--;
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, OPTIONS_BUTTON, 32, 64, (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }, new TranslatableText("options.title")));

        // quit button
        button_id--;
        //noinspection ConstantConditions
        this.addDrawableChild(new FoxClientMiniButton(this.width / 2 + 100 - 20*button_id - 8*(button_id-1), y + spacingY * 2, 20, 20, 0, 0, 20, EXIT_BUTTON, 32, 64, (button) -> {
            this.client.scheduleStop();
        }, new TranslatableText("menu.quit")));

        // copyright
        this.mojangCopyrightTextWidth = this.textRenderer.getWidth(mojangCopyrightText);
        this.mojangCopyrightTextX = this.width - this.mojangCopyrightTextWidth - 4;

        this.foxclientCopyrightTextWidth = this.textRenderer.getWidth(foxclientCopyrightText);
        this.foxclientCopyrightTextX = this.width - this.foxclientCopyrightTextWidth - 4;
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
        int fomxSize = 118;
        drawTexture(matrices, (width/2) - (fomxSize/2), height/2 - fomxSize + 32, 0, 0, 128, fomxSize, fomxSize, fomxSize, fomxSize);

        // draw texts
        int transparent = MathHelper.ceil(0.5f * 255.0F) << 24;

        // -> copyright
        drawStringWithShadow(matrices, this.textRenderer, mojangCopyrightText.asString(), this.mojangCopyrightTextX, this.height - 10, 16777215 | transparent);
        // -> copyright hover
        if (mouseX > this.mojangCopyrightTextX && mouseX < this.mojangCopyrightTextX + this.mojangCopyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
            fill(matrices, this.mojangCopyrightTextX, this.height - 1, this.mojangCopyrightTextX + this.mojangCopyrightTextWidth, this.height, 16777215 | transparent);
        }

        String gameVersion = "Minecraft "+ SharedConstants.getGameVersion().getName();
        assert this.client != null;
        if (this.client.isDemo()) {
            gameVersion = gameVersion + " Demo";
        } else {
            gameVersion = gameVersion + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
        }

        drawStringWithShadow(matrices, this.textRenderer, gameVersion, 4, this.height - 10, 16777215 | transparent);
        drawStringWithShadow(matrices, this.textRenderer, "FoxClient "+ Main.VERSION, 4, this.height - 20, 16777215 | transparent);
        drawStringWithShadow(matrices, this.textRenderer, foxclientCopyrightText, this.foxclientCopyrightTextX, this.height - 20, 16777215 | transparent);

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
            assert this.client != null;
            this.client.setScreen(new CreditsScreen(true, Runnables.doNothing()));
        }
        return false;
    }
}