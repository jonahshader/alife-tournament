package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.ui.PanCam;

public class SimScreen implements Screen {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    private Environment env;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        Gdx.input.setInputProcessor(new PanCam(worldViewport, worldCam)); // TODO: use multiplexer

        this.env = new Environment(100, 100);

    }

    @Override
    public void render(float delta) {
        Vector2 mousePos = worldViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        env.creatureSpawner.allCreatures.get(0).position.set(mousePos);

        env.update(delta);

        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();
        env.draw(app.shapeDrawer, app.batch);
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
