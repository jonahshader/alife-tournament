package com.csi4999.screens;

import com.badlogic.gdx.Game;
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
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.clientListeners.DescriptionListener;
import com.csi4999.systems.networking.clientListeners.RegisterFeedbackListener;
import com.csi4999.systems.networking.common.Account;
import com.csi4999.systems.networking.packets.*;
import com.esotericsoftware.kryonet.Client;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class LoginScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;
    private BitmapFont titleFont;
    private Color titleFontColor;

    public volatile boolean loginDoneQueue = false;
    public UserAccountPacket user;

    public LoginScreen(ALifeApp app) {
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

        TextField username = new TextField("username", skin);
        TextField password = new TextField("password", skin);
        Label accountResponse = new Label ("", skin);
        RegisterFeedbackListener.getInstance().accountResponse = accountResponse;
        RegisterFeedbackListener.getInstance().loginScreen = this;

        TextButton registerButton = new TextButton("register", skin);
        TextButton loginButton = new TextButton("login", skin);

        // Create buttons and their respective click listeners
        TextButton savedEntitiesButton = new TextButton("Saved", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);
        TextButton saveTestButton = new TextButton("Save Test", skin);

        savedEntitiesButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                ScreenStack.push(new SavedEntitiesScreen(app));
                Client client = GameClient.getInstance().client;
                client.sendTCP(new RequestSavedEntityDataPacket(1));
                while(! DescriptionListener.getInstance().ready){}
                ScreenStack.push(new SavedEntitiesScreen(app, DescriptionListener.getInstance().environmentDescriptions,
                    DescriptionListener.getInstance().creatureDescriptions));
            }
        });

        settingsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new SettingsScreen(app));


            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        loginButton.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                client.sendTCP(new LoginPacket(new Account(username.getText(), password.getText())));
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                client.sendTCP(new RegisterPacket(new Account(username.getText(), password.getText())));
            }
        });

        saveTestButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new SaveTestingScreen(app));
            }
        });


        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(username).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(password).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(loginButton).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(registerButton).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(savedEntitiesButton).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(saveTestButton).fill().uniform();
        buttonsTable.row().pad(30, 0, 0, 0);
        buttonsTable.add(accountResponse).fill().uniform();


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
        if (loginDoneQueue && user != null) {
            ScreenStack.push(new SimScreen(app, user));
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
