package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public abstract class Collider extends PhysicsObject {
    Rectangle bounds;
    abstract public boolean collidesWith(Collider other);
    abstract public void computeBounds();
    abstract public void receiveActiveColliders(List<Collider> activeColliders);

    public Collider(){}

    public Collider(float initial_pX, float initial_pY, float initial_vX, float initial_vY, float initial_aX, float initial_aY) {
        super(initial_pX, initial_pY, initial_vX, initial_vY, initial_aX, initial_aY);
        this.bounds = new Rectangle();
    }

    public Collider(Vector2 position, Vector2 velocity, Vector2 acceleration) {
        super(position, velocity, acceleration);
        this.bounds = new Rectangle();
    }

    // default implementation
    public float[] getVisionVector() {
        return null;
    }

    public void renderBounds(ShapeDrawer d) {
        d.setColor(1f, .5f, .25f, 1f);
        d.rectangle(bounds);
    }

    @Override
    public void move(float dt) {
        super.move(dt);
        computeBounds();
    }

    abstract void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha);
}
