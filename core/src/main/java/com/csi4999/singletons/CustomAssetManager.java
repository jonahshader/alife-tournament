package com.csi4999.singletons;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class CustomAssetManager {
    private CustomAssetManager() {}
    private static CustomAssetManager instance;
    public final AssetManager manager = new AssetManager();

    // Textures
    public static final String SPRITE_PACK = "graphics/sprites.atlas";

    // Fonts
    public static final String UI_FONT = "ui/neutralizer/raw/font-title-export.fnt";

    // Skins
    public static final String SKIN_MAIN = "ui/neutralizer/skin/neutralizer-ui.json";

    public void loadImages() {
        manager.load(SPRITE_PACK, TextureAtlas.class);
    }

    public void loadSounds() {

    }

    public void loadFonts() {
        manager.load(UI_FONT, BitmapFont.class);
    }

    public void loadSkins() {
        manager.load(SKIN_MAIN, Skin.class);
    }

    public static CustomAssetManager getInstance() {
        if (instance == null)
            instance = new CustomAssetManager();
        return instance;
    }
}
