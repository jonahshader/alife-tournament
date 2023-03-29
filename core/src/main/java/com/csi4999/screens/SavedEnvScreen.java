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
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.clientListeners.LoadListener;
import com.csi4999.systems.networking.common.SavedEnvironmentDescription;
import com.csi4999.systems.networking.packets.RequestEnvironmentPacket;
import com.csi4999.systems.networking.packets.UserAccountPacket;
import com.esotericsoftware.kryonet.Client;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class SavedEnvScreen implements Screen {

    private Skin skin;
    private final OrthographicCamera savedEnvCam;
    private final FitViewport savedEnvViewport;
    private final ALifeApp app;
    private Stage stage;


    public java.util.List<SavedEnvironmentDescription> savedEnvironments;

    public UserAccountPacket user;

    public SavedEnvScreen(ALifeApp app, java.util.List<SavedEnvironmentDescription> savedEnvironments) {

        this.user = GameClient.getInstance().user;

        this.savedEnvironments = savedEnvironments;

        this.app = app;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        savedEnvCam = new OrthographicCamera();
        savedEnvViewport = new FitViewport(400, 400, savedEnvCam);

        savedEnvCam.position.set(savedEnvCam.viewportWidth/2, savedEnvCam.viewportHeight/2, 0);
        savedEnvCam.update();

        stage = new Stage(savedEnvViewport, app.batch);


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

        TextButton switchToCreatures = new TextButton("Switch to Saved Creatures", skin);
        Label title = new Label("Saved Environments", skin);

        mainTable.add(title).expandX().pad(0,0,20,0);
        mainTable.row();

        entityTable.add(new Label("Name:", skin)).width(100).pad(0,0,15, 50).uniform();
        entityTable.add(new Label("Description:", skin)).expandX().pad(0,0,15,0);
        entityTable.row();

        for (SavedEnvironmentDescription d : savedEnvironments) {

            Label environmentName = new Label(d.name, skin);
            environmentName.setEllipsis(true);
            Label environmentDescription;

            if (d.description.length() <= 25)
                environmentDescription = new Label(d.description,skin);
            else
                environmentDescription = new Label(d.description.substring(0,24),skin);

            environmentDescription.setAlignment(Align.left);

            TextButton loadButton = new TextButton("Load", skin);



//            entityTable.add(environmentName).width(Value.percentWidth(0.25f)).pad(0,0,0, 5).uniform();
            entityTable.add(environmentName).width(100).pad(0,0,0, 50).uniform();
            entityTable.add(environmentDescription).expandX().pad(0,0,0,0);
            entityTable.add(loadButton);
            entityTable.row();

            loadButton.addListener(new ClickListener(){

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Client client = GameClient.getInstance().client;
                    client.sendTCP(new RequestEnvironmentPacket(d.environmentID));
                    while (!LoadListener.getInstance().ready){}

                    ScreenStack.push(new SimScreen(app, GameClient.getInstance().user, LoadListener.getInstance().environment));
                    LoadListener.getInstance().environment = null;
                    LoadListener.getInstance().ready = false;
                }
            });

        }

        ScrollPane scroll = new ScrollPane(entityTable, skin);
        scroll.setScrollBarPositions(false, false);
        mainTable.add(scroll).fillX();
        mainTable.row();
        if (entityTable.getRows() == 0)
            mainTable.add(switchToCreatures).expandX().pad(100,0,0,0);
        else
            mainTable.add(switchToCreatures).expandX();

        TextButton backButton = new TextButton("Go Back", skin);
        mainTable.row();
        mainTable.add(backButton).expandX();


        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        switchToCreatures.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new SavedCreatureScreen(app));
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
        savedEnvCam.update();
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
