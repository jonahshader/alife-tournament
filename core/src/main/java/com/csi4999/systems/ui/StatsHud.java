package com.csi4999.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.csi4999.systems.environment.CreatureSpawner;
import com.csi4999.systems.environment.FoodSpawner;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class StatsHud implements InputProcessor {
    private static final int WIDTH = (int) (640 * 1.5f);
    private static final int HEIGHT = (int) (360 * 1.5f);
    private static final int SAMPLE_INTERVAL = 60;
    private boolean visible = false;
    private Plot creaturePopulation = new Plot(300, "Population");
    private Plot creatureEnergy = new Plot(300, "Total Creature Energy");
    private Plot foodEnergy = new Plot(300, "Total Food Energy");

    private CreatureSpawner creatureSpawner;
    private FoodSpawner foodSpawner;

    private int sampleCountdown = 0;

    private Viewport viewport = new FitViewport(WIDTH, HEIGHT);

    public StatsHud(CreatureSpawner creatureSpawner, FoodSpawner foodSpawner) {
        this.creatureSpawner = creatureSpawner;
        this.foodSpawner = foodSpawner;

        viewport.apply(true);
    }

    public void update() {
        if (sampleCountdown == 0) {
            sampleCountdown += SAMPLE_INTERVAL;
            creaturePopulation.addDatum(creatureSpawner.getCreatureCount());
            creatureEnergy.addDatum(creatureSpawner.getAllCreatureEnergy());
            foodEnergy.addDatum(foodSpawner.getAllFoodEnergy());
        }
        sampleCountdown--;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply(true);
    }

    public void render(ShapeDrawer drawer) {
        if (visible) {
            drawer.getBatch().setProjectionMatrix(viewport.getCamera().combined);
            drawer.getBatch().begin();
            float padding = 20;
            float w = WIDTH - (2 * padding);
            float h = HEIGHT - (2 * padding);
            creaturePopulation.draw(drawer, padding, padding, w, h/4, new Color(1f, 0.8f, 0.8f, 1f));
            creatureEnergy.draw(drawer, padding, (h/4) + padding*2, w, h/4, new Color(0.5f, 1f, 0.2f, 1f));
            foodEnergy.draw(drawer, padding, 2*(h/4) + padding*3, w, h/4, new Color(0.2f, 1f, 0.2f, 1f));
            drawer.getBatch().end();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.TAB) {
            visible = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.TAB) {
            visible = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
