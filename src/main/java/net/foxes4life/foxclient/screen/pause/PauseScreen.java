package net.foxes4life.foxclient.screen.pause;

import net.fabricmc.loader.api.FabricLoader;
import net.foxes4life.foxclient.ui.button.FoxClientButton;
import net.foxes4life.foxclient.screen.title.TitleScreen;
import net.foxes4life.foxclient.ui.toast.FoxClientToast;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.lang.reflect.InvocationTargetException;

public class PauseScreen extends Screen {
    public PauseScreen() {
        super(TextUtils.string("Pause Menu"));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    public void init() {
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 24 - 16, 204, 20, TextUtils.translatable("menu.returnToGame"), (button) -> {

            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 48 - 16, 98, 20, TextUtils.translatable("gui.advancements"), (button) -> {
            this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 48 - 16, 98, 20, TextUtils.translatable("gui.stats"), (button) -> {
            this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
        }));
        String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 72 - 16, 98, 20, TextUtils.translatable("menu.sendFeedback"), (button) -> {
            this.client.setScreen(new ConfirmLinkScreen((confirmed) -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(string);
                }

                this.client.setScreen(this);
            }, string, true));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 72 - 16, 98, 20, TextUtils.translatable("menu.reportBugs"), (button) -> {
            this.client.setScreen(new ConfirmLinkScreen((confirmed) -> {
                if (confirmed) {
                    Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
                }

                this.client.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 96 - 16, 98, 20, TextUtils.translatable("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));
        FoxClientButton buttonWidget = this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, TextUtils.translatable("menu.shareToLan"), (button) -> {
            this.client.setScreen(new OpenToLanScreen(this));
        }));

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> modMenuGui = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");

                this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, TextUtils.translatable("modmenu.options"), (button) -> {
                    try {
                        this.client.setScreen((Screen) modMenuGui.getDeclaredConstructor(Screen.class).newInstance(this));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }));
            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
        } else {
            FoxClientToast toaster = this.client.getToastManager().getToast(FoxClientToast.class, Toast.TYPE);
            if (toaster == null) {
                this.client.getToastManager().add(new FoxClientToast(
                        Text.of("FoxClient"), TextUtils.translatable("foxclient.gui.toast.modmenu.missing")));
            }
        }


        buttonWidget.active = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
        Text text = this.client.isInSingleplayer() ? TextUtils.translatable("menu.returnToMenu") : TextUtils.translatable("menu.disconnect");
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 145 - 16, 204, 20, text, (button) -> {
            boolean bl = this.client.isInSingleplayer();
            boolean bl2 = this.client.isConnectedToRealms();
            button.active = false;
            this.client.world.disconnect();
            if (bl) {
                this.client.disconnect(new MessageScreen(TextUtils.translatable("menu.savingLevel")));
            } else {
                this.client.disconnect();
            }

            net.minecraft.client.gui.screen.TitleScreen titleScreen = new net.minecraft.client.gui.screen.TitleScreen();
            if (bl) {
                this.client.setScreen(titleScreen);
            } else if (bl2) {
                this.client.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                this.client.setScreen(new MultiplayerScreen(titleScreen));
            }

        }));
    }

    void exitToMenu() {
        boolean inSingleplayer = this.client.isInSingleplayer();
        boolean inRealms = this.client.isConnectedToRealms();

        this.client.world.disconnect();

        if (inSingleplayer) {
            this.client.disconnect(new MessageScreen(TextUtils.translatable("menu.savingLevel")));
        } else {
            this.client.disconnect();
        }

        TitleScreen title = new TitleScreen(false);

        if (inSingleplayer) {
            this.client.setScreen(title);
        } else if (inRealms) {
            this.client.setScreen(new RealmsMainScreen(title));
        } else {
            this.client.setScreen(new MultiplayerScreen(title));
        }
    }
}
