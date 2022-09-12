package net.foxes4life.foxclient;

import net.minecraft.client.network.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionConstants {
    public static ServerInfo LAST_SERVER = null;
    public static boolean BACKEND_IS_LOGGED_IN = false;

    public static HashMap<String, String> UUID_HASHES = new HashMap<>();
    public static ArrayList<String> FOXCLIENT_USERS = new ArrayList<>();
}
