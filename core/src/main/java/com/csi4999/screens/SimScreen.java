package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.ui.PanCam;

import java.util.concurrent.locks.ReentrantLock;

public class SimScreen implements Screen {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    private Environment env;

    private volatile boolean threadRunning;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        Gdx.input.setInputProcessor(new PanCam(worldViewport, worldCam)); // TODO: use multiplexer

        this.env = new Environment(3000, 150);

        threadRunning = true;
        new Thread(() -> {
            while (threadRunning) {
                env.update(1/60f);
//                System.out.println("hi");
            }

        }).start();
    }

    @Override
    public void render(float delta) {

//        env.update(1/60f);

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
    public void dispose() {
        threadRunning = false;
    }
}
