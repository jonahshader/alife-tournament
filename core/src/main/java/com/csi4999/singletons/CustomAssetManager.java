package com.csi4999.singletons;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public final class CustomAssetManager {
    private static CustomAssetManager instance;
    public final AssetManager manager = new AssetManager();

    // Textures
    public static final String SPRITE_PACK = "graphics/sprites.atlas";

    public void loadImages() {
        manager.load(SPRITE_PACK, TextureAtlas.class);
    }

    public void loadSounds() {

    }

    public void loadFonts() {

    }

    public static CustomAssetManager getInstance() {
        if (instance == null)
            instance = new CustomAssetManager();
        return instance;
    }
}
