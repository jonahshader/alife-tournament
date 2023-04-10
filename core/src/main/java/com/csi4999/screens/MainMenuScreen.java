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
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.clientListeners.DescriptionListener;
import com.csi4999.systems.networking.clientListeners.RankingResponseListener;
import com.csi4999.systems.networking.packets.RequestRankingsPacket;
import com.csi4999.systems.networking.packets.RequestSavedEntityDataPacket;
import com.esotericsoftware.kryonet.Client;
import jdk.javadoc.internal.doclint.Env;

import static com.csi4999.singletons.CustomAssetManager.*;

public class MainMenuScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;
    private BitmapFont titleFont;
    private Color titleFontColor;

    public MainMenuScreen(ALifeApp app) {
        this.app = app;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        titleFont = CustomAssetManager.getInstance().manager.get(TITLE_FONT);
        titleFontColor = new Color(1f, 1f, 1f, 1f);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(400, 400, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);

        Client client = GameClient.getInstance().client;
        client.sendTCP(new RequestRankingsPacket());
        while (!RankingResponseListener.getInstance().ready){}
        RankingResponseListener.getInstance().ready = false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        TextButton shopButton = new TextButton("Shop", skin);
        TextButton trainingButton = new TextButton("Play", skin);

        TextButton savedEntitiesButton = new TextButton("Load", skin);
        TextButton LeaderboardButton = new TextButton("Leaderboard", skin);
        TextButton infoButton = new TextButton("Info", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);

        trainingButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new GenerationScreen(app));
            }
        });

        shopButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new ShopScreen(app));
            }
        });

        savedEntitiesButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                client.sendTCP(new RequestSavedEntityDataPacket(GameClient.getInstance().user.userID));
                while (! DescriptionListener.getInstance().ready){}
                DescriptionListener.getInstance().ready = false;
                ScreenStack.push(new SavedEnvScreen(app, DescriptionListener.getInstance().environmentDescriptions));
            }
        });

        trainingButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new GenerationScreen(app));
            }
        });

        LeaderboardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new LeaderboardScreen(app));
            }
        });

        infoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                ScreenStack.push(new SettingsScreen(app));
                ScreenStack.push(new InfoScreen(app));
            }
        });


        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        buttonsTable.row().pad(0, 0, 10, 0);
        buttonsTable.add(trainingButton).fill().uniform();
        buttonsTable.row().pad(0, 0, 10, 0);
        buttonsTable.add(shopButton).fill().uniform();
        buttonsTable.row().pad(0, 0, 10, 0);
        buttonsTable.add(savedEntitiesButton).fill().uniform();
        buttonsTable.row().pad(0, 0, 10, 0);


        buttonsTable.add(LeaderboardButton).fill().uniform();
        buttonsTable.row().pad(0, 0, 10, 0);


        buttonsTable.add(infoButton).fill().uniform();


        if (GameClient.getInstance().user.is_admin) {

            TextButton saveTestButton = new TextButton("Save Test", skin);
            saveTestButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ScreenStack.push(new SaveTestingScreen(app));
                }
            });
            buttonsTable.row().pad(0, 0, 10, 0);
            buttonsTable.add(saveTestButton).fill().uniform();
            buttonsTable.row().pad(30, 0, 0, 0);
        }
        else
            buttonsTable.row().pad(0, 0, 10, 0);

        buttonsTable.add(exitButton).fill().uniform();

        mainTable.row().pad(40,0,25,0);
        mainTable.add(title);
        mainTable.row();
        mainTable.add(buttonsTable);


        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(menuCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(25,25, menuViewport.getWorldWidth() - 50, menuViewport.getWorldHeight() - 50);
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
