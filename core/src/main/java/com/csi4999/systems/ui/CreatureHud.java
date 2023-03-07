package com.csi4999.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.screens.SimScreen;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.creature.Tool;

import java.util.HashMap;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class CreatureHud implements InputProcessor, Screen {
    private Skin skin;
    private Stage stage;

    private ExtendViewport viewport;
    private OrthographicCamera cam;
    private Creature c;

    private Table mainTable;

    private SimScreen sim;

    public CreatureHud(ExtendViewport viewport, OrthographicCamera cam, Batch batch) {
        this.viewport = viewport;
        this.cam = cam;

        //this.cam = new OrthographicCamera();
        //this.viewport = new ExtendViewport(360, 360, this.cam);

        this.skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        stage = new Stage(viewport, batch);
    }

    public CreatureHud(SimScreen sim, Batch batch) {
        this.sim = sim;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        cam = new OrthographicCamera();
        viewport = new ExtendViewport(450, 450, cam);
        stage = new Stage(viewport, batch);
        cam.position.set(0, 0, 0);
        cam.update();
    }

    public void updateCamera() {
        if (this.c != null) {
            cam.position.set(c.position, 0);
            cam.update();
            mainTable.setPosition(c.position.x + 50, c.position.y - 75);
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // zoom in/out, centered at creature
        float zoomScalar = (float) Math.pow(1.125f, amountY);
        Vector2 translation = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY())).sub(new Vector2(cam.position.x, cam.position.y));

        cam.translate(translation);
        cam.update();
        cam.zoom *= zoomScalar;
        cam.update();
        cam.translate(translation.scl(-1f * zoomScalar));
        return true;
    }

    @Override
    public void show() {
        mainTable = new Table();

        mainTable.setSize(150, 150);
        mainTable.setPosition(300, -80 + viewport.getWorldHeight()/2);
        mainTable.align(Align.right);

        Label title = new Label("Creature info", skin);

        TextButton saveButton = new TextButton("Save Creature", skin);

        //TODO: Need a click listener for save button and save creature functionality

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

        mainTable.row().center();
        mainTable.add(title).pad(0, 0, 10, 0).fill();
        mainTable.row().center();
        mainTable.add(toolsLabel).fill();
        mainTable.row().center();
        mainTable.add(sensorsLabel).fill();
        mainTable.row().center();
        mainTable.add(saveButton).pad(10, 0, 0, 0).fill();

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        stage.getBatch().setProjectionMatrix(cam.combined);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        cam.update();
        viewport.update(width, height);
        mainTable.setPosition(c.position.x,c.position.y);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {stage.dispose();}
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }



    public void assignCreature(Creature c) {
        this.c = c;
        cam.position.set(c.position, 0);
    }

    public void unassignCreature() {
        this.c = null;
        stage.dispose();
    }
}
