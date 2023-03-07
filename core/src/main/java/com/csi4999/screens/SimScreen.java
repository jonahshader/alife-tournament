package com.csi4999.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.csi4999.systems.ui.PanCam;
import com.csi4999.systems.ui.ToolBar;

public class SimScreen implements Screen, InputProcessor {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    public final ALifeApp app;

    public Environment env;

    public volatile boolean limitSpeed = true;
    public boolean renderingEnabled = true;
    public volatile boolean playing = true;

    private UserAccountPacket user;
    private ToolBar toolBar;

    private Thread simThread;

    public SimScreen(ALifeApp app, UserAccountPacket user) {
        this.app = app;
        this.user = user;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);

        toolBar = new ToolBar(app.batch, this);

        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(toolBar.stage);
        m.addProcessor(new PanCam(worldViewport, worldCam));
        m.addProcessor(this);
        Gdx.input.setInputProcessor(m);

        this.env = new Environment(3000, 150);
    }

    private void tryLaunchSimThread() {
        if (simThread == null || !simThread.isAlive()) {
            simThread = new Thread(() -> {
                while (!limitSpeed && playing) {
                    env.update();
                }
            });
            simThread.start();
        }
    }


    @Override
    public void render(float delta) {
        if (playing) {
            if (limitSpeed) {
                env.update();
            } else {
                tryLaunchSimThread();
            }
        }


        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (renderingEnabled) {
            app.batch.begin();
            env.draw(app.shapeDrawer, app.batch);
            app.batch.end();
        }

        toolBar.render();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        toolBar.resize(width, height);
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
        playing = false;
        toolBar.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
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
