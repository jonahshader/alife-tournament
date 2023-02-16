package com.csi4999.systems.environment;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class Food extends Circle {
    // Basically just a testball right now
    public Food(Vector2 position, Vector2 velocity, Vector2 acceleration, float radius) {
        super(position, velocity, acceleration, radius);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, this.radius);
    }

    @Override
    public void handleColliders() {
        if (collision.size() > 0) {
            radius -= 0.05f;
            color.r = 1f;
            color.g = 0f;
        } else {
            radius += 0.01f;
            color.r = 0f;
            color.g = 1f;
        }
        color.b -= 0.01f;
        color.b = Math.max(color.b, 0f);
        radius = Math.max(radius, 2f);
    }
}
