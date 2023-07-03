package net.foxes4life.foxclient.screen.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.screen.settings.ui.CategoryButton;
import net.foxes4life.foxclient.screen.settings.ui.ToggleButton;
import net.foxes4life.foxclient.util.BackgroundUtils;
import net.foxes4life.foxclient.util.ConfigUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class FoxClientSettingsScreen extends Screen {
    private static final Identifier X_BUTTON = new Identifier("foxclient", "textures/ui/title/buttons/x.png");

    private static String currentCategory;
    private static LinkedHashMap<String, List<FoxClientSetting>> categories;

    static int currentSelectedCat = 0;
    static double catSelectBgY = 22;
    static int catSelectBgYGoal = 0;

    static double entryHoverBgY = 0;
    static int entryHoverBgYGoal = 0;

    private static final int sidebarWidth = 64 + 16;

    private int amountOfDrawableChilds = 0; // set amount of buttons created in init() here (excluding the ones in the loop)
    private boolean initDone = false; // to prevent amountOfDrawableChilds from increasing after init is done

    public FoxClientSettingsScreen() {
        super(TextUtils.string("FoxClient"));

        categories = new LinkedHashMap<>();
        categories.put("client", List.of(FoxClientSetting.HudEnabled, FoxClientSetting.ArmorHudEnabled, FoxClientSetting.BlockHudEnabled));
        categories.put("menus", List.of(FoxClientSetting.CustomMainMenu, FoxClientSetting.CustomPauseMenu));
        categories.put("misc", List.of(FoxClientSetting.DiscordEnabled, FoxClientSetting.DiscordShowIP, FoxClientSetting.DiscordShowPlayer, FoxClientSetting.SmoothZoom));
        categories.put("eastereggs", List.of(FoxClientSetting.UwUfy));
        categories.put("ingame-hud", List.of(FoxClientSetting.HudBackground, FoxClientSetting.HudLogo, FoxClientSetting.HudVersion, FoxClientSetting.HudCoordinates, FoxClientSetting.HudCoordinatesColor, FoxClientSetting.HudFPS, FoxClientSetting.HudPing, FoxClientSetting.HudTps, FoxClientSetting.HudServerIP, FoxClientSetting.HudBiome));
        categories.put("armor-hud", List.of(FoxClientSetting.ArmorHudDisplayPercentage));
        categories.put("block-hud", List.of(FoxClientSetting.BlockHudAnimations));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    protected void init() {
        assert this.client != null;
//        this.client.keyboard.setRepeatEvents(true);

        AtomicInteger cat = new AtomicInteger();

        categories.forEach((name, settings) -> {
            cat.getAndIncrement();
            this.addDrawableChild(new CategoryButton(0, cat.get() * 22, sidebarWidth, 22, ConfigUtils.translatableCategory(name), false, name, (button) -> {
                for (Element child : this.children()) {
                    if (child instanceof CategoryButton) {
                        ((CategoryButton) child).selected = false;
                    }
                }

                ((CategoryButton) button).selected = true;
                addCategoryButtons(name, settings);
            }));
        });

        if (currentCategory == null) {
            currentSelectedCat = 0;
            currentCategory = categories.keySet().toArray(new String[0])[0];
        }

        // close
        this.addDrawableChild(new TexturedButtonWidget(this.client.getWindow().getScaledWidth() - 24, 4, 20, 20, 0, 0, 20, X_BUTTON, 32, 64, (button) -> this.close(), TextUtils.translatable("foxclient.gui.button.close")));

        initDone = true;

        if (currentCategory != null) {
            for (Element child : this.children()) {
                if (child instanceof CategoryButton sidebarButton) {
                    sidebarButton.selected = currentCategory.equals(sidebarButton.categoryId);
                }
            }

            addCategoryButtons(currentCategory, categories.get(currentCategory));
        }
    }

    private void addCategoryButtons(String name, List<FoxClientSetting> settings) {
        if (currentCategory != null) {
            for (Object o : this.children().subList(amountOfDrawableChilds, this.children().size()).toArray()) {
                this.remove((Element) o);
            }
        }

        currentCategory = name;

        AtomicInteger settingsThing = new AtomicInteger(0);

        int bHeight = 22;
        settings.forEach((setting) -> {
            settingsThing.getAndIncrement();
            Boolean value = Main.config.get(setting, Boolean.class);

            this.addDrawableChild(new ToggleButton(sidebarWidth + 2, settingsThing.get() * bHeight + 32, width - sidebarWidth - 4, bHeight, ConfigUtils.getTranslation(setting), value, (b) -> {
                Main.config.set(setting, !value);
                ConfigUtils.onOptionChanged(setting, !value);
            }));

            /*if (value.isBoolean()) {*/
            //System.out.println("boolean");

            /*} else if (value.isString()) {
                //System.out.println("string");
                this.addDrawableChild(ButtonWidget.builder(Text.of(""), (b) -> {}).build());
            } else {
                System.out.println("UNKNOWN: " + value.getValue().getClass());
                // add dummy to avoid crashes
                this.addDrawableChild(ButtonWidget.builder(Text.of("a"), (b) -> {}).build());
            }*/
        });
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        if (!initDone) amountOfDrawableChilds++;
        return super.addDrawableChild(drawableElement);
    }

    @Override
    public void close() {
        //System.out.println("aaa adsdasd"); // <- rooot has a stroke <- yes uwu
        super.close();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        assert this.client != null;

        BackgroundUtils.drawRandomBackground(context, this.width, this.height);
        context.fill(0, 0, this.width, this.height, 0x44000000);
        context.fill(0, 0, sidebarWidth, this.height, 0x44000000);

        int i = 0;
        for (Map.Entry<String, List<FoxClientSetting>> entry : categories.entrySet()) {
            if (entry.getKey().equals(currentCategory)) {
                catSelectBgYGoal = (i * 22) + 22;
                catSelectBgY = MathHelper.lerp(delta * 1.2, catSelectBgY, catSelectBgYGoal);
                context.fill(0, (int) catSelectBgY, sidebarWidth, (int) catSelectBgY + 22, 0x44ffffff);
            }

            i++;
        }

        for (Element child : children()) {
            if (child instanceof ToggleButton) {
                if (((ToggleButton) child).isHovered()) {
                    entryHoverBgYGoal = ((ToggleButton) child).getY();
                    entryHoverBgY = MathHelper.lerp(delta * 1.2, entryHoverBgY, entryHoverBgYGoal);
                    context.fill( sidebarWidth + 2, (int) entryHoverBgY, this.client.getWindow().getScaledWidth() - 2, (int) entryHoverBgY + 22, 0x44ffffff);
                }
            }
        }

        context.drawCenteredTextWithShadow(this.textRenderer, OrderedText.styledForwardsVisitedString("FoxClient Settings", Style.EMPTY), ((this.client.getWindow().getScaledWidth() - sidebarWidth) / 2) + sidebarWidth, 20, 0xffffff);

        assert this.client != null;

        // draw buttons
        super.render(context, mouseX, mouseY, delta);
    }
}