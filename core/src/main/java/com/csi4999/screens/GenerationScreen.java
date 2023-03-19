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
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.FlagellaBuilder;
import com.csi4999.systems.creature.tools.HornBuilder;
import com.csi4999.systems.creature.tools.MouthBuilder;
import com.csi4999.systems.environment.EnvProperties;
import com.csi4999.systems.networking.GameClient;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class GenerationScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;

    private EnvProperties properties;

    // Booleans for checking whether the user has these components unlocked
    private boolean eyeAllowed = false;
    private boolean mouthAllowed = false;
    private boolean flagellaAllowed = false;
    private boolean hornAllowed = false;

    public GenerationScreen(ALifeApp app) {
        this.app = app;
        this.properties = new EnvProperties();
        properties.minSensors = 1;
        properties.minTools = 1;
        properties.maxSensors = (int) GameClient.getInstance().user.numberOfSensors;
        properties.maxTools = (int) GameClient.getInstance().user.numberOfTools;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(1280, 720, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table mainTable = new Table();
        Table slidersTable = new Table();
        Table componentsTable = new Table();

        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.align(Align.center);

        CheckBox eyeCheckbox = new CheckBox("Eye", skin);
        CheckBox mouthCheckbox = new CheckBox("Mouth", skin);
        CheckBox flagellaCheckbox = new CheckBox("Flagella", skin);
        CheckBox hornCheckbox = new CheckBox("Horn", skin);

        // Booleans for checking whether the checkboxes are enabled
        // Creature components are built based on which of these are true
        AtomicBoolean eyeEnabled = new AtomicBoolean(false);
        AtomicBoolean mouthEnabled = new AtomicBoolean(false);
        AtomicBoolean flagellaEnabled = new AtomicBoolean(false);
        AtomicBoolean hornEnabled = new AtomicBoolean(false);
        eyeCheckbox.setChecked(false);
        mouthCheckbox.setChecked(false);
        flagellaCheckbox.setChecked(false);
        hornCheckbox.setChecked(false);

        // Checking whether the builders for each component are present for user account and automatically enabling
        for (SensorBuilder s: GameClient.getInstance().user.sensorBuilders) {
            if (s instanceof EyeBuilder) {
                eyeCheckbox.setChecked(true);
                eyeAllowed = true;
                eyeEnabled.set(true);
            }
        }
        for (ToolBuilder t: GameClient.getInstance().user.toolBuilders) {
            if (t instanceof MouthBuilder) {
                mouthCheckbox.setChecked(true);
                mouthAllowed = true;
                mouthEnabled.set(true);
            } else if (t instanceof FlagellaBuilder) {
                flagellaCheckbox.setChecked(true);
                flagellaAllowed = true;
                flagellaEnabled.set(true);
            } else if (t instanceof HornBuilder) {
                hornCheckbox.setChecked(true);
                hornAllowed = true;
                hornEnabled.set(true);
            }
        }

        if (!eyeAllowed) eyeCheckbox.setColor(1, 0, 0, 1);
        if (!mouthAllowed) mouthCheckbox.setColor(1, 0, 0, 1);
        if (!flagellaAllowed) flagellaCheckbox.setColor(1, 0, 0, 1);
        if (!hornAllowed) hornCheckbox.setColor(0f, 0f, 0f, .5f);

        // Sliders //
        // Mutation Rate
        Slider mutationRateSlider = new Slider(0, 4, 0.1f, false, skin);
        mutationRateSlider.setValue(properties.globalMutationRate);
        Label mutationRateLabel = new Label("Mutation Rate", skin);
        Label mutationRateValueLabel = new Label(String.valueOf(properties.globalMutationRate), skin);
        mutationRateLabel.setAlignment(Align.top);

        // Initial Creatures
        Slider initialCreaturesSlider = new Slider(0, 1000, 1, false, skin);
        initialCreaturesSlider.setValue(properties.initialCreatures);
        Label initialCreaturesLabel = new Label("Initial Population", skin);
        Label initialCreaturesValueLabel = new Label(String.valueOf(properties.initialCreatures), skin);
        initialCreaturesValueLabel.setAlignment(Align.top);

        // Initial Food
        Slider initialFoodSlider = new Slider(0, 10000, 1, false, skin);
        initialFoodSlider.setValue(properties.initialFood);
        Label initialFoodLabel = new Label("Initial Food", skin);
        Label initialFoodValueLabel = new Label(String.valueOf(properties.initialFood), skin);
        initialFoodValueLabel.setAlignment(Align.top);

        // Food Spread
        Slider foodDeviationSlider = new Slider(0, 8192, 1, false, skin);
        foodDeviationSlider.setValue(properties.foodSpawnStd);
        Label foodDeviationLabel = new Label("Food Deviation", skin);
        Label foodDeviationValueLabel = new Label(String.valueOf(properties.foodSpawnStd), skin);
        foodDeviationValueLabel.setAlignment(Align.top);

        // Sensors
        Slider sensorSlider = new Slider(properties.minSensors, properties.maxSensors, 1, false, skin);
        sensorSlider.setValue(properties.maxSensors);
        Label sensorLabel = new Label("Max Sensors", skin);
        Label sensorValueLabel = new Label(String.valueOf(properties.maxSensors), skin);
        sensorValueLabel.setAlignment(Align.top);

        // Tools
        Slider toolSlider = new Slider(properties.minTools, properties.maxTools, 1, false, skin);
        toolSlider.setValue(properties.maxTools);
        Label toolLabel = new Label("Max Tools", skin);
        Label toolValueLabel = new Label(String.valueOf(properties.maxTools), skin);

        // Buttons
        TextButton continueButton = new TextButton("Continue", skin);
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.setColor(1f, 0f, 0f, 1f);

        // Slider listeners
        mutationRateSlider.addListener(event -> {
            float mutationRate = mutationRateSlider.getValue();
            properties.globalMutationRate = mutationRate;
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

        // Checkbox listeners
        eyeCheckbox.addListener(event -> {
            if (eyeAllowed) {
                eyeEnabled.set(eyeCheckbox.isChecked());
            } else {
                eyeCheckbox.setChecked(false);
            }
            return false;
        });
        mouthCheckbox.addListener(event -> {
            if (mouthAllowed) {
                mouthEnabled.set(mouthCheckbox.isChecked());
            } else {
                mouthCheckbox.setChecked(false);
            }
            return false;
        });
        flagellaCheckbox.addListener(event -> {
            if (flagellaAllowed) {
                flagellaEnabled.set(flagellaCheckbox.isChecked());
            } else {
                flagellaCheckbox.setChecked(false);
            }
            return false;
        });
        hornCheckbox.addListener(event -> {
            if (hornAllowed) {
                hornEnabled.set(hornCheckbox.isChecked());
            } else {
                hornCheckbox.setChecked(false);
            }
            return false;
        });

        // Button listeners
        continueButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Continue button adds the components all at once based on booleans and goes to next screen
                if (eyeEnabled.get()) {
                    properties.sensorBuilders.add(new EyeBuilder());
                }
                if (mouthEnabled.get()) {
                    properties.toolBuilders.add(new MouthBuilder());
                }
                if (flagellaEnabled.get()) {
                    properties.toolBuilders.add(new FlagellaBuilder());
                }
                if (hornEnabled.get()) {
                    properties.toolBuilders.add(new HornBuilder());
                }
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

        componentsTable.row().pad(0,10,10,0);
        componentsTable.add(eyeCheckbox).align(Align.left);
        componentsTable.row().pad(0,10,10,0);
        componentsTable.add(flagellaCheckbox).align(Align.left);
        componentsTable.row().pad(0,10,10,0);
        componentsTable.add(mouthCheckbox).align(Align.left);
        componentsTable.row().pad(0,10,10,0);
        componentsTable.add(hornCheckbox).align(Align.left);


        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(mutationRateLabel).align(Align.left);
        slidersTable.add(mutationRateSlider);
        slidersTable.add(mutationRateValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(initialCreaturesLabel).align(Align.left);
        slidersTable.add(initialCreaturesSlider);
        slidersTable.add(initialCreaturesValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(initialFoodLabel).align(Align.left);
        slidersTable.add(initialFoodSlider);
        slidersTable.add(initialFoodValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(foodDeviationLabel).align(Align.left);
        slidersTable.add(foodDeviationSlider);
        slidersTable.add(foodDeviationValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(sensorLabel).align(Align.left);
        slidersTable.add(sensorSlider);
        slidersTable.add(sensorValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);

        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(toolLabel).align(Align.left);
        slidersTable.add(toolSlider);
        slidersTable.add(toolValueLabel).pad(0, 10f, 0, 0).align(Align.center).width(width);


        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(continueButton).fill().align(Align.center);
        slidersTable.row().pad(0, 0, 10, 0);
        slidersTable.add(exitButton).fill().align(Align.center);

        mainTable.add(slidersTable);
        mainTable.add(componentsTable).top();

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
