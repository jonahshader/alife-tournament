package com.csi4999.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;

import com.csi4999.systems.networking.packets.*;
import com.csi4999.systems.physics.PhysicsEngine;

import com.esotericsoftware.kryonet.Client;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class SaveTestingScreen implements Screen {

    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;
    private BitmapFont titleFont;
    private Color titleFontColor;


    public UserAccountPacket user;

    private List<SensorBuilder> sensorBuilders = new ArrayList<>();
    private List<ToolBuilder> toolBuilders = new ArrayList<>();
    private Random r;

    public SaveTestingScreen(ALifeApp app){
        this.app = app;

        this.r = new Random();

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

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);

        TextButton createAndSaveEnvButton = new TextButton("Create and Send New Environment", skin);
        TextButton updateEnvButton = new TextButton("Update an Existing Environment", skin);
        TextButton bogusEnvButton = new TextButton("Send Environment with bad id", skin);

        TextButton createAndSaveCreatureButton = new TextButton("Create and Send New Creature", skin);
        TextButton updateCreatureButton = new TextButton("Update an Existing Creature", skin);
        TextButton bogusCreatureButton = new TextButton("Send Creature with bad id", skin);

        TextButton updateAccountButton = new TextButton("Update an account", skin);
        TextButton getSaved = new TextButton("Get Saved", skin);

        getSaved.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                client.sendTCP(new RequestSavedEntityDataPacket(0));
            }
        });

        createAndSaveEnvButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Environment environment = new Environment(EnvProperties.makeTestDefault());
                environment.userID = 1;
                client.sendTCP(new SaveEnvironmentPacket(environment));
            }
        });

        updateEnvButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Environment environment = new Environment(EnvProperties.makeTestDefault());
                environment.userID = 1;
                environment.EnvironmentID = 1;

                client.sendTCP(new SaveEnvironmentPacket(environment));

            }
        });

        bogusEnvButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Environment environment = new Environment(EnvProperties.makeTestDefault());
                environment.userID = 1;
                // since there is no env with this id to update should trigger logic to reassign id to next pk
                environment.EnvironmentID = 1234;

                client.sendTCP(new SaveEnvironmentPacket(environment));

            }
        });

        createAndSaveCreatureButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Creature creature = new Creature(new Vector2((float) r.nextGaussian(0f, 512f), (float) r.nextGaussian(0f, 512f)),
                    sensorBuilders, toolBuilders, 4, 4, new PhysicsEngine(), r);
                creature.userID=1;
                client.sendTCP(new SaveCreaturePacket(creature));

            }
        });

        updateCreatureButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Creature creature = new Creature(new Vector2((float) r.nextGaussian(0f, 512f), (float) r.nextGaussian(0f, 512f)),
                    sensorBuilders, toolBuilders, 4, 4, new PhysicsEngine(), r);
                creature.userID=1;
                creature.creatureID = 1;
                client.sendTCP(new SaveCreaturePacket(creature));

            }
        });

        bogusCreatureButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                Creature creature = new Creature(new Vector2((float) r.nextGaussian(0f, 512f), (float) r.nextGaussian(0f, 512f)),
                    sensorBuilders, toolBuilders, 4, 4, new PhysicsEngine(), r);
                creature.userID=1;
                creature.creatureID = 1234;
                client.sendTCP(new SaveCreaturePacket(creature));

            }
        });

        updateAccountButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Client client = GameClient.getInstance().client;
                UserAccountPacket user = UserAccountPacket.createDefault(1);
                client.sendTCP(user);

            }
        });


        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });





        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(getSaved).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(createAndSaveEnvButton).fill().uniform();



        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(updateEnvButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(bogusEnvButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(createAndSaveCreatureButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(updateCreatureButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(bogusCreatureButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(updateAccountButton).fill().uniform();

        buttonsTable.row().pad(5, 0, 0, 0);
        buttonsTable.add(exitButton).fill().uniform();

        mainTable.row().pad(40,0,50,0);
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
