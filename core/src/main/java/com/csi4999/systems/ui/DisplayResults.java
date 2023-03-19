package com.csi4999.systems.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.screens.SimScreen;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.systems.networking.common.ChunkPerformance;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class DisplayResults {
    private static final int WIDTH = (int) (360 * 1.5f);
    private static final int HEIGHT = (int) (360 * 1.5f);

    private static final float BAR_WIDTH = 12f;

    private FitViewport viewport;

    private SimScreen sim;
    private BitmapFont font;

    public DisplayResults(SimScreen sim) {
        this.sim = sim;

        viewport = new FitViewport(WIDTH, HEIGHT);
        viewport.getCamera().position.setZero();
        viewport.getCamera().update();
        font = CustomAssetManager.getInstance().manager.get(UI_FONT);
    }

    public void render(ShapeDrawer drawer) {
        if (sim.tournamentResults != null) {
            drawer.getBatch().setProjectionMatrix(viewport.getCamera().combined);
            drawer.getBatch().begin();
            for (int i = 0; i < sim.tournamentResults.performances.size(); i++) {
                ChunkPerformance p = sim.tournamentResults.performances.get(i);
                float pos = (i / (sim.tournamentResults.performances.size() - 1f)) - .5f;

                float xPos = (-BAR_WIDTH/2) + pos * .8f * WIDTH;
                float yPos = (-HEIGHT/2f) + 40f;
                float barHeight = (HEIGHT/2f) - yPos - 40f;
                drawer.filledRectangle(xPos, yPos, BAR_WIDTH, barHeight * p.proportionOfWin, new Color(1f, 1f, 1f, 1f));
                font.draw(drawer.getBatch(), "Username somehow", xPos + BAR_WIDTH/2, yPos - 10f, 100f, Align.center, false);
            }
            drawer.getBatch().end();
        }

    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.getCamera().position.setZero();
        viewport.getCamera().update();
    }
}
