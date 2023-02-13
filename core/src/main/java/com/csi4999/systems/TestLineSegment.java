package com.csi4999.systems;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.LineSegment;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class TestLineSegment extends LineSegment {
    /**
     * An example of a concrete class that extends LineSegment, implementing the necessary methods.
     * Changes the color of colliders for demonstration.
     */

    public TestLineSegment() {
    }

    public TestLineSegment(float lineLength) {
        super(lineLength);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.line(0f, 0f, lineLength, 0f);
    }

    @Override
    public void receiveActiveColliders(List<Collider> activeColliders) {
        for (Collider activeCollider : activeColliders) {
            activeCollider.color.g = 1f;
        }
    }
}
