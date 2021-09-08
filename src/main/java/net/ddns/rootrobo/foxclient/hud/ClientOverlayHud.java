package net.ddns.rootrobo.foxclient.hud;

import com.google.common.collect.Lists;
import net.ddns.rootrobo.foxclient.Main;
import net.ddns.rootrobo.foxclient.client.Client;
import net.ddns.rootrobo.foxclient.networking.ServerTickStuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientOverlayHud extends DrawableHelper {
    private final MinecraftClient client;
    private final TextRenderer fontRenderer;
    public static List<Color> colors = new ArrayList<>();
    private static int rgb = 0;

    // data
    private static int PING = 0;
    private static int FPS = 0;
    private static int CPS_L = 0;
    private static int CPS_R = 0;

    private static float TPS = 0;

    public ClientOverlayHud(MinecraftClient client) {
        this.client = client;
        this.fontRenderer = client.textRenderer;
    }

    public void render(MatrixStack matrices) {
        if (!this.client.options.debugEnabled) {
            //noinspection deprecation
            //RenderSystem.;
            this.renderLeftText(matrices);
            //noinspection deprecation
            //RenderSystem.popMatrix();
        }
    }

    public static void tick() {
        // rgb
        if(rgb >= colors.size()-1) {
            rgb = 0;
        } else {
            rgb = rgb+1;
        }

        PING = Client.getPing();
        FPS = Client.getFPS();
        CPS_L = Client.getLeftCPS();
        CPS_R = Client.getRightCPS();

        TPS = ServerTickStuff.calculateServerTPS();
    }

    public void renderLeftText(MatrixStack matrices) {
        List<Text> list = Lists.newArrayList();
        assert this.client.player != null;

        list.add(new LiteralText("FoxClient "+ Main.VERSION));
        list.add(new LiteralText(""));

        if(client.getCameraEntity() != null) {
            list.add(new LiteralText(String.format("[XYZ] %s %s %s", this.client.player.getBlockPos().getX(), this.client.player.getBlockPos().getY(), this.client.player.getBlockPos().getZ())));
        }

        list.add(new LiteralText(String.format("[FPS] %s", FPS)));

        list.add(new LiteralText(String.format("[CPS] %s %s", CPS_L, CPS_R)));

        list.add(new LiteralText(String.format("[TPS] %s", TPS)));

        list.add(new LiteralText(String.format("[Ping] %s", PING)));

        list.add(new LiteralText(this.client.player.getInventory().getMainHandStack().getTranslationKey()));

        if(this.client.player.getInventory().getMainHandStack().getNbt() != null) {
            if(this.client.player.getInventory().getMainHandStack().getNbt().get("Effects") != null) {
                NbtList effectTag = this.client.player.getInventory().getMainHandStack().getNbt().getList("Effects", 10);
                if(!effectTag.isEmpty()) {
                    list.add(new TranslatableText("foxclient.gui.effectlist.title"));
                    for (NbtElement tag : effectTag) {
                        if(((NbtCompound) tag).get("EffectId") == null) continue;
                        StatusEffect effect = StatusEffect.byRawId(((NbtCompound) tag).getByte("EffectId"));
                        if(effect == null) continue;
                        int duration = ((NbtCompound) tag).getInt("EffectDuration");
                        if(duration < 20) {
                            if(duration == 1) {
                                list.add(new TranslatableText("foxclient.gui.effectlist.effect.tick", new TranslatableText(effect.getTranslationKey()), duration));
                            } else {
                                list.add(new TranslatableText("foxclient.gui.effectlist.effect.ticks", new TranslatableText(effect.getTranslationKey()), duration));
                            }
                        } else {
                            if(duration / 20 == 1) {
                                list.add(new TranslatableText("foxclient.gui.effectlist.effect.second", new TranslatableText(effect.getTranslationKey()), duration / 20));
                            } else {
                                list.add(new TranslatableText("foxclient.gui.effectlist.effect.seconds", new TranslatableText(effect.getTranslationKey()), duration / 20));
                            }
                        }
                    }
                }
            }
        }

        float x = 2F;

        for(int i = 0; i < list.size(); ++i) {
            Text text = list.get(i);
            if (!(text == null || text == Text.EMPTY)) {
                int j = 9;
                //int k = this.fontRenderer.getWidth(text.getString());
                int y = 2 + j * i;
                //fill(matrices, 1, y - 1, 2 + k + 1, y + j - 1, -1873784752); // background

                if(text.getString().equals("FoxClient "+Main.VERSION)) {
                    this.fontRenderer.draw(matrices, text, x, (float)y, colors.get(rgb).getRGB());
                } else {
                    this.fontRenderer.draw(matrices, text, x, (float)y, 16777215);
                }

                //drawCenteredText(matrices, fontRenderer, text, 2.0F, (float)m);
            }
        }
    }
}
