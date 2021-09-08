package net.ddns.rootrobo.foxclient.mixin;

import net.ddns.rootrobo.foxclient.MainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.IOException;
import java.io.InputStream;

@Mixin(Window.class)
public abstract class WindowMixin {
    @ModifyVariable(method = "setIcon", at = @At(value = "INVOKE"), ordinal = 0)
    private InputStream setIcon16(InputStream icon16, InputStream icon32) {
        try {
            return MinecraftClient.getInstance().getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("foxclient", "icons/icon_16x16.png"));
        } catch (IOException ignored) {
            return icon16;
        }
    }
    @ModifyVariable(method = "setIcon", at = @At(value = "INVOKE"), ordinal = 1)
    private InputStream setIcon32(InputStream icon16, InputStream icon32) {
        try {
            return MinecraftClient.getInstance().getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("foxclient", "icons/icon_32x32.png"));
        } catch (IOException ignored) {
            return icon32;
        }
    }
}