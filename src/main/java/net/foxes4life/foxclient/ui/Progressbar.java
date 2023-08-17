package net.foxes4life.foxclient.ui;

import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class Progressbar {
    public int x;
    public int y;
    public int width;
    public int height;
    public float alpha = 1f;
    
    public Progressbar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void render(DrawContext context, double progress) {
        int progressWidth = (int)Math.ceil(width * progress);
        int cornerSize = (int)(height * .2f);

        int backgroundColor = new Color(1, 1, 1, .2f * alpha).getRGB();
        int foregroundColor = new Color(1, 1, 1, alpha).getRGB();

        context.fill(x + cornerSize, y, x + width - cornerSize, y + cornerSize, backgroundColor);
        context.fill(x, y + cornerSize, x + width, y + height - cornerSize, backgroundColor);
        context.fill(x + cornerSize, y + height - cornerSize, x + width - cornerSize, y + height, backgroundColor);

        context.fill(x, y + cornerSize, x + progressWidth, y + height - cornerSize, foregroundColor);
        if (progressWidth > cornerSize) {
            context.fill(x + cornerSize, y, x + progressWidth - cornerSize, y + cornerSize, foregroundColor);
            context.fill(x + cornerSize, y + height - cornerSize, x + progressWidth - cornerSize, y + height, foregroundColor);
        }
    }
}
