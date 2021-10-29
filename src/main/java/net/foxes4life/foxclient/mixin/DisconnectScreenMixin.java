package net.foxes4life.foxclient.mixin;

/*
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
*/
import net.foxes4life.foxclient.gui.widgets.NicerButtonWidget;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
/*
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Session;
*/
import net.minecraft.text.Text;
/*
import net.minecraft.text.TranslatableText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
*/
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
*/
@Mixin(DisconnectedScreen.class)
public abstract class DisconnectScreenMixin extends Screen {
    //private static final String clientToken = "9b542dad7abf1c872d0756d22ea3e871"; // CHANGE THIS


    protected DisconnectScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        this.addDrawableChild(new NicerButtonWidget(this.width / 2 - 100, this.height - 110, 200, 20, new TranslatableText("foxclient.gui.button.reconnect"), (buttonWidget) -> {
            buttonWidget.setMessage(Text.of("soon"));
        }));
    }

    /*
    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 110, 200, 20, new TranslatableText("idk.reauth"), (buttonWidget) -> {
            String token = MinecraftClient.getInstance().getSession().getAccessToken();
            token = refreshToken(token);
            Session cs = MinecraftClient.getInstance().getSession();

            Session ns = new Session(cs.getUsername(), cs.getUuid(), token, "mojang");
            try {
                setSession(ns);
                System.out.println("SUCCESS!");
            } catch (Exception ignored) {
                System.out.println("FAILED!!");
            }
        }));
    }

    private static String refreshToken(String token) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://authserver.mojang.com/validate");
        post.setHeader("Content-Type", "application/json");
        try {
            post.setEntity(new StringEntity("{ \"accessToken\" : \""+token+"\", \"clientToken\": \""+clientToken+"\" }"));
            HttpResponse response = httpclient.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(rd).getAsJsonObject();
            System.out.println("got new token :3");
            return object.get("accessToken").getAsString();
        } catch (IOException ignored) {
        }
        return token;
    }

    private static void setSession(Session s) throws Exception {
        Class<? extends MinecraftClient> client = MinecraftClient.getInstance().getClass();
        try {
            Field session = null;

            for (Field f : client.getDeclaredFields()) {
                if (f.getType().isInstance(s)) {
                    session = f;
                    System.out.println("Found field " + f.toString() + ", injecting...");
                }
            }

            if (session == null) {
                throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
            }

            session.setAccessible(true);
            session.set(MinecraftClient.getInstance(), s);
            session.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    */
}
