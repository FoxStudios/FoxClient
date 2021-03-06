package net.foxes4life.foxclient.screen.pause;

import net.foxes4life.foxclient.gui.FoxClientButton;
import net.foxes4life.foxclient.screen.mainmenu.FoxClientTitleScreen;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class  FoxClientPauseMenu extends Screen {
    public FoxClientPauseMenu () {
        super(TextUtils.string("Pause Menu"));
    }

    public void render (MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void init () {
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, TextUtils.translatable("menu.returnToGame"), (button) -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, TextUtils.translatable("gui.advancements"), (button) -> {
            this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, TextUtils.translatable("gui.stats"), (button) -> {
            this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
        }));
        String string = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, TextUtils.translatable("menu.sendFeedback"), (button) -> {
            this.client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(string);
                }

                this.client.setScreen(this);
            }, string, true));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, TextUtils.translatable("menu.reportBugs"), (button) -> {
            this.client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                if (confirmed) {
                    Util.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
                }

                this.client.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, TextUtils.translatable("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));
        FoxClientButton buttonWidget = this.addDrawableChild(new FoxClientButton(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, TextUtils.translatable("menu.shareToLan"), (button) -> {
            this.client.setScreen(new OpenToLanScreen(this));
        }));
        buttonWidget.active = this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote();
        Text text = this.client.isInSingleplayer() ? TextUtils.translatable("menu.returnToMenu") : TextUtils.translatable("menu.disconnect");
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, text, (button) -> {
            boolean bl = this.client.isInSingleplayer();
            boolean bl2 = this.client.isConnectedToRealms();
            button.active = false;
            this.client.world.disconnect();
            if (bl) {
                this.client.disconnect(new MessageScreen(TextUtils.translatable("menu.savingLevel")));
            } else {
                this.client.disconnect();
            }

            TitleScreen titleScreen = new TitleScreen();
            if (bl) {
                this.client.setScreen(titleScreen);
            } else if (bl2) {
                this.client.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                this.client.setScreen(new MultiplayerScreen(titleScreen));
            }

        }));
    }

    void exitToMenu () {
        boolean inSingleplayer = this.client.isInSingleplayer();
        boolean inRealms = this.client.isConnectedToRealms();

        this.client.world.disconnect();

        if (inSingleplayer) {
            this.client.disconnect(new MessageScreen(TextUtils.translatable("menu.savingLevel")));
        } else {
            this.client.disconnect();
        }

        FoxClientTitleScreen title = new FoxClientTitleScreen(false);

        if (inSingleplayer) {
            this.client.setScreen(title);
        } else if (inRealms) {
            this.client.setScreen(new RealmsMainScreen(title));
        } else {
            this.client.setScreen(new MultiplayerScreen(title));
        }
    }
}
