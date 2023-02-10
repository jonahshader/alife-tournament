package com.csi4999.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;

public class SavedEntitiesScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera savedEntitiesCam;
    private final FitViewport savedEntitiesViewport;
    private final ALifeApp app;
    private Stage stage;
    public SavedEntitiesScreen(ALifeApp app) {
        this.app = app;

        skin = new Skin(Gdx.files.internal("ui/neutralizer/skin/neutralizer-ui.json"));

        savedEntitiesCam = new OrthographicCamera();
        savedEntitiesViewport = new FitViewport(400, 400, savedEntitiesCam);

        savedEntitiesCam.position.set(savedEntitiesCam.viewportWidth/2, savedEntitiesCam.viewportHeight/2, 0);
        savedEntitiesCam.update();

        stage = new Stage(savedEntitiesViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void show() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.align(Align.center);


        TextButton backButton = new TextButton("Go Back", skin);
        backButton.setColor(1f, 0f, 0f, 1f);
        TextButton option2 = new TextButton("Placeholder", skin);


        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        option2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new SimScreen(app));
            }
        });


        mainTable.row().pad(0, 0, 50, 0);
        mainTable.add(option2).fill().uniform();
        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(backButton).fill().uniform();

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
