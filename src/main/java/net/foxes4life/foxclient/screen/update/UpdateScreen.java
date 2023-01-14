package net.foxes4life.foxclient.screen.update;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.screen.title.TitleScreen;
import net.foxes4life.foxclient.ui.button.FoxClientButton;
import net.foxes4life.foxclient.util.BackgroundUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.foxes4life.foxclient.util.update.UpdateChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class UpdateScreen extends Screen {
    public UpdateScreen() {
        super(Text.of("FoxClient Update"));
    }

    protected void init() {
        BackgroundUtils.selectBackground();

        this.addDrawableChild(new FoxClientButton(this.width / 2 - 100, this.height / 2, 200, 20, TextUtils.string("Update"), (button) -> Util.getOperatingSystem().open(UpdateChecker.getReleaseUrl())));
        this.addDrawableChild(new FoxClientButton(this.width / 2 - 100, this.height / 2 + 24, 200, 20, TextUtils.string("Ignore"), (button) -> close()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        BackgroundUtils.drawRandomBackground(matrices, this.width, this.height);

        drawCenteredText(matrices, this.textRenderer, "A new version of FoxClient is available!", this.width / 2, this.height / 2 - 20, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void close() {
        UpdateChecker.dismissed = true;
        if (Main.config.get(FoxClientSetting.CustomMainMenu, Boolean.class)) {
            MinecraftClient.getInstance().setScreen(new TitleScreen(false));
        } else {
            MinecraftClient.getInstance().setScreen(new net.minecraft.client.gui.screen.TitleScreen());
        }
    }
}
