package net.ddns.rootrobo.foxclient.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    @Shadow @Final private Screen parent;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void onClose() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }
}
