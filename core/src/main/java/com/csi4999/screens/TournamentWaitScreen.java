package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.packets.TournamentPacket;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class TournamentWaitScreen implements Screen {
    public static TournamentWaitScreen instance;
    private Skin skin;
    private final OrthographicCamera cam;
    private final FitViewport viewport;
    private final ALifeApp app;
    private Stage stage;

    public volatile TournamentPacket tournamentPacket;
    public volatile boolean tournamentFailed = false;

    public TournamentWaitScreen(ALifeApp app) {
        instance = this;
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        cam = new OrthographicCamera();
        viewport = new FitViewport(400, 400, cam);

        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();
        stage = new Stage(viewport, app.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table t = new Table();

        t.setFillParent(true);
        t.center();
        Label message = new Label("Waiting for server to create tournament...", skin);

        t.add(message);

        stage.addActor(t);
    }

    @Override
    public void render(float delta) {
        if (tournamentPacket != null)
            ScreenStack.switchTo(new SimScreen(app, GameClient.getInstance().user, tournamentPacket));
        if (tournamentFailed)
            ScreenStack.switchTo(new MainMenuScreen(app));

        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(cam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(25,25, viewport.getWorldWidth() - 50, viewport.getWorldHeight() - 50);
        app.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
