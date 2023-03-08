package com.csi4999.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.AppPreferences;

public class SettingsScreen extends AppPreferences implements Screen {
    private Skin skin;
    private final OrthographicCamera settingsCam;
    private final FitViewport settingsViewport;
    private final ALifeApp app;
    private Stage stage;
    private InputProcessor oldInputProcessor;

    public SettingsScreen(ALifeApp app) {
        this.app = app;

        skin = new Skin(Gdx.files.internal("ui/neutralizer/skin/neutralizer-ui.json"));

        settingsCam = new OrthographicCamera();
        settingsViewport = new FitViewport(400, 400, settingsCam);

        settingsCam.position.set(settingsCam.viewportWidth / 2, settingsCam.viewportHeight / 2, 0);
        settingsCam.update();

        stage = new Stage(settingsViewport, app.batch);
        oldInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.align(Align.center);

        // Button to go back to main menu
        TextButton backButton = new TextButton("Go Back", skin);
        backButton.setColor(1f, 0f, 0f, 1f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(oldInputProcessor);
                ScreenStack.pop();
            }
        });

        // Sliders and checkboxes for various settings
        final CheckBox fullscreenCheckbox = new CheckBox(null, skin);

        final Slider masterSlider = new Slider(0f, 1f, 0.1f, false, skin);
        final Slider musicSlider = new Slider(0f, 1f, 0.1f, false, skin);
        final Slider soundSlider = new Slider(0f, 1f, 0.1f, false, skin);
        final Slider hudSlider = new Slider(0f, 1f, 0.1f, false, skin);
        masterSlider.setValue(getMasterVolume());
        musicSlider.setValue(getMusicVolume());
        soundSlider.setValue(getSoundVolume());
        hudSlider.setValue(getHudScale());

        fullscreenCheckbox.setChecked(isFullscreenEnabled());
        masterSlider.addListener(event -> {
            setMasterVolume(masterSlider.getValue());
            return false;
        });
        musicSlider.addListener(event -> {
            setMusicVolume(musicSlider.getValue());
            return false;
        });
        soundSlider.addListener(event -> {
            setSoundVolume(soundSlider.getValue());
            return false;
        });
        hudSlider.addListener(event -> {
            setHudScale(hudSlider.getValue());
            return false;
        });
        fullscreenCheckbox.addListener(event -> {
            boolean enabled = fullscreenCheckbox.isChecked();
            setFullscreenEnabled(enabled);
            return false;
        });


        // Labels for settings added to the table
        Label masterVolume = new Label("Master Volume", skin);
        Label musicVolume = new Label("Music Volume", skin);
        Label soundVolume = new Label("Sound Volume", skin);
        Label fullscreenToggle = new Label("Toggle Fullscreen", skin);
        Label hudScale = new Label("HUD Scale", skin);


        mainTable.add(masterVolume);
        mainTable.add(masterSlider);
        mainTable.row().pad(10, 0, 0, 0);
        mainTable.add(musicVolume);
        mainTable.add(musicSlider);
        mainTable.row().pad(10, 0, 0, 0);
        mainTable.add(soundVolume);
        mainTable.add(soundSlider);
        mainTable.row().pad(10, 0, 0, 0);
        mainTable.add(hudScale);
        mainTable.add(hudSlider);
        mainTable.row().pad(10, 0, 0, 0);
        mainTable.add(fullscreenToggle);
        mainTable.add(fullscreenCheckbox);
        mainTable.row().pad(50, 0, 0, 0);
        mainTable.add(backButton);

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
        stage.dispose();
    }
}
