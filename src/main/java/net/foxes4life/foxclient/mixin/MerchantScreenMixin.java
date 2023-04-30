package net.foxes4life.foxclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    @Shadow
    private int selectedIndex;

    @Inject(method = "syncRecipeIndex", at = @At("HEAD"))
    private void syncRecipeIndex(CallbackInfo ci) {
        TradeOffer trade = this.handler.getRecipes().get(selectedIndex);

        if (trade != null && !trade.isDisabled() && Screen.hasShiftDown()) {
            if (this.handler.getSlot(2).getStack().getItem() == trade.getSellItem().getItem()) {
                MinecraftClient mc = MinecraftClient.getInstance();
                ClientPlayerInteractionManager interactionManager = mc.interactionManager;

                if (interactionManager == null) return;

                interactionManager.clickSlot(this.handler.syncId, 2, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
