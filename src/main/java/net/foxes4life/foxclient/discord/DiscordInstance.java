package net.foxes4life.foxclient.discord;

public class DiscordInstance {
    private static Discord discord = null;

    public static Discord get() {
        if(discord == null) {
            discord = new Discord();
        }
        return discord;
    }
}
