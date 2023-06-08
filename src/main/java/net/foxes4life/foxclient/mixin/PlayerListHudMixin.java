package net.foxes4life.foxclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.foxes4life.foxclient.SessionConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    final Identifier FOXCLIENT_TAIL = new Identifier("foxclient", "textures/ui/branding/tail.png");

    @Inject(at = @At("TAIL"), method = "renderLatencyIcon")
    private void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        String uuidHash = SessionConstants.UUID_HASHES.get(entry.getProfile().getId().toString().replace("-", ""));
        if (uuidHash != null) {
            if (SessionConstants.FOXCLIENT_USERS.contains(uuidHash)) {
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, FOXCLIENT_TAIL);
                context.drawTexture(FOXCLIENT_TAIL, x + width - 20, y, 0, 8, 8, 8, 8, 8);
            }
        }
    }
}