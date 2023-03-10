package com.csi4999.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.csi4999.ALifeApp;
import com.csi4999.screens.NameDescriptionScreen;
import com.csi4999.screens.SimScreen;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.packets.SaveCreaturePacket;

import java.util.HashMap;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class CreatureHud implements InputProcessor, Screen {
    private static final int WIDTH = (int) (640 * 1.5f);
    private static final int HEIGHT = (int) (360 * 1.5f);

    private Skin skin;
    private OrthographicCamera myCam;
    private OrthographicCamera worldCam;
    private ExtendViewport myViewport;

    public Stage stage;
    private Table mainTable;
    private Creature c;
    private ALifeApp app;
    private Environment env;


    public CreatureHud(Batch batch, OrthographicCamera worldCam, ALifeApp app, Environment env) {
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        this.app = app;
        this.worldCam = worldCam;
        this.env = env;

        myCam = new OrthographicCamera();
        myViewport = new ExtendViewport(WIDTH, HEIGHT, myCam);

        stage = new Stage(myViewport, batch);
    }

    public void updateCamera() {
        if (this.c != null) {
//            worldCam.setToOrtho(false);
            worldCam.position.set(c.position, 0);
//            worldCam.rotate(-c.rotationDegrees);
            worldCam.update();
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void show() {
        stage.clear();
        mainTable = new Table();

        mainTable.setSize(120, 150);
        mainTable.setPosition(0, 0);

        mainTable.align(Align.bottomLeft);

        Label title = new Label("Creature Info", skin);

        TextButton saveButton = new TextButton("Save Creature", skin);


        saveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NameDescriptionScreen.NameDescriptionCallback n = (name, description) -> {
                    c.creatureName = name;
                    c.creatureDescription = description;
                    GameClient.getInstance().client.sendTCP(new SaveCreaturePacket(c));
                };
                ScreenStack.push(new NameDescriptionScreen(app, "Save Creature", "Save", n));
            }
        });



        HashMap<String, Integer> tools = new HashMap<>();
        HashMap<String, Integer> sensors = new HashMap<>();

        // Get tools/sensors for a creature and the number present on that creature
        for (Tool t : c.getTools()) {
            tools.put(t.getClass().getSimpleName(),
                tools.getOrDefault(t.getClass().getSimpleName(), 0) + 1);
        }

        for (Sensor s : c.getSensors()) {
            sensors.put(s.getClass().getSimpleName(),
                sensors.getOrDefault(s.getClass().getSimpleName(), 0) + 1);
        }


        StringBuilder toolsInfo = new StringBuilder();
        StringBuilder sensorsInfo = new StringBuilder();

        for (String name : tools.keySet()) {
            toolsInfo.append(name + ": " + tools.get(name) + "\n");
        }

        for (String name : sensors.keySet()) {
            sensorsInfo.append(name + ": " + sensors.get(name) + "\n");
        }

        Label toolsLabel = new Label(toolsInfo.toString(), skin);
        Label sensorsLabel = new Label(sensorsInfo.toString(), skin);

        int pad = 4;
        mainTable.row().center();
        mainTable.add(title).fill().uniformX().pad(pad);
        mainTable.row().center();
        mainTable.add(toolsLabel).fill().uniformX().pad(pad);
        mainTable.row().center();
        mainTable.add(sensorsLabel).fill().uniformX().pad(pad);
        mainTable.row().center();
        mainTable.add(saveButton).fill().uniformX().pad(pad);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        if (c != null) {
            myViewport.apply();
            stage.getBatch().setProjectionMatrix(myCam.combined);
            app.batch.begin();
            app.shapeDrawer.filledRectangle(0f, 0f, 120f, 150f, new Color(0f, 0f, 0f, 0.5f));
            app.batch.end();
            stage.act();
            stage.draw();
            if (c.removeQueued) {
                stage.clear();
                c = null;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        myViewport.update(width, height, true);
        if (c != null) {
            mainTable.setPosition(0, 0);
        }
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {
    }
    @Override
    public void dispose() {stage.dispose();}
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE) {
            Vector3 pos = worldCam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            assignCreature(env.getCreature((int) pos.x, (int) pos.y));
            return true;
        } else if (button == Input.Buttons.LEFT) {
            unassignCreature();
        }
        return false;
    }
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return c != null;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    public void assignCreature(Creature c) {
        this.c = c;
        myCam.position.set(myCam.viewportWidth/2, myCam.viewportHeight/2, 0);
        show();
    }

    public void unassignCreature() {
        this.c = null;
    }
}
