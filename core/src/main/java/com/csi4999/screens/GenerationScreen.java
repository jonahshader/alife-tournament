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
import com.csi4999.systems.networking.packets.RequestSavedEntityDataPacket;
import com.esotericsoftware.kryonet.Client;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class GenerationScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;

    private EnvProperties properties;

    public GenerationScreen(ALifeApp app) {
        this.app = app;
        this.properties = EnvProperties.makeTestDefault();

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(400, 400, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table mainTable = new Table();

        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.align(Align.center);


        TextButton continueButton = new TextButton("Continue", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);

        Slider mutationRateSlider = new Slider(0, 4, 0.1f, false, skin);
        Label mutationRateLabel = new Label("Mutation Rate", skin);
        Label mutationRateValueLabel = new Label("0", skin);


        Slider initialCreaturesSlider = new Slider(0, 1000, 1f, false, skin);
        Label initialCreaturesLabel = new Label("Initial Population", skin);
        Label initialCreaturesValueLabel = new Label("0", skin);

        Slider initialFoodSlider = new Slider(0, 10000, 1f, false, skin);
        Label initialFoodLabel = new Label("Initial Food", skin);
        Label initialFoodValueLabel = new Label("0", skin);
        Slider foodDeviationSlider = new Slider(0, 1024, 1f, false, skin);
        Label foodDeviationLabel = new Label("Food Deviation", skin);
        Label foodDeviationValueLabel = new Label("0", skin);

        Slider sensorSlider = new Slider(0, 24, 1, false, skin);
        Label sensorLabel = new Label("Max Sensors", skin);
        Label sensorValueLabel = new Label("0", skin);
        Slider toolSlider = new Slider(0, 24, 1, false, skin);
        Label toolLabel = new Label("Max Tools", skin);
        Label toolValueLabel = new Label("0", skin);


        // Select which components can be used here too?

        mutationRateSlider.addListener(event -> {
            float mutationRate = mutationRateSlider.getValue();
            properties.mutationRate = mutationRate;
            mutationRateValueLabel.setText(String.valueOf(((int)(mutationRate * 100))/ 100f));
            return false;
        });
        initialCreaturesSlider.addListener(event -> {
            int initialCreatures = (int) initialCreaturesSlider.getValue();
            properties.initialCreatures = initialCreatures;
            initialCreaturesValueLabel.setText(String.valueOf(initialCreatures));
            return false;
        });
        initialFoodSlider.addListener(event -> {
            int initialFood = (int) initialFoodSlider.getValue();
            properties.initialFood = initialFood;
            initialFoodValueLabel.setText(String.valueOf(initialFood));
            return false;
        });
        foodDeviationSlider.addListener(event -> {
            int foodStd = (int) foodDeviationSlider.getValue();
            properties.foodSpawnStd = foodStd;
            foodDeviationValueLabel.setText(String.valueOf(foodStd));
            return false;
        });
        sensorSlider.addListener(event -> {
            int maxSensors = (int) sensorSlider.getValue();
            properties.maxSensors = maxSensors;
            sensorValueLabel.setText(String.valueOf(maxSensors));
            return false;
        });
        toolSlider.addListener(event -> {
            int maxTools = (int) toolSlider.getValue();
            properties.maxTools = maxTools;
            toolValueLabel.setText(String.valueOf(maxTools));
            return false;
        });


        continueButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.push(new SimScreen(app, GameClient.getInstance().user, properties));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        float width = 50f;


        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(mutationRateLabel).align(Align.left);
        mainTable.add(mutationRateSlider);
        mainTable.add(mutationRateValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(initialCreaturesLabel).align(Align.left);
        mainTable.add(initialCreaturesSlider);
        mainTable.add(initialCreaturesValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(initialFoodLabel).align(Align.left);
        mainTable.add(initialFoodSlider);
        mainTable.add(initialFoodValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(foodDeviationLabel).align(Align.left);
        mainTable.add(foodDeviationSlider);
        mainTable.add(foodDeviationValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(sensorLabel).align(Align.left);
        mainTable.add(sensorSlider);
        mainTable.add(sensorValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(toolLabel).align(Align.left);
        mainTable.add(toolSlider);
        mainTable.add(toolValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);


        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(continueButton).fill().align(Align.center);
        mainTable.row().pad(0, 0, 10, 0);
        mainTable.add(exitButton).fill().align(Align.center);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(menuCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(15,15, menuViewport.getWorldWidth() - 35, menuViewport.getWorldHeight() - 35); // Why are these the values that produce a somewhat symmetrical result?
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
