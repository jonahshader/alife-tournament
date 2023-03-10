package com.csi4999.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import  com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.clientListeners.LoadListener;
import com.csi4999.systems.networking.common.SavedCreatureDescription;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;
import com.csi4999.systems.networking.packets.RequestEnvironmentPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryonet.Client;

import java.util.ArrayList;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class SavedEntitiesScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera savedEntitiesCam;
    private final FitViewport savedEntitiesViewport;
    private final ALifeApp app;
    private Stage stage;

    // set to true if no DB implemented
    private final boolean noDatabase = true;

    // dummy data
    private String[] creatureNames;
    private  String[] envNames;

    public java.util.List<SavedCreatureDescription> savedCreatures;

    public java.util.List<SavedEnvironmentDescription> savedEnvironments;

    public UserAccountPacket user;

    public SavedEntitiesScreen(ALifeApp app, java.util.List<SavedEnvironmentDescription> savedEnvironments,
                               java.util.List<SavedCreatureDescription> savedCreatures, UserAccountPacket user) {


        this.user = user;
        this.savedEnvironments = savedEnvironments;
        this.savedCreatures = savedCreatures;

        this.app = app;


        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        savedEntitiesCam = new OrthographicCamera();
        savedEntitiesViewport = new FitViewport(400, 400, savedEntitiesCam);

        savedEntitiesCam.position.set(savedEntitiesCam.viewportWidth/2, savedEntitiesCam.viewportHeight/2, 0);
        savedEntitiesCam.update();

        stage = new Stage(savedEntitiesViewport, app.batch);

        // fake data are created
        if (noDatabase) {
            creatureNames = new String[50];
            envNames = new String[50];
            for (int i = 0; i < 50; i ++) {
                creatureNames[i] = "Creature " + i;
                envNames[i] = "Environment " + i;
            }

        }
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // main table for the screen
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();

        //table for right side
        //only holds label with creature name for right now
        Table rightSide = new Table();

        //table of creatures
        Table creatures = new Table();
        for (String creature : creatureNames){
            TextButton creatureButton = new TextButton(creature, skin);
            creatureButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TO DO: design more complex layout for here
                    rightSide.clearChildren();
                    Label creatureName = new Label(creature, skin);
                    rightSide.add(creatureName).fill();
                }
            });
            creatures.add(creatureButton).expand().fillX();
            creatures.row();
        }

//        for (SavedCreatureDescription c : savedCreatures) {
//            TextButton creatureButton = new TextButton(c.name, skin);
//            creatureButton.addListener(new ClickListener(){
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    // TO DO: design more complex layout for here
//                    rightSide.clearChildren();
//                    Label creatureName = new Label(c.name, skin);
//                    rightSide.add(creatureName).fill();
//                    rightSide.row().pad(0, 0, 50, 0);
//                    rightSide.add(new Label(c.description, skin)).fill();
//                }
//            });
//            creatures.add(creatureButton).expand().fillX();
//            creatures.row();
//        }

        //table of environments
        Table environments = new Table();
//        for (String environment : envNames){
//            TextButton envButton = new TextButton(environment, skin);
//            envButton.addListener(new ClickListener(){
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    // TO DO: design more complex layout for here
//                    rightSide.clearChildren();
//                    Label envName = new Label(environment, skin);
//                    rightSide.add(envName).fill();
//                }
//            });
//            environments.add(envButton).expand().fillX();
//            environments.row();
//        }

        for (SavedEnvironmentDescription e : savedEnvironments) {
            TextButton envButton = new TextButton(e.name, skin);
            envButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TO DO: design more complex layout for here
                    rightSide.clearChildren();
                    Label envName = new Label(e.name, skin);
                    rightSide.add(envName).fill().top().center();
                    rightSide.row().pad(200, 0, 0, 0);
                    rightSide.row();
                    rightSide.row();
                    rightSide.row();
                    rightSide.add(new Label(e.description, skin)).fill().bottom();

                    TextButton loadButton = new TextButton("Load Environment" ,skin);
                    loadButton.addListener(new ClickListener(){




                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Client client = GameClient.getInstance().client;
                            client.sendTCP(new RequestEnvironmentPacket(e.environmentID));
                            while (!LoadListener.getInstance().ready){}

                            ScreenStack.push(new SimScreen(app, GameClient.getInstance().user, LoadListener.getInstance().environment));
                            LoadListener.getInstance().environment = null;
                            LoadListener.getInstance().ready = false;
                        }
                    });
                    rightSide.row();
                    rightSide.add(loadButton).uniform();
                }
            });
            environments.add(envButton).fillX().uniform();
            environments.row();
        }

        // provides scrolling for left side
        ScrollPane creatureList = new ScrollPane(creatures, skin);
        creatureList.setScrollBarPositions(true, false);

        // provides scrolling for left side
        ScrollPane envList = new ScrollPane(environments, skin);
        envList.setScrollBarPositions(true, false);

        //left side is stack of buttons
        Stack leftSide = new Stack();
        leftSide.addActor(creatureList);
        leftSide.addActor(envList);


//        SplitPane screenDivider = new SplitPane(leftSide, rightSide, false, skin);

        TextButton backButton = new TextButton("Go Back", skin);
        backButton.addListener(new ClickListener(){
                        @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        //buttons for toggling view
        TextButton viewCreatures = new TextButton("Saved Creatures", skin);
        TextButton viewEnvironments = new TextButton("Saved Environments", skin);


        mainTable.add(viewCreatures).fill().uniform();
        mainTable.add(viewEnvironments).fill().uniform();
        mainTable.row();
        mainTable.add(leftSide).expandY().top();
        mainTable.add(rightSide).expandY().top();
        mainTable.center();
//        mainTable.add(screenDivider).colspan(2).expandY();
        mainTable.row();
        mainTable.add(backButton).fill().colspan(2);

        //changes visibility on tabs
        ChangeListener tab_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                creatureList.setVisible(viewCreatures.isChecked());
                envList.setVisible(viewEnvironments.isChecked());
            }
        };
        viewCreatures.addListener(tab_listener);
        viewEnvironments.addListener(tab_listener);

        // ensures only one of creatures / env is shown at a time
        ButtonGroup tabs = new ButtonGroup();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(viewCreatures);
        tabs.add(viewEnvironments);




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
        savedEntitiesCam.update();
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
