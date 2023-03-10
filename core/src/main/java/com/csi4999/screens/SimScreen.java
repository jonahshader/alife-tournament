package com.csi4999.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.csi4999.systems.ui.CreatureHud;
import com.csi4999.systems.ui.PanCam;
import com.csi4999.systems.ui.StatsHud;
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

    public UserAccountPacket user;
    private ToolBar toolBar;
    private CreatureHud creatureHud;
    private StatsHud statsHud;

    private Thread simThread;

    public SimScreen(ALifeApp app, UserAccountPacket user) {
        this(app, user, new Environment(EnvProperties.makeTestDefault()));
    }

    public SimScreen(ALifeApp app, UserAccountPacket user, Environment environment) {
        this.app = app;
        this.user = user;
        this.env = environment;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        creatureHud = new CreatureHud(app.batch, worldCam, app, env);
        statsHud = new StatsHud(env.creatureSpawner, env.foodSpawner);

        toolBar = new ToolBar(app.batch, this);
    }

    private void tryLaunchSimThread() {
        if (simThread == null || !simThread.isAlive()) {
            simThread = new Thread(() -> {
                while (!limitSpeed && playing) {
                    env.update();
                    statsHud.update();
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
                statsHud.update();
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

        creatureHud.updateCamera();

        if (renderingEnabled) {
            app.batch.begin();
            env.draw(app.shapeDrawer, app.batch, worldCam);
            app.batch.end();
        }


        creatureHud.render(delta);
        toolBar.render();
        statsHud.render(app.shapeDrawer);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        toolBar.resize(width, height);
        creatureHud.resize(width, height);
        statsHud.resize(width, height);
    }

    @Override
    public void show() {
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(statsHud);
        m.addProcessor(toolBar.stage);
        m.addProcessor(creatureHud.stage);
        m.addProcessor(creatureHud);
        m.addProcessor(new PanCam(worldViewport, worldCam));
        m.addProcessor(this);
        Gdx.input.setInputProcessor(m);
    }

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
        if (keycode == Input.Keys.ESCAPE) {
            playing = false;
            ScreenStack.pop();
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
