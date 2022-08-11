package net.foxes4life.foxclient.screen.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.screen.settings.ui.CategoryButton;
import net.foxes4life.foxclient.screen.settings.ui.ToggleButton;
import net.foxes4life.foxclient.util.BackgroundUtils;
import net.foxes4life.foxclient.util.ConfigUtils;
import net.foxes4life.foxclient.util.TextUtils;
import net.foxes4life.konfig.data.KonfigCategory;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class FoxClientSettingsScreen extends Screen {
    private static final Identifier X_BUTTON = new Identifier("foxclient", "textures/ui/title/buttons/x.png");

    private static String currentCategoryId;
    private static KonfigCategory currentCategory;
    LinkedHashMap<String, KonfigCategory> categories;

    static int currentSelectedCat = 0;
    static double catSelectBgY = 0;
    static int catSelectBgYGoal = 0;

    static double entryHoverBgY = 0;
    static int entryHoverBgYGoal = 0;

    private static final int sidebarWidth = 64 + 16;

    private int amountOfDrawableChilds = 0; // set amount of buttons created in init() here (excluding the ones in the loop)
    private boolean initDone = false; // to prevent amountOfDrawableChilds from increasing after init is done

    public FoxClientSettingsScreen() {
        super(TextUtils.string("FoxClient"));
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void tick() {}

    protected void init() {
        assert this.client != null;
        this.client.keyboard.setRepeatEvents(true);

        categories = Main.konfig.getData();

        AtomicInteger cat = new AtomicInteger();

        categories.forEach((name, category) -> {
            cat.getAndIncrement();
            this.addDrawableChild(new CategoryButton(0,
                    cat.get() * 22,
                    sidebarWidth,
                    22,
                    ConfigUtils.translatableCategory(category),
                    false,
                    name,
                    (button) -> {
                        for (Element child : this.children()) {
                            if (child instanceof CategoryButton) {
                                ((CategoryButton) child).selected = false;
                            }
                        }

                        ((CategoryButton) button).selected = true;
                        addCategoryButtons(name, category);
                    }
            ));
        });

        if (currentCategory == null) {
            currentSelectedCat = 0;
            currentCategoryId = (String) categories.keySet().toArray()[0];
            currentCategory = categories.get(currentCategoryId);
//            System.out.println("new category: "+ currentCategoryId);
        }

        // close
        this.addDrawableChild(new TexturedButtonWidget(this.client.getWindow().getScaledWidth() - 24, 4, 20, 20, 0, 0, 20, X_BUTTON, 32, 64, (button) -> this.close(), TextUtils.translatable("foxclient.gui.button.close")));

        initDone = true;

        if (currentCategory != null) {
            for (Element child : this.children()) {
                if (child instanceof CategoryButton sidebarButton) {
                    sidebarButton.selected = currentCategoryId.equals(sidebarButton.categoryId);
                }
            }

            addCategoryButtons(currentCategoryId, currentCategory);
        }
    }

    private void addCategoryButtons(String name, KonfigCategory category) {
        if (currentCategory != null) {
            for (Object o : this.children().subList(amountOfDrawableChilds, this.children().size()).toArray()) {
                this.remove((Element) o);
            }
        }

        currentCategoryId = name;
        currentCategory = category;

        AtomicInteger settingsThing = new AtomicInteger(0);

        int bHeight = 22;
        currentCategory.catData.forEach((key, value) -> {
            settingsThing.getAndIncrement();

            if (Boolean.class.equals(value.value.getClass())) {
                //System.out.println("boolean");
                this.addDrawableChild(
                        new ToggleButton(sidebarWidth + 2,
                                settingsThing.get() * bHeight + 32,
                                width - sidebarWidth - 4,
                                bHeight,
                                ConfigUtils.translatableEntry(category, value), (Boolean) value.value, (b) -> {
                            //System.out.println("clicked toggle!");
                            Main.konfig.set(name, key, !(boolean) value.value);
                            try {
                                Main.konfig.save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }));
            } else if (String.class.equals(value.value.getClass())) {
                //System.out.println("string");
                this.addDrawableChild(new ButtonWidget(0, 0, 0, 0, Text.of(""), (b) -> {
                }));
            } else {
                System.out.println("UNKNOWN: " + value.value.getClass());
                // add dummy to avoid crashes
                this.addDrawableChild(new ButtonWidget(0, 0, 0, 0, Text.of("a"), (b) -> {
                }));
            }
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

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;

        BackgroundUtils.drawRandomBackground(matrices, this.width, this.height);
        fill(matrices, 0, 0, this.width, this.height, 0x44000000);
        fill(matrices, 0, 0, sidebarWidth, this.height, 0x44000000);

        int i = 0;
        for (Map.Entry<String, KonfigCategory> entry : categories.entrySet()) {
            if (entry.getKey().equals(currentCategoryId)) {
                catSelectBgYGoal = (i * 22) + 22;
                catSelectBgY = MathHelper.lerp(delta * 1.2, catSelectBgY, catSelectBgYGoal);
                fill(matrices, 0, (int) catSelectBgY, sidebarWidth, (int) catSelectBgY + 22, 0x44ffffff);
            }

            i++;
        }

        for (Element child : children()) {
            if (child instanceof ToggleButton) {
                if (((ToggleButton) child).isHovered()) {
                    entryHoverBgYGoal = ((ToggleButton) child).y;
                    entryHoverBgY = MathHelper.lerp(delta * 1.2, entryHoverBgY, entryHoverBgYGoal);
                    fill(matrices, sidebarWidth + 2, (int) entryHoverBgY, this.client.getWindow().getScaledWidth() - 2, (int) entryHoverBgY + 22, 0x44ffffff);
                }
            }
        }

        drawCenteredTextWithShadow(matrices, this.textRenderer, OrderedText.styledForwardsVisitedString("FoxClient Settings", Style.EMPTY), ((this.client.getWindow().getScaledWidth() - sidebarWidth) / 2) + sidebarWidth, 20, 0xffffff);

        assert this.client != null;

        // draw buttons
        super.render(matrices, mouseX, mouseY, delta);
    }
}