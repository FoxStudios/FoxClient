package net.foxes4life.foxclient.networking;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.SessionConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WebSocketClientImpl extends WebSocketClient {
    public WebSocketClientImpl(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected");
        System.out.println(handshakedata.getHttpStatus());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received" + message);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        //System.out.println("received ByteBuffer");
        byte[] packet = bytes.array();
        byte packetId = packet[0];
        byte[] data = Arrays.copyOfRange(packet, 1, packet.length);
        //System.out.println("Received packet from server: " + packetId + " " + new String(data));

        switch (packetId) {
            case 0x02 -> {
                //System.out.println("S2C_ENCRYPTION_KEY_REQUEST");
                String dataString = new String(data);
                String[] split = dataString.split("\0");
                String serverPublicKey = split[0];

                // generate a random 16 character string
                Random random = new Random();
                String sharedSecret = "";
                for (int i = 0; i < 16; i++) {
                    sharedSecret += (char) (random.nextInt(26) + 'a');
                }

                // hash: server id + shared secret + public key
                String hash = BrokenHash.hash("" + sharedSecret + serverPublicKey);

                // send request to https://sessionserver.mojang.com/session/minecraft/join

                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("https://sessionserver.mojang.com/session/minecraft/join");
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Accept", "application/json");

                Session mcSession = MinecraftClient.getInstance().getSession();

                try {
                    post.setEntity(new StringEntity("{\"accessToken\": \"" + mcSession.getAccessToken() +
                            "\", \"selectedProfile\": \"" + mcSession.getUuid() + "\", \"serverId\": \"" + hash + "\"}"));
                    HttpResponse execute = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                    this.close();
                    return;
                }

                List<Byte> response = new ArrayList<>();
                response.add((byte) 0x03); // todo: use class for C2S_ENCRYPTION_KEY_RESPONSE

                for (byte b : sharedSecret.getBytes()) {
                    response.add(b);
                }

                byte[] responseArray = new byte[response.size()];
                for (int i = 0; i < response.size(); i++) {
                    responseArray[i] = response.get(i);
                }

                //System.out.println("hash: " + hash);

                this.send(responseArray);
            }
            case 0x04 -> {
                Main.LOGGER.info("Backend Login successful");
                SessionConstants.BACKEND_IS_LOGGED_IN = true;
                this.close();
            }
            default -> {
                System.out.println("Unknown packet " + packetId);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
