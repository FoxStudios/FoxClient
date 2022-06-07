package net.foxes4life.foxclient.screen.worldselect;

import net.foxes4life.foxclient.gui.FoxClientMiniButton;
import net.foxes4life.foxclient.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FoxClientWorldSelect extends Screen {
    Screen parent;

    Identifier closeButtonTex = new Identifier("foxclient", "textures/ui/screen/worldselect/icons/close.png");
    Identifier editButtonTex = new Identifier("foxclient", "textures/ui/screen/worldselect/icons/edit.png");
    Identifier deleteButtonTex = new Identifier("foxclient", "textures/ui/screen/worldselect/icons/delete.png");
    Identifier playButtonTex = new Identifier("foxclient", "textures/ui/screen/worldselect/icons/play.png");
    Identifier createButtonTex = new Identifier("foxclient", "textures/ui/screen/worldselect/icons/create.png");

    public FoxClientWorldSelect (Screen parent) {
        super(TextUtils.string("WorldSelect"));
        this.parent = parent;
    }

    public void init () {
        this.client.keyboard.setRepeatEvents(true);

        //close
        this.addDrawableChild(new FoxClientMiniButton(0, this.height - 32, 32, 32, 0, 0, 20, closeButtonTex, 32, 64,
                (button) -> {
                    this.close();
                }, TextUtils.string("close")));

        //delete
        this.addDrawableChild(new FoxClientMiniButton(this.width - 32, this.height - 32, 32, 32, 0, 0, 20, deleteButtonTex, 32, 64,
                (button) -> {
                    // yes
                }, TextUtils.string("delete")));

        //edit
        this.addDrawableChild(new FoxClientMiniButton(this.width - 64, this.height - 32, 32, 32, 0, 0, 20, editButtonTex, 32, 64,
                (button) -> {
                    // yes
                }, TextUtils.string("edit")));

        //create
        this.addDrawableChild(new FoxClientMiniButton(this.width - 96, this.height - 32, 32, 32, 0, 0, 20, createButtonTex, 32, 64,
                (button) -> {
                    CreateWorldScreen.create(MinecraftClient.getInstance(), this);
                }, TextUtils.string("create")));

        //play
        this.addDrawableChild(new FoxClientMiniButton(this.width - 128, this.height - 32, 32, 32, 0, 0, 20, playButtonTex, 32, 64,
                (button) -> {
                    // yes
                },  TextUtils.string("play")));
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), 0xFF282828);
        fill(matrices, 0, this.height, this.width, this.height - 32, 0xFF383838);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
