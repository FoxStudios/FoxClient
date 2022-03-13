package net.foxes4life.foxclient.util;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

public class TextureUtil {
    public static AbstractTexture fromIdentifier(Identifier id) {
        return new ResourceTexture(id);
    }
}
