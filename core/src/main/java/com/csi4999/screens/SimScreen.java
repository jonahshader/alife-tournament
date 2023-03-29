package com.csi4999.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.common.ChunkPerformance;
import com.csi4999.systems.networking.packets.NewRanksPacket;
import com.csi4999.systems.networking.packets.TournamentPacket;
import com.csi4999.systems.networking.packets.TournamentResultsPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.csi4999.systems.tournament.WinCondition;
import com.csi4999.systems.ui.*;

import java.util.List;

public class SimScreen implements Screen, InputProcessor {
    public static SimScreen instance;
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
    public ToolBar toolBar;
    private CreatureHud creatureHud;
    private StatsHud statsHud;
    public ChunkSelector chunkSelector;
    private WinCondition winCondition;
    public TournamentResultsPacket tournamentResults;
    public NewRanksPacket newRanksPacket;
    public List<String> chunkNames;
    public List<Float> initialRanks;
    private DisplayResults displayResults;
    private CreatureNameTag creatureNameTag;

    private Thread simThread;

    public SimScreen(ALifeApp app, UserAccountPacket user, EnvProperties properties) {
        this(app, user, new Environment(properties));
    }

    public SimScreen(ALifeApp app, UserAccountPacket user, Environment env) {
        instance = this;
        this.app = app;
        this.user = user;
        this.env = env;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        creatureHud = new CreatureHud(app.batch, worldCam, app, env);
        statsHud = new StatsHud(env.creatureSpawner, env.foodSpawner);
        chunkSelector = new ChunkSelector(worldViewport, worldCam, this);
        toolBar = new ToolBar(app.batch, this, false);
        displayResults = new DisplayResults(this);
    }

    public SimScreen(ALifeApp app, UserAccountPacket user, TournamentPacket tournament) {
        instance = this;
        this.app = app;
        this.user = user;
        env = tournament.environment;
        chunkNames = tournament.names;
        initialRanks = tournament.initialRanks;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        creatureHud = new CreatureHud(app.batch, worldCam, app, env);
        statsHud = new StatsHud(env.creatureSpawner, env.foodSpawner);
        toolBar = new ToolBar(app.batch, this, true);
        winCondition = new WinCondition(this, tournament.chunkIDs);
        displayResults = new DisplayResults(this);
        creatureNameTag = new CreatureNameTag(tournament.chunkIDs, tournament.names, env.creatureSpawner);
    }

    private void tryLaunchSimThread() {
        if (simThread == null || !simThread.isAlive()) {
            simThread = new Thread(() -> {
                while (!limitSpeed && playing) {
                    mainLoop();
                }
            });
            simThread.start();
        }
    }

    private void mainLoop() {
        env.update();
        statsHud.update();
        if (winCondition != null)
            winCondition.update();
    }


    @Override
    public void render(float delta) {
        if (playing) {
            if (limitSpeed) {
                mainLoop();
            } else {
                tryLaunchSimThread();
            }
        }

//        if (!savedUserRank && newRanksPacket != null && tournamentResults != null) {
//            for (ChunkPerformance p : tournamentResults.performances) {
//                if (p.user)
//            }
//
//            savedUserRank = true;
//        }


        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.21f, .2f, .21f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        creatureHud.updateCamera();

        if (renderingEnabled) {
            app.batch.begin();
            env.draw(app.shapeDrawer, app.batch, worldCam);
            if (creatureNameTag != null) {
                creatureNameTag.render(app.batch);
            }

            app.batch.end();
        }


        creatureHud.render(delta);
        toolBar.render();
        statsHud.render(app.shapeDrawer);
        if (chunkSelector != null)
            chunkSelector.render(app.shapeDrawer, delta);
        displayResults.render(app.shapeDrawer);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        toolBar.resize(width, height);
        creatureHud.resize(width, height);
        statsHud.resize(width, height);
        displayResults.resize(width, height);
    }

    @Override
    public void show() {
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(statsHud);
        m.addProcessor(toolBar.stage);
        m.addProcessor(creatureHud.stage);
        m.addProcessor(creatureHud);
        m.addProcessor(new PanCam(worldViewport, worldCam));
        if (chunkSelector != null)
            m.addProcessor(chunkSelector);
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
            ScreenStack.switchTo(new MainMenuScreen(app));
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
