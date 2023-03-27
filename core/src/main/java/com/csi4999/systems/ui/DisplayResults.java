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

    private ExtendViewport viewport;

    private SimScreen sim;
    private BitmapFont font;

    public DisplayResults(SimScreen sim) {
        this.sim = sim;

        viewport = new ExtendViewport(WIDTH, HEIGHT);
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
                float yPos = (-HEIGHT/2f) + 60f;
                float barHeight = (HEIGHT/2f) - yPos - 60f;
                drawer.filledRectangle(xPos, yPos, BAR_WIDTH, barHeight, new Color(0f, 0f, 0f, 0.5f));
                drawer.filledRectangle(xPos, yPos, BAR_WIDTH, barHeight * p.proportionOfWin, new Color(1f, 1f, 1f, 1f));
                font.draw(drawer.getBatch(), sim.chunkNames.get(i), xPos + BAR_WIDTH/2, yPos - 10f, 0f, Align.center, false);
                font.draw(drawer.getBatch(), Float.toString(sim.initialRanks.get(i)), xPos + BAR_WIDTH/2, yPos - 25f, 0f, Align.center, false);
                if (sim.newRanksPacket != null) {
                    float newRank = sim.newRanksPacket.ranks.get(i);
                    float oldRank = sim.initialRanks.get(i);
                    float diff = newRank - oldRank;
                    if (diff > 0) {
                        drawer.getBatch().setColor(new Color(0f, 1f, 0f, 1f));
                    } else {
                        drawer.getBatch().setColor(new Color(1f, 0f, 0f, 1f));
                    }
                    font.draw(drawer.getBatch(), diff > 0 ? "+" : "" + diff, xPos + BAR_WIDTH/2, yPos - 40f, 0f, Align.center, false);
                    drawer.getBatch().setColor(new Color(1f, 1f, 1f, 1f));

                }


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
