package com.csi4999.systems;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.Line;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class TestLine extends Line {

    public TestLine() {
    }

    public TestLine(Vector2 line) {
        super(line);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.line(0f, 0f, line.x, line.y);
    }

    @Override
    public void receiveActiveColliders(List<Collider> activeColliders) {

    }
}
