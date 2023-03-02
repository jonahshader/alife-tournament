package com.csi4999.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.ui.FollowCam;
import com.csi4999.systems.ui.PanCam;

public class SimScreen implements Screen, InputProcessor {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    private Environment env;

    private boolean drawingEnabled = true;
    private FollowCam followCam;

    private volatile boolean threadRunning;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        followCam = new FollowCam(worldViewport, worldCam);
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(new FollowCam(worldViewport, worldCam));
        m.addProcessor(new PanCam(worldViewport, worldCam));
        m.addProcessor(this);
        Gdx.input.setInputProcessor(m);

        this.env = new Environment(3000, 150);

        threadRunning = true;
        new Thread(() -> {
            while (threadRunning) {
                env.update(1/60f);
            }

        }).start();
    }

    @Override
    public void render(float delta) {
        if (drawingEnabled) {
            worldViewport.apply();
            app.batch.setProjectionMatrix(worldCam.combined);
            followCam.updateCamera();

            // set clear color
            Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
            // apply clear color to screen
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            app.batch.begin();
            env.draw(app.shapeDrawer, app.batch);
            app.batch.end();
        }
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

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.O) {
            drawingEnabled = !drawingEnabled;
            return true;
        }
        if (keycode == Input.Keys.P) {
            threadRunning = !threadRunning;
            if (threadRunning) {
                new Thread(() -> {
                    while (threadRunning) {
                        env.update(1/60f);
                    }

                }).start();
            }
            return true;
        }
        if (keycode == Input.Keys.C) {
            Vector3 pos = worldCam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            followCam.assignCreature(env.getCreature((int) pos.x, (int) pos.y));
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            followCam.unassign();
            worldCam.position.set(0,0,0);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
