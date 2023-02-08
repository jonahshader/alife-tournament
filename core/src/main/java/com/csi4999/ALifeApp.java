package com.csi4999;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.csi4999.screens.FirstScreen;
import com.csi4999.screens.MainMenuScreen;
import com.csi4999.screens.SimScreen;
import com.csi4999.singletons.CustomAssetManager;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.csi4999.singletons.CustomAssetManager.SPRITE_PACK;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ALifeApp extends Game {
    public SpriteBatch batch;
    public ShapeDrawer shapeDrawer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load assets
        CustomAssetManager.getInstance().loadImages();
        CustomAssetManager.getInstance().manager.finishLoading();

        // get white pixel necessary for ShapeDrawer to function
        TextureAtlas atlas = CustomAssetManager.getInstance().manager.get(SPRITE_PACK);
        TextureRegion pixel = atlas.findRegion("white_pixel");
        shapeDrawer = new ShapeDrawer(batch, pixel);


        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
