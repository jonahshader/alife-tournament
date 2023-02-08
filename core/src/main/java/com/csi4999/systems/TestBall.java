package com.csi4999.systems;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TestBall extends PhysicsObject {
    public float radius = 5;
    public TestBall(float initial_vX, float initial_vY, float initial_aX, float initial_aY, float initial_pX, float initial_pY) {
        super(initial_vX, initial_vY, initial_aX, initial_aY, initial_pX, initial_pY);
    }

    public TestBall(Vector2 position, Vector2 velocity, Vector2 acceleration) {
        super(position, velocity, acceleration);
    }

    public TestBall() {
    }

    @Override
    void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, radius);
    }
}
