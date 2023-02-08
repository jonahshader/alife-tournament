package com.csi4999.systems;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class TestBall extends Circle {


    public TestBall(float radius) {
        super(radius);
    }

    public TestBall(float initial_pX, float initial_pY, float initial_vX, float initial_vY, float initial_aX, float initial_aY, float radius) {
        super(initial_pX, initial_pY, initial_vX, initial_vY, initial_aX, initial_aY, radius);
    }

    public TestBall(Vector2 position, Vector2 velocity, Vector2 acceleration, float radius) {
        super(position, velocity, acceleration, radius);
    }

    void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, this.radius);
    }

    @Override
    public void receiveActiveColliders(List<Collider> activeColliders) {

    }


}