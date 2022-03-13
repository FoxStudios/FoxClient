package net.foxes4life.foxclient.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AddAltAccountScreen extends Screen {
    protected final Screen parent;
    private List<OrderedText> tooltipText;
    private ButtonWidget addButton;
    private ButtonWidget cancelButton;
    protected TextFieldWidget username;
    protected TextFieldWidget token;

    public AddAltAccountScreen(Screen parent) {
        super(new TranslatableText("ADD ALT ACCOUNT"));
        this.parent = parent;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void tick() {
        this.username.tick();
    }

    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.username = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.username, new TranslatableText("username"));
        this.token = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 44, 200, 20, this.token, new TranslatableText("token"));
        this.token.setMaxLength(512);
        this.token.setUneditableColor(new Color(0xFF0000).getRGB());

        this.addSelectableChild(this.username);
        this.addSelectableChild(this.token);

        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 80, 150, 20, new TranslatableText("foxclient.gui.altmanager.login"), (buttonWidget) -> {
            System.out.println("Trying to log in into: ");
            buttonWidget.active = false;
            buttonWidget.setMessage(new TranslatableText("foxclient.gui.altmanager.logging_in"));

            String username = this.username.getText();
            String token = this.token.getText();
            String uuid = null;
            System.out.println("Username: "+username);
            System.out.println("Token: "+token);

            try {
                JsonObject o = readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/"+username);
                uuid = o.get("id").getAsString();
                username = o.get("name").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(uuid == null) {
                System.out.println("could not find mc account");
                buttonWidget.setMessage(new TranslatableText("foxclient.gui.altmanager.login_failed.not_found"));
                return;
            }

            System.out.println("UUID: "+uuid);

            boolean success = validateToken(token);
            if(success) {
                System.out.println("TOKEN VALID!");
                Session s = new Session(username, uuid, token, Optional.of("mojang"), this.client.getSession().getClientId(), Session.AccountType.MOJANG); //todo: change
                try {
                    setSession(s);
                    buttonWidget.setMessage(new TranslatableText("foxclient.gui.altmanager.logged_in"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("TOKEN INVALID!");
                Session s = new Session(username, uuid, token, Optional.of("mojang"), this.client.getSession().getClientId(), Session.AccountType.MOJANG); //todo: change
                try {
                    setSession(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            buttonWidget.active = false;
        }));

        this.cancelButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 76, this.height - 80, 72, 20, ScreenTexts.CANCEL, (buttonWidget) -> {
            this.client.setScreen(this.parent);
        }));

        this.setInitialFocus(this.username);
    }

    public void onClose() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.username.render(matrices, mouseX, mouseY, delta);
        this.token.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderOrderedTooltip(matrices, this.tooltipText, mouseX, mouseY);
        }

    }

    public void setTooltip(List<OrderedText> list) {
        this.tooltipText = list;
    }

    private static JsonObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonParser parser = new JsonParser();
            return parser.parse(rd).getAsJsonObject();
        } finally {
            is.close();
        }
    }

    private static boolean validateToken(String token) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://authserver.mojang.com/validate");
        post.setHeader("Content-Type", "application/json");
        try {
            post.setEntity(new StringEntity("{ \"accessToken\" : \""+token+"\" }"));
            HttpResponse response = httpclient.execute(post);
            if(response.getStatusLine().getStatusCode() == 204) return true;
        } catch (IOException ignored) {
            return false;
        }

        return false;
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
}