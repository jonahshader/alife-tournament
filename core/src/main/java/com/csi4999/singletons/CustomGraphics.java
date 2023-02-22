package com.csi4999.singletons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import static com.csi4999.singletons.CustomAssetManager.SPRITE_PACK;

public class CustomGraphics {
    private static CustomGraphics instance;

    public Sprite circle;

    private CustomGraphics() {
        TextureAtlas t = CustomAssetManager.getInstance().manager.get(SPRITE_PACK);
        circle = new Sprite(t.findRegion("circle"));
        circle.setOrigin(circle.getWidth()/2, circle.getHeight()/2);
    }

    public static CustomGraphics getInstance() {
        if (instance == null)
            instance = new CustomGraphics();
        return instance;
    }
}
