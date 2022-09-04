package net.foxes4life.foxclient.mixin;

import net.foxes4life.foxclient.SessionConstants;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
    @Shadow
    @Final
    private Screen parent;

    @Shadow
    private int reasonHeight;

    protected DisconnectedScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void init(CallbackInfo ci) {
        Objects.requireNonNull(this.textRenderer);
        int y = this.height / 2 + this.reasonHeight / 2 + 25;

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, Math.min(y + 9, this.height - 30), 200, 20,
                TextUtils.translatable("foxclient.gui.button.reconnect"),
                (button -> {
                    assert client != null;

                    if (SessionConstants.LAST_SERVER == null) {
                        button.setMessage(Text.of("ERROR"));
                        return;
                    }
                    ConnectScreen.connect(this.parent, client, ServerAddress.parse(SessionConstants.LAST_SERVER.address),
                            SessionConstants.LAST_SERVER);
                })));
    }
}
