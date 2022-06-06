package net.foxes4life.foxclient.screen.settings.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.config.Category;
import net.foxes4life.foxclient.config.ConfigHelper;
import net.foxes4life.foxclient.gui.settings.SettingsCategorySidebarButton;
import net.foxes4life.foxclient.gui.settings.SettingsToggleButton;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class SettingsMenuScreen extends Screen {
    private static final Identifier X_BUTTON = new Identifier("foxclient", "textures/ui/buttons/x.png");

    private static String currentCategoryId;
    private static Category currentCategory;

    private static final int sidebarWidth = 64+16;
    private int amountOfDrawableChilds = 0; // set amount of buttons created in init() here (excluding the ones in the loop)
    private boolean initDone = false; // to prevent amountOfDrawableChilds from increasing after init is done

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

        LinkedHashMap<String, Category> categories = Main.config.things;

        AtomicInteger cat = new AtomicInteger();

        categories.forEach((name, category) -> {
            cat.getAndIncrement();
            this.addDrawableChild(new SettingsCategorySidebarButton(0,
                    cat.get() *22+6,
                    sidebarWidth,
                    22,
                    Text.of(category.name),
                    false,
                    name,
                    (button) -> {
                        for (Element child : this.children()) {
                            if(child instanceof SettingsCategorySidebarButton) {
                                ((SettingsCategorySidebarButton) child).selected = false;
                            }
                        }

                        ((SettingsCategorySidebarButton) button).selected = true;
                addCategoryButtons(name, category);
            }
            ));
        });

        if(currentCategory == null) {
            currentCategoryId = (String) categories.keySet().toArray()[0];
            currentCategory = categories.get(currentCategoryId);
//            System.out.println("new category: "+ currentCategoryId);
        }

        // close
        this.addDrawableChild(new TexturedButtonWidget(this.client.getWindow().getScaledWidth()-24, 4, 20, 20, 0, 0, 20, X_BUTTON, 32, 64, (button) -> this.close(), new TranslatableText("foxclient.gui.button.close")));

        initDone = true;

        if(currentCategory != null) {
            for (Element child : this.children()) {
                if(child instanceof SettingsCategorySidebarButton sidebarButton) {
                    sidebarButton.selected = currentCategoryId.equals(sidebarButton.categoryId);
                }
            }

            addCategoryButtons(currentCategoryId, currentCategory);
        }
    }

    private void addCategoryButtons(String name, Category category) {
        if(currentCategory != null) {
            for (Object o : this.children().subList(amountOfDrawableChilds, this.children().size()).toArray()) {
                this.remove((Element) o);
            }
        }

        currentCategoryId = name;
        currentCategory = category;

        AtomicInteger settingsThing = new AtomicInteger(0);

        int bHeight = 22;
        currentCategory.settings.forEach((key, value) -> {
            settingsThing.getAndIncrement();

            if (Boolean.class.equals(value.getValue().getClass())) {
//                System.out.println("boolean");
                this.addDrawableChild(
                        new SettingsToggleButton(sidebarWidth+2,
                                settingsThing.get()*bHeight+32,
                                width - sidebarWidth - 4,
                                bHeight,
                                Text.of(value.name), (Boolean) value.getValue(), (b) -> {
//                            System.out.println("clicked toggle!");
                            Main.config_instance.set(name, key, value.setValue(!(boolean)value.getValue()));
                            ConfigHelper.onUpdate(name, key, value.getValue());
                        }));
            } else if (String.class.equals(value.getValue().getClass())) {
//                System.out.println("string");
                this.addDrawableChild(new ButtonWidget(0,0,0,0, Text.of(""), (b) -> {}));
            } else {
                System.out.println("UNKNOWN: " + value.getValue().getClass());
                // add dummy to avoid crashes
                this.addDrawableChild(new ButtonWidget(0,0,0,0, Text.of("a"), (b) -> {}));
            }
        });
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        if(!initDone) amountOfDrawableChilds++;
        return super.addDrawableChild(drawableElement);
    }

    @Override
    public void close() {
        //System.out.println("aaa adsdasd"); // <- rooot has a stroke <- yes uwu
        super.close();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        fill(matrices, 0, 0, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), 0xFF282828);
        fill(matrices, 0, 0, sidebarWidth, this.client.getWindow().getScaledHeight(), 0xFF383838);

        drawStringWithShadow(matrices, this.textRenderer, "FoxClient Settings", 96, 10, 0xffffff);

        assert this.client != null;

        // draw buttons
        super.render(matrices, mouseX, mouseY, delta);
    }
}