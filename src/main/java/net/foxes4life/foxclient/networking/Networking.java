package net.foxes4life.foxclient.networking;

import net.foxes4life.foxclient.Main;
import net.foxes4life.foxclient.networking.shared.LoggedInWebsocketPacket;
import net.foxes4life.foxclient.networking.shared.LowWebsocketPacket;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.util.ArrayList;

public class Networking {
    private static WebSocketClient client;

    public static void init() {
        try {
            URI uri = new URI("wss://backend.client.foxes4life.net");

            client = new WebSocketClientImpl(uri);
            client.connectBlocking();

            if (client.isClosed() || !client.isOpen()) {
                Main.LOGGER.error("Failed to connect to backend!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void joinServer(String address) {
        if (sendPacket(constructLoggedInPacket(LoggedInWebsocketPacket.JOIN_SERVER, address))) {
            Main.LOGGER.info("Sent join server packet!");
        } else {
            Main.LOGGER.error("Failed to send join server packet!");
        }
    }

    public static void leaveServer(String address) {
        if (sendPacket(constructLoggedInPacket(LoggedInWebsocketPacket.LEAVE_SERVER, address))) {
            Main.LOGGER.info("Sent leave server packet!");
        } else {
            Main.LOGGER.error("Failed to send leave server packet!");
        }
    }

    private static boolean sendPacket(byte[] packet) {
        if (client == null || client.isClosed()) {
            return false;
        } else {
            client.send(packet);
            return true;
        }
    }

    private static byte[] constructLoggedInPacket(LoggedInWebsocketPacket packet, String data) {
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.add(LowWebsocketPacket.COMMON_LOGGED_IN_PACKET.getId());
        bytes.add(packet.getId());
        for (byte b : data.getBytes()) {
            bytes.add(b);
        }
        byte[] bytesArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bytesArray[i] = bytes.get(i);
        }
        return bytesArray;
    }
}
