package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.ui.PanCam;

public class SimScreen implements Screen {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        Gdx.input.setInputProcessor(new PanCam(worldViewport, worldCam)); // TODO: use multiplexer
    }

    @Override
    public void render(float delta) {
        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();
        app.shapeDrawer.setColor(1f, 1f, 1f, 1f);
        app.shapeDrawer.filledCircle(0f, 0f, 32f);

        app.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
    }

    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
