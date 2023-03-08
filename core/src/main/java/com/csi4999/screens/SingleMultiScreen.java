package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.csi4999.systems.networking.GameServer;
import com.csi4999.systems.networking.common.Account;
import com.csi4999.systems.networking.packets.LoginPacket;
import com.csi4999.systems.networking.packets.RegisterPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;

import java.io.IOException;

import static com.csi4999.singletons.CustomAssetManager.*;
import static com.csi4999.systems.networking.GameServer.OFFLINE_PORT;

public class SingleMultiScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;
    private BitmapFont titleFont;
    private Color titleFontColor;

    public SingleMultiScreen(ALifeApp app) {
        this.app = app;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        titleFont = CustomAssetManager.getInstance().manager.get(UI_FONT);
        titleFontColor = new Color(1f, 1f, 1f, 1f);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(400, 400, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // Main table that holds the title label at the top and a buttons table at the bottom
        Table mainTable = new Table();
        Table buttonsTable = new Table();

        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.align(Align.center);
        buttonsTable.align(Align.center);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFontColor);
        Label title = new Label("ALIFE TOURNAMENT", skin);
        title.setStyle(titleStyle);


        // Create buttons and their respective click listeners
        TextButton onlineButton = new TextButton("Online", skin);
        TextButton offlineButton = new TextButton("Offline", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);

        onlineButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.switchTo(new ConnectScreen(app));
            }
        });

        offlineButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new GameServer(OFFLINE_PORT); // server doesn't need to be stored
                // mimic login procedure
                try {
                    GameClient.getInstance().tryConnect("localhost", OFFLINE_PORT);
                    Thread.sleep(100);
                    Account account = new Account("", "");
                    GameClient.getInstance().client.sendTCP(new RegisterPacket(account));
                    Thread.sleep(100);
                    GameClient.getInstance().client.sendTCP(new LoginPacket(account));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

//                ScreenStack.push(new SimScreen(app, UserAccountPacket.createDefault(0)));
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        buttonsTable.row().pad(0, 0, 10, 0);
        buttonsTable.add(onlineButton).fill().uniform();
        buttonsTable.row().pad(0, 0, 10, 0);
        buttonsTable.add(offlineButton).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(exitButton).fill().uniform();

        mainTable.row().pad(40,0,50,0);
        mainTable.add(title);
        mainTable.row();
        mainTable.add(buttonsTable);


        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        if (GameClient.getInstance().user != null) {
            ScreenStack.switchTo(new MainMenuScreen(app));
        }
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(menuCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(25,25, menuViewport.getWorldWidth() - 50, menuViewport.getWorldHeight() - 50); // Why are these the values that produce a somewhat symmetrical result?
        app.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        menuCam.update();
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
