package net.foxes4life.foxclient.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.config.Category;
import net.foxes4life.foxclient.config.ConfigData;
import net.foxes4life.foxclient.gui.FoxClientButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class SettingsMenuScreen extends Screen {
    private static final Identifier BACKGROUND = new Identifier("foxclient", "textures/ui/title/bg/0.png");
    private static final Identifier X_BUTTON = new Identifier("foxclient", "textures/ui/buttons/x.png");

    public SettingsMenuScreen() {
        super(new LiteralText("FoxClient"));
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

        try {
            Set<String> categories = Main.config.things.keySet();

            for (String category : categories) {
                Category c = Main.config.things.get(category);
                String name = c.name;
                System.out.println(name);
                for (String s : c.settings.keySet()) {
                    System.out.println(name + " -> " + s + ": " + c.settings.get(s));
                }
            }

            Main.config_instance.set("misc", "discord-rpc", true);

            System.out.println(Main.config_instance.getBoolean("misc", "discord-rpc"));
        } catch (Exception nuuu) {
            nuuu.printStackTrace();
        }

        // VANILLA BUTTONS
        this.addDrawableChild(
                new FoxClientButton(this.width / 2 - 100,
                        y,
                        200,
                        20,
                        new TranslatableText("idk"),
                (button) -> {
                    assert this.client != null;
                    this.client.setScreen(new SelectWorldScreen(this));
                })
        );

        // close
        this.addDrawableChild(new TexturedButtonWidget(this.client.getWindow().getScaledWidth()-24, 4, 20, 20, 0, 0, 20, X_BUTTON, 32, 64, (button) -> this.close(), new TranslatableText("foxclient.gui.button.close")));
    }

    @Override
    public void close() {
        System.out.println("aaa adsdasd"); // <- rooot has a stroke <- yes uwu
        super.close();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), 0xFF282828);
        fill(matrices, 0, 0, 64, this.client.getWindow().getScaledHeight(), 0xFF383838);

        drawStringWithShadow(matrices, this.textRenderer, "FoxClient Settings", 64 + 20, 20, 0xffffff);

        //drawStringWithShadow(matrices, this.textRenderer, "FoxClient "+ Main.VERSION, 4, this.height - 20, 16777215);

        assert this.client != null;

        // draw buttons
        super.render(matrices, mouseX, mouseY, delta);
    }
}