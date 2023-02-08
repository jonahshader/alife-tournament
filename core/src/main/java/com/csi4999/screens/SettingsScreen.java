package com.csi4999.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;

public class SettingsScreen implements Screen {
    private TextureAtlas atlas;
    private Skin skin;
    private final OrthographicCamera settingsCam;
    private final FitViewport settingsViewport;
    private final ALifeApp app;
    private Stage stage;

    public SettingsScreen(ALifeApp app) {
        this.app = app;

        atlas = new TextureAtlas("ui/neutralizer/skin/neutralizer-ui.atlas");
        skin = new Skin(Gdx.files.internal("ui/neutralizer/skin/neutralizer-ui.json"));

        settingsCam = new OrthographicCamera();
        settingsViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), settingsCam);

        settingsCam.position.set(settingsCam.viewportWidth/2, settingsCam.viewportHeight/2, 0);
        settingsCam.update();

        stage = new Stage(settingsViewport, app.batch);
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
        TextButton saveButton = new TextButton("Save Settings", skin);


        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        saveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Save whatever settings are changed
            }
        });


        mainTable.row().pad(0, 0, 50, 0);
        mainTable.add(saveButton).fill().uniform();
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
