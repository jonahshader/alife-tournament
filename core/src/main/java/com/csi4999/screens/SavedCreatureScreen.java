package com.csi4999.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.clientListeners.DescriptionListener;
import com.csi4999.systems.networking.clientListeners.LoadListener;
import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.packets.RequestCreaturePacket;
import com.csi4999.systems.networking.packets.RequestEnvironmentPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.csi4999.systems.networking.serverlisteners.LoadCreatureListener;
import com.esotericsoftware.kryonet.Client;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class SavedCreatureScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera savedCreaturesCam;
    private final FitViewport savedCreaturesViewport;
    private final ALifeApp app;
    private Stage stage;


    public java.util.List<SavedCreatureDescription> savedCreatures;

    public UserAccountPacket user;

    public SavedCreatureScreen(ALifeApp app) {
        this.user = GameClient.getInstance().user;

        this.savedCreatures = DescriptionListener.getInstance().creatureDescriptions;

        this.app = app;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        savedCreaturesCam = new OrthographicCamera();
        savedCreaturesViewport = new FitViewport(400, 400, savedCreaturesCam);

        savedCreaturesCam.position.set(savedCreaturesCam.viewportWidth/2, savedCreaturesCam.viewportHeight/2, 0);
        savedCreaturesCam.update();

        stage = new Stage(savedCreaturesViewport, app.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table mainTable = new Table();
        Table entityTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.align(Align.center);
        mainTable.align(Align.top);
        entityTable.align(Align.center);

        TextButton switchToEnvironments = new TextButton("Switch to Saved Environments", skin);
        Label title = new Label("Saved Creatures", skin);

        mainTable.add(title).expandX().pad(0,0,0,0);
        mainTable.row();

        for (SavedCreatureDescription d : savedCreatures) {
            Label creatureName = new Label(d.name, skin);
            Label creatureDescription;

            if (d.description.length() <= 25)
                creatureDescription = new Label(d.description,skin);
            else
                creatureDescription = new Label(d.description.substring(0,24),skin);

            TextButton loadButton = new TextButton("Load", skin);

            loadButton.addListener(new ClickListener(){

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Client client = GameClient.getInstance().client;
                    client.sendTCP(new RequestCreaturePacket(d.creatureID));
                    while (!LoadListener.getInstance().ready){}

                    ScreenStack.push(new SimScreen(app, GameClient.getInstance().user, new EnvProperties(), LoadListener.getInstance().creature));
                    LoadListener.getInstance().creature = null;
                    LoadListener.getInstance().ready = false;
                }
            });

            entityTable.add(creatureName).width(100).pad(0,0,0, 50).uniform();
            entityTable.add(creatureDescription).expandX().pad(0,0,0,0);
            entityTable.add(loadButton);
            entityTable.row();
        }

        ScrollPane scroll = new ScrollPane(entityTable, skin);
        scroll.setScrollBarPositions(false, false);
        mainTable.add(scroll).fillX();
        mainTable.row();
        if (entityTable.getRows() == 0)
            mainTable.add(switchToEnvironments).expandX().pad(100,0,0,0);
        else
            mainTable.add(switchToEnvironments).expandX();

        TextButton backButton = new TextButton("Go Back", skin);
        mainTable.row();
        mainTable.add(backButton).expandX();


        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        switchToEnvironments.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new SavedEnvScreen(app, DescriptionListener.getInstance().environmentDescriptions));
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
        savedCreaturesCam.update();
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

    }
}
