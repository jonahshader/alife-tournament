package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.systems.networking.GameClient;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.io.IOException;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class ConnectScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera cam;
    private final FitViewport viewport;
    private final ALifeApp app;
    private Stage stage;


    public ConnectScreen(ALifeApp app) {
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        cam = new OrthographicCamera();
        viewport = new FitViewport(400, 400, cam);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();
        stage = new Stage(viewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table mainTable = new Table();
        Table ipTable = new Table();
        Table portTable = new Table();
        ipTable.setFillParent(true);
        portTable.setFillParent(true);
        mainTable.setFillParent(true);
        mainTable.top();
        ipTable.top();
        portTable.top();
        mainTable.align(Align.center);
        ipTable.align(Align.center);
        portTable.align(Align.center);

        TextField ip = new TextField("localhost", skin);
        TextField port = new TextField("25565", skin);

        TextButton connect = new TextButton("Connect", skin);

        connect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    GameClient.getInstance().tryConnect(ip.getText(), Integer.parseInt(port.getText()));
//                    app.setScreen(new SimScreen(app)); // TODO: we need some kinda menu after mainmenu, or we need to rework stuff
                    app.setScreen(new LoginScreen(app));
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: put red text somewhere that says failed to connect
                }
            }
        });

        ipTable.add(ip);
        portTable.add(port);

        mainTable.add(ipTable);
        mainTable.add(portTable);
        mainTable.add(connect);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
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
        cam.update();
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
