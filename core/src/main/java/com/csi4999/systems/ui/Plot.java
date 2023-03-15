package com.csi4999.systems.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.CustomGraphics;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.csi4999.singletons.CustomAssetManager.TITLE_FONT;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;
import static com.csi4999.systems.CustomMath.*;

public class Plot {
    private List<Float> plot = new ArrayList<>();
    private int maxHistory;
    private ReentrantLock l = new ReentrantLock();

    private BitmapFont font;

    private String title;

    public Plot(int maxHistory, String title) {
        this.maxHistory = maxHistory;
        this.title = title;
        font = CustomAssetManager.getInstance().manager.get(UI_FONT);
    }

    public void addDatum(float datum) {
        l.lock();
        plot.add(datum);
        if (plot.size() > maxHistory)
            plot.remove(0);
        l.unlock();
    }

    public void draw(ShapeDrawer drawer, float x, float y, float width, float height, Color plotColor) {
        drawer.filledRectangle(x, y, width, height, new Color(0f, 0f, 0f, 0.5f));
//        float min = findMin(plot, 0f);
        l.lock();
        float min = 0;
        float max = findMax(plot, 1f);
        drawer.setColor(plotColor);


        for (int i = 0; i < plot.size()-1; i++) {
            float yLeft = map(plot.get(i), min, max, y, y + height);
            float yRight = map(plot.get(i+1), min, max, y, y + height);
            float xLeft = map(i, 0, plot.size(), x, x + width);
            float xRight = map(i+1, 0, plot.size(), x, x + width);
            drawer.line(xLeft, yLeft, xRight, yRight);
        }
        font.draw(drawer.getBatch(), Float.toString(max), x, y + height);
        font.draw(drawer.getBatch(), title, x + width/2, y + height);
        l.unlock();

    }


}
