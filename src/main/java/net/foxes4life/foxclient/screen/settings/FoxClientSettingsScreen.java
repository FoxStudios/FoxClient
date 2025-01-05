package net.foxes4life.foxclient.screen.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.MainClient;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.screen.settings.ui.CategoryButton;
import net.foxes4life.foxclient.screen.settings.ui.ToggleButton;
import net.foxes4life.foxclient.util.BackgroundUtils;
import net.foxes4life.foxclient.util.ConfigUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.foxes4life.foxclient.util.transforms.Easing;
import net.foxes4life.foxclient.util.transforms.Transform;
import net.foxes4life.konfig.bindables.Bindable;
import net.foxes4life.konfig.bindables.BindableNumber;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class FoxClientSettingsScreen extends Screen {
    private static final Identifier X_BUTTON = Identifier.of("foxclient", "textures/ui/title/buttons/x.png");

    private static String currentCategory;
    private static LinkedHashMap<String, List<FoxClientSetting>> categories;

    private static BindableNumber<Integer> currentSelectedCat;
    private static Bindable<ToggleButton> currentHovered;
    private static Bindable<Boolean> isHovering;
    private static final Transform sidebarTransform = new Transform(200, Easing.OutQuint, 0, 22);
    private static final Transform hoverMoveTransform = new Transform(200, Easing.OutQuint, 0, 1);
    private static final Transform hoverFadeTransform = new Transform(100, Easing.Linear, 0, 0);

    private static final int sidebarWidth = 64 + 48;

    private int amountOfDrawableChildren = 0; // set amount of buttons created in init() here (excluding the ones in the loop)
    private boolean initDone = false; // to prevent amountOfDrawableChildren from increasing after init is done

    private final boolean showBackground;

    public FoxClientSettingsScreen(boolean showBackground) {
        super(TextUtils.string("FoxClient"));
        this.showBackground = showBackground;

        if (currentSelectedCat == null) {
            currentSelectedCat = new BindableNumber<>(0);
            currentSelectedCat.valueChanged.add((newValue) -> {
                sidebarTransform.startValue = sidebarTransform.getValue();
                sidebarTransform.endValue = (newValue * 22) + 22;
                sidebarTransform.reset();
            });

            currentHovered = new Bindable<>(null);
            currentHovered.valueChanged.add((newValue) -> {
                hoverMoveTransform.startValue = hoverMoveTransform.getValue();
                hoverMoveTransform.endValue = newValue == null ? 0 : newValue.getY();
                hoverMoveTransform.reset();
            });

            isHovering = new Bindable<>(false);
            isHovering.valueChanged.add((newValue) -> {
                hoverFadeTransform.startValue = hoverFadeTransform.getValue();
                hoverFadeTransform.endValue = newValue ? 1 : 0;
                hoverFadeTransform.reset();
            });

            sidebarTransform.removeOnFinish = false;
            hoverMoveTransform.removeOnFinish = false;
            hoverFadeTransform.removeOnFinish = false;
            MainClient.transformManager.addTransform(sidebarTransform);
            MainClient.transformManager.addTransform(hoverMoveTransform);
            MainClient.transformManager.addTransform(hoverFadeTransform);
        }

        categories = new LinkedHashMap<>();
        categories.put("client", List.of(FoxClientSetting.HudEnabled, FoxClientSetting.ArmorHudEnabled, FoxClientSetting.BlockHudEnabled));
        categories.put("menus", List.of(FoxClientSetting.CustomMainMenu, FoxClientSetting.CustomPauseMenu));
        categories.put("misc", List.of(FoxClientSetting.DiscordEnabled, FoxClientSetting.DiscordShowIP, FoxClientSetting.DiscordShowPlayer, FoxClientSetting.SmoothZoom));
        categories.put("eastereggs", List.of(FoxClientSetting.UwUfy));
        categories.put("ingame-hud", List.of(FoxClientSetting.HudBackground, FoxClientSetting.HudLogo, FoxClientSetting.HudVersion, FoxClientSetting.HudCoordinates, FoxClientSetting.HudCoordinatesColor, FoxClientSetting.HudFPS, FoxClientSetting.HudPing, FoxClientSetting.HudTps, FoxClientSetting.HudServerIP, FoxClientSetting.HudBiome));
        categories.put("armor-hud", List.of(FoxClientSetting.ArmorHudDisplayPercentage));
        categories.put("block-hud", List.of(FoxClientSetting.BlockHudAnimations));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
            currentSelectedCat.setValue(0);
            currentCategory = categories.keySet().toArray(new String[0])[0];
        }

        ButtonTextures closeButtonTextures = new ButtonTextures(X_BUTTON, X_BUTTON);

        // close
        this.addDrawableChild(new TexturedButtonWidget(this.client.getWindow().getScaledWidth() - 24, 4, 20, 20, closeButtonTextures, (button) -> this.close(), TextUtils.translatable("foxclient.gui.button.close")));

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
            for (Object o : this.children().subList(amountOfDrawableChildren, this.children().size()).toArray()) {
                this.remove((Element) o);
            }
        }

        currentCategory = name;
        currentSelectedCat.setValue(categories.keySet().stream().toList().indexOf(name));

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
        if (!initDone) amountOfDrawableChildren++;
        return super.addDrawableChild(drawableElement);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        assert this.client != null;

        if (showBackground)
            BackgroundUtils.drawRandomBackground(context, this.width, this.height);

        context.fill(0, 0, this.width, this.height, 0x44000000);
        context.fill(0, 0, sidebarWidth, this.height, 0x44000000);

        boolean hoveringAny = false;

        for (Element child : children()) {
            if (child instanceof ToggleButton button) {
                if ((button).isHovered()) {
                    currentHovered.setValue(button);
                    hoveringAny = true;
                    break;
                }
            }
        }

        if (isHovering.getValue() != hoveringAny)
            isHovering.setValue(hoveringAny);

        int sidebarY = (int)sidebarTransform.getValue();
        context.fill(0, sidebarY, sidebarWidth, sidebarY + 22, 0x44ffffff);

        int hoverY = (int)hoverMoveTransform.getValue();
        float hoverAlpha = (float)hoverFadeTransform.getValue();
        Color hoverColor = new Color(1f, 1f, 1f, .2f * hoverAlpha);
        context.fill( sidebarWidth + 2, hoverY, this.client.getWindow().getScaledWidth() - 2, hoverY + 22, hoverColor.getRGB());

        context.drawCenteredTextWithShadow(this.textRenderer, OrderedText.styledForwardsVisitedString("FoxClient Settings", Style.EMPTY), ((this.client.getWindow().getScaledWidth() - sidebarWidth) / 2) + sidebarWidth, 20, 0xffffff);

        assert this.client != null;

        // draw buttons
        super.render(context, mouseX, mouseY, delta);
    }
}
