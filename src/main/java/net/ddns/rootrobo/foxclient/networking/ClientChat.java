package net.ddns.rootrobo.foxclient.networking;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import net.minecraft.client.MinecraftClient;

public final class ClientChat {

    public static final String HOST = System.getProperty("host", "foxsquad.ddns.net");
    public static final int PORT = Integer.parseInt(System.getProperty("port", "25565"));
    public static Channel ch;

    public static boolean firstStart = true;
    public static void start() throws Exception {
        if(firstStart) {
            System.out.println("CC START");
        }

        // Configure SSL.
        final SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        EventLoopGroup group = new NioEventLoopGroup(1, (new ThreadFactoryBuilder()).setNameFormat("Chat").build());
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChatInitializer(sslCtx));

            // Start the connection attempt.
            if(firstStart) {
                System.out.println("CC CONNECT");
            }
            try {
                ChannelFuture f = b.connect(HOST, PORT);
                f.addListener( new ChannelFutureListener() {
                    @Override public void operationComplete(ChannelFuture future) throws Exception {
                        if(!future.isSuccess()) {//if is not successful, reconnect
                            future.channel().close();
                            firstStart = false;
                            start();
                            // ?
                            Thread.currentThread().interrupt();
                        } else {
                            ch = future.channel();
                            //add a listener to detect the connection lost
                            addCloseDetectListener(ch);
                        }
                    }

                    private void addCloseDetectListener(Channel channel) {
                        //if the channel connection is lost, the ChannelFutureListener.operationComplete() will be called
                        channel.closeFuture().addListener((ChannelFutureListener) future -> start());
                    }
                });
            } catch(Exception ignored) {
                firstStart = false;
                start();
            }
        } catch (Exception ignored) {
            if(firstStart) System.out.println("CC CONNECT FAILED");
            group.shutdownGracefully().await(1000);
            group.shutdownNow();
            Thread.sleep(1000);
            firstStart = false;
            start();
        }
    }

    public static boolean send(String msg) {
        if(ch != null && ch.isWritable()) {
            ch.writeAndFlush(MinecraftClient.getInstance().getSession().getUsername() +":"+msg+"\r\n");
            return true;
        } else {
            return false;
        }
    }
}
