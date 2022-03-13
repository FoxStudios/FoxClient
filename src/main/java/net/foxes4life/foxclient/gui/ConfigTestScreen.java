package net.foxes4life.foxclient.gui;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.config.ConfigData;
import net.foxes4life.foxclient.discord.DiscordInstance;
import net.foxes4life.foxclient.egg.EggManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigTestScreen extends Screen {
    private static boolean dcButtonEnabled = true;
    protected final Screen parent;
    private List<OrderedText> tooltipText;

    private ButtonWidget closeButton;

    private ConfigData config;

    public ConfigTestScreen(Screen parent) {
        super(new TranslatableText("DEBUG CONFIG"));
        this.parent = parent;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void tick() {

    }

    protected void init() {
        this.config = Main.config;

        assert this.client != null;
        this.client.keyboard.setRepeatEvents(true);

        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 80, 150, 20, Text.of(String.valueOf(Main.config_instance.getBoolean("trueorfalse"))), (buttonWidget) -> {
            if(Main.config_instance.getBoolean("trueorfalse")) {
                Main.config_instance.set("trueorfalse", false);
            } else {
                Main.config_instance.set("trueorfalse", true);
            }
            buttonWidget.setMessage(Text.of(String.valueOf(Main.config_instance.getBoolean("trueorfalse"))));
        }));

        ButtonWidget dc_button = new ButtonWidget(
                this.width / 2 + 4,
                this.height - 60, 150, 20,
                Text.of("Discord RPC: " + Main.config_instance.getBoolean("discord-rpc")),
                (buttonWidget) -> {
            if(!dcButtonEnabled) {
                buttonWidget.active = false;
            } else {
                buttonWidget.active = true;
            }

            if(Main.config_instance.getBoolean("discord-rpc")) {
                Main.config_instance.set("discord-rpc", false);
                DiscordInstance.get().stfu();
            } else {
                Main.config_instance.set("discord-rpc", true);
                DiscordInstance.get().init();
            }

            dcButtonEnabled = false;
            buttonWidget.active = false;

            buttonWidget.setMessage(Text.of("Discord RPC: " + Main.config_instance.getBoolean("discord-rpc")));

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }

                buttonWidget.active = true;
                dcButtonEnabled = true;
            }).start();
        });

        dc_button.active = dcButtonEnabled;
        this.addDrawableChild(dc_button);

        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 40, 150, 20, Text.of(String.valueOf(EggManager.isOwOEnabled())), (buttonWidget) -> {
            if(EggManager.isOwOEnabled()) {
                EggManager.setEnabled("owo", false);
            } else {
                EggManager.setEnabled("owo", true);
            }
            buttonWidget.setMessage(Text.of(String.valueOf(EggManager.isOwOEnabled())));
        }));

        this.closeButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 76, this.height - 80, 72, 20, ScreenTexts.DONE, (buttonWidget) -> {
            this.client.setScreen(this.parent);
        }));

    }

    public void onClose() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, -1873784752); // background

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