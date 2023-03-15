package com.csi4999.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.networking.GameClient;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.io.IOException;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class ConnectScreen implements Screen, InputProcessor {
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
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
        Table t = new Table();
        Table body = new Table();
        t.setFillParent(true);
        t.top();

        body.align(Align.center);
        t.align(Align.center);

        Label screenLabel = new Label("Connect To Server", skin);
        Label ipLabel = new Label("IP", skin);
        Label portLabel = new Label("Port", skin);


        TextField ip = new TextField("localhost", skin);
        TextField port = new TextField("25565", skin);

        TextButton connect = new TextButton("Connect", skin);

        connect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    GameClient.getInstance().tryConnect(ip.getText(), Integer.parseInt(port.getText()));
                    // TODO: we need some kinda menu after mainmenu, or we need to rework stuff
                    ScreenStack.switchTo(new LoginScreen(app));
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: put red text somewhere that says failed to connect
                }
            }
        });

        int vPadAmount = 10;
        int hPadAmount = 3;
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        body.add(ipLabel).fill();
        body.add(ip).fill();
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        body.add(portLabel).fill();
        body.add(port).fill();
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);

        t.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        t.add(screenLabel).center();
        t.row();
        t.add(body).fill();
        t.row();
        t.add(connect).center();

        stage.addActor(t);
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

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            ScreenStack.pop();
            return true;
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
