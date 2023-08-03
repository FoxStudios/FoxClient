package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.configuration.FoxClientSetting;
import net.foxes4life.foxclient.gui.ArmorHud;
import net.foxes4life.foxclient.gui.BlockHud;
import net.foxes4life.foxclient.gui.FoxClientHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, float pFloat3, PlayerEntity player, ItemStack stack, int seed);

    private static FoxClientHUD foxClientHUD = null;
    private static ArmorHud armorHud = null;
    private static BlockHud blockHud = null;

    @Inject(at = @At("HEAD"), method = "render")
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (Main.config.get(FoxClientSetting.HudEnabled, Boolean.class)) {
            if (foxClientHUD == null) {
                foxClientHUD = new FoxClientHUD(client);
            }
            foxClientHUD.render(context);
        }

        if (Main.config.get(FoxClientSetting.ArmorHudEnabled, Boolean.class)) {
            if (armorHud == null) armorHud = new ArmorHud(client);
            armorHud.render(context, (x, y, item) -> renderHotbarItem(context, x, y, tickDelta, client.player, item, 1));
        }

        if (Main.config.get(FoxClientSetting.BlockHudEnabled, Boolean.class)) {
            if (blockHud == null) blockHud = new BlockHud(client);
            blockHud.render(context, (x, y, item) -> renderHotbarItem(context, x, y, tickDelta, client.player, item, 1));
        }
    }
}
