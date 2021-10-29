package net.foxes4life.foxclient.notification;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;

public class Notification {
    public static void showNotification(Text title, Text msg, String img_uri) {
        Toast toaster = new Toaster(img_uri, title, msg);
        MinecraftClient.getInstance().getToastManager().add(toaster);
    }
}
