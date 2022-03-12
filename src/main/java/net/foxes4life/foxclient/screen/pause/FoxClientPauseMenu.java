package net.foxes4life.foxclient.screen.pause;

import net.foxes4life.foxclient.screen.mainmenu.FoxClientTitleScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class  FoxClientPauseMenu extends Screen {
    public FoxClientPauseMenu () {
        super(new LiteralText("Pause Menu"));
    }

    public void render (MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void init () {
        Text text = this.client.isInSingleplayer() ? new TranslatableText("menu.returnToMenu") : new TranslatableText("menu.disconnect");
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, text, (button) -> {
            exitToMenu();
        }));
    }

    void exitToMenu () {
        boolean inSingleplayer = this.client.isInSingleplayer();
        boolean inRealms = this.client.isConnectedToRealms();

        this.client.world.disconnect();

        if (inSingleplayer) {
            this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
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
