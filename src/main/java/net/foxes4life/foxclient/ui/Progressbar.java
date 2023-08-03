package net.foxes4life.foxclient.ui;

import net.minecraft.client.gui.DrawContext;

public class Progressbar {
    public int x;
    public int y;
    public int width;
    public int height;
    
    public Progressbar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void render(DrawContext context, double progress) {
        int progressWidth = (int)Math.ceil(width * progress);
        int cornerSize = (int)(height * .2f);

        context.fill(x + cornerSize, y, x + width - cornerSize, y + cornerSize, 0x33ffffff);
        context.fill(x, y + cornerSize, x + width, y + height - cornerSize, 0x33ffffff);
        context.fill(x + cornerSize, y + height - cornerSize, x + width - cornerSize, y + height, 0x33ffffff);

        context.fill(x, y + cornerSize, x + progressWidth, y + height - cornerSize, 0xffffffff);
        if (progressWidth > cornerSize) {
            context.fill(x + cornerSize, y, x + progressWidth - cornerSize, y + cornerSize, 0xffffffff);
            context.fill(x + cornerSize, y + height - cornerSize, x + progressWidth - cornerSize, y + height, 0xffffffff);
        }
    }
}
