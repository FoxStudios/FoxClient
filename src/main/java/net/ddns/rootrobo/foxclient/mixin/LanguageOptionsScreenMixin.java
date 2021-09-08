package net.ddns.rootrobo.foxclient.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {
    //private MappingConfiguration mappingConfiguration = new MappingConfiguration();

    //@Shadow
    //private OptionButtonWidget forceUnicodeButton;

    @Mutable
    @Shadow @Final
    LanguageManager languageManager;

    //@Shadow private ButtonWidget doneButton;

    public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }


    @Mixin(targets = "net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget")
    @Environment(EnvType.CLIENT)
    private static class ILanguageSelectionListWidget {
        @Mixin(targets = "net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry")
        private static class EntryThingy {
            @Final
            @Shadow
            private LanguageDefinition languageDefinition;

            @Inject(at = @At("HEAD"), method = "onPressed")
            private void onPressed(CallbackInfo ci) {
                System.out.println("GOT PRESSED! "+this.languageDefinition.getCode());
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        System.out.println("LANG SCREEN INIT");
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 20, 150, 20, ScreenTexts.PROCEED, (button) -> {
            System.out.println("LANG SCREEN -> FAST LANG LOAD");
            System.out.println(I18n.translate("gui.proceed"));
            LanguageDefinition ldef = null;
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
            try {
                for (String namespace : resolver.getNamespaces()) {
                    System.out.println("NS: "+namespace);
                }

                System.out.println("CURRENT NAMESPACE: "+resolver.getCurrentRuntimeNamespace());
                String lsln = resolver.mapFieldName("intermediary", "net.minecraft.class_426", "field_2486", "");

                String gsn = resolver.mapMethodName("intermediary", "net.minecraft.class_350", "method_25334", "");

                if(FabricLauncherBase.getLauncher().isDevelopment()) {
                    gsn = "getSelected";
                    lsln = "languageSelectionList";
                }

                System.out.println("LIST FIELD NAME: "+lsln);
                System.out.println("GET SELECTED METHOD NAME: "+gsn);
                Method m = Objects.requireNonNull(this.children().get(0).getClass()).getSuperclass().getMethod(EntryListWidget.class.getDeclaredMethod(gsn).getName());

                m.setAccessible(true);
                Object le = m.invoke(this.getClass().getDeclaredField(lsln).get(this));
                Field ye = le.getClass().getDeclaredFields()[0];
                ye.setAccessible(true);
                System.out.println("GET LANGUAGE FROM LIST");
                LanguageDefinition def = (LanguageDefinition) ye.get(le);
                System.out.println("LANGUAGE FROM LIST: "+def.getCode());
                ldef = def;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            if(ldef == null) {
                System.out.println("LDEF NULL; NOT PROCEEDING!");
            }
            //Language languageEntry = (LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelected();
            if (ldef != null && !ldef.getCode().equals(this.languageManager.getLanguage().getCode())) {
                System.out.println("LDEF CODE: "+ldef.getCode());

                System.out.println("FOCUSED: "+ Objects.requireNonNull(this.getFocused()).toString());

                //this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions));

                this.languageManager.setLanguage(ldef);
                this.gameOptions.language = ldef.getCode();
                this.gameOptions.write();

                List<LanguageDefinition> list = Lists.newArrayList(ldef);
                if (this.languageManager.getLanguage() != ldef) {
                    list.add(this.languageManager.getLanguage());
                }

                ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

                String string = String.format("lang/%s.json", ldef.getCode());
                Iterator<String> var7 = manager.getAllNamespaces().iterator();

                Map<String, String> map = Maps.newHashMap();

                ArrayList<Resource> ye = new ArrayList<>();
                while(var7.hasNext()) {
                    String string2 = var7.next();
                    Identifier identifier = new Identifier(string2, string);
                    try {
                        ye.add(manager.getResource(identifier));
                    } catch (IOException ioException) {
                    }
                }
                System.out.println("invoke load lol");
                load(ye, map);
                System.out.println("create new translation storage");
                TranslationStorage translationStorage = null;

                try {
                    Constructor<TranslationStorage> constructor = TranslationStorage.class.getDeclaredConstructor(Map.class, boolean.class);
                    constructor.setAccessible(true);
                    translationStorage = constructor.newInstance(map, ldef.isRightToLeft());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                //TranslationStorage translationStorage = TranslationStorage.load(MinecraftClient.getInstance().getResourceManager(), list);

                Language.setInstance(translationStorage);
                I18nInvoker.setLanguage(Language.getInstance());
                TranslationStorage.setInstance(Language.getInstance());
                System.out.println(I18n.translate("gui.proceed"));
            }

            assert this.client != null;
            this.client.setScreen(this.parent);
        }));
    }

    private static void load(List<Resource> resources, Map<String, String> translationMap) {
        Iterator var2 = resources.iterator();

        while(var2.hasNext()) {
            Resource resource = (Resource)var2.next();

            try {
                InputStream inputStream = resource.getInputStream();
                Throwable var5 = null;

                try {
                    Language.load(inputStream, translationMap::put);
                } catch (Throwable var15) {
                    var5 = var15;
                    throw var15;
                } finally {
                    if (inputStream != null) {
                        if (var5 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var14) {
                                var5.addSuppressed(var14);
                            }
                        } else {
                            inputStream.close();
                        }
                    }

                }
            } catch (IOException var17) {
                System.err.println("Failed to load translations from {}");
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        for (int i = 0; i < this.children().size(); i++) {
            if(this.children().get(i) instanceof ButtonWidget buttonWidget) {
                System.out.println("(debug) class: "+this.children().get(i).getClass());

                if(buttonWidget.getMessage().equals(ScreenTexts.DONE)) {
                    buttonWidget.setMessage(Text.of("Crashma game LOL"));
                }
            }
        }
    }
}
