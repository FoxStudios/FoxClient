package net.foxes4life.foxclient.networking;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.SessionConstants;
import net.foxes4life.foxclient.networking.shared.BrokenHash;
import net.foxes4life.foxclient.networking.shared.LoggedInWebsocketPacket;
import net.foxes4life.foxclient.networking.shared.LowWebsocketPacket;
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
        LowWebsocketPacket lowWebsocketPacket = LowWebsocketPacket.values()[packetId];
        byte[] data = Arrays.copyOfRange(packet, 1, packet.length);
        //System.out.println("Received packet from server: " + packetId + " " + new String(data));

        switch (lowWebsocketPacket) {
            case S2C_LOGIN_ENCRYPTION_KEY_REQUEST -> {
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
                response.add(LowWebsocketPacket.C2S_LOGIN_ENCRYPTION_KEY_RESPONSE.getId());

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
            case S2C_LOGIN_SUCCESS -> {
                Main.LOGGER.info("Backend Login successful");
                SessionConstants.BACKEND_IS_LOGGED_IN = true;
                this.close();
            }
            case COMMON_LOGGED_IN_PACKET -> {
                byte loggedInPacketId = data[0];
                byte[] loggedInPacketData = Arrays.copyOfRange(data, 1, data.length);
                LoggedInWebsocketPacket loggedInPacket = LoggedInWebsocketPacket.values()[loggedInPacketId];

                switch (loggedInPacket) {
                    case PLAYERS_ON_SERVER -> {
                        String playersString = new String(loggedInPacketData);
                        String[] players = playersString.split("\0");

                        Main.LOGGER.info("Players on server: " + Arrays.toString(players));
                        SessionConstants.FOXCLIENT_USERS.clear();
                        SessionConstants.FOXCLIENT_USERS.addAll(Arrays.asList(players));
                    }
                }
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
