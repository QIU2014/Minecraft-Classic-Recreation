package com.eric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Textures {
    private static final HashMap<String, Texture> idMap = new HashMap<>();

    public static Texture loadTexture(String resourceName) {
        return loadTexture(resourceName, Texture.TextureFilter.Nearest);
    }

    public static Texture loadTexture(String resourceName, Texture.TextureFilter filter) {
        Texture cached = idMap.get(resourceName);
        if (cached != null) return cached;

        Texture texture = new Texture(Gdx.files.internal(resourceName));
        // Match the original's GL_NEAREST + gluBuild2DMipmaps as closely as possible.
        texture.setFilter(filter, filter);
        idMap.put(resourceName, texture);
        return texture;
    }

    public static void dispose() {
        for (Texture t : idMap.values()) t.dispose();
        idMap.clear();
    }
}
