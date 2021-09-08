package net.ddns.rootrobo.foxclient.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientChatHandler extends SimpleChannelInboundHandler<String> {
    private static final char COLOR_CHAR = '\u00A7';
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        try {
            ctx.close().await(900);
            ctx.channel().close().await(100);
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (MinecraftClient.getInstance().player != null) {
            msg = translateAlternateColorCodes('&', msg);
            MinecraftClient.getInstance().player.sendMessage(Text.of("§8[§6CC§8]§r §b"+msg), false);
        }
    }

    // stolen from bukkit, hehe
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }
}
