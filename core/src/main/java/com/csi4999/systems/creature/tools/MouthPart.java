package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MouthPart extends PhysicsObject {

    public MouthPart() {
        super(new Vector2().setZero(), new Vector2().setZero(), new Vector2().setZero());
        color.set(0.9f, 0.3f, 0.2f, 1f);

    }
    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color);
        shapeDrawer.filledTriangle(0f, 0f, 5f, 2f, 5f, 4f);
        shapeDrawer.filledTriangle(5f, 2f, 8f, 0f, 5f, 4f);
    }
}
