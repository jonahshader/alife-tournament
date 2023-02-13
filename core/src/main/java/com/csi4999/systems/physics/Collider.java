package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Arrays;
import java.util.List;

public abstract class Collider extends PhysicsObject {
    Rectangle bounds;
    float[] similarityVector;

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

    // get similarity for another collider, dot product between both similarities, return -1 if one or both are null
    public float getSimilarity(float[] other) {
        float res = -1f;

        if (similarityVector != null && other != null) {
            normalizeSimilarity(other);
            normalizeSimilarity(similarityVector);
            res = 0;
            for (int i = 0; i < similarityVector.length; i++) {
                res += similarityVector[i] * other[i];
            }
        }

        return res;
    }

    public void setSimilarity(float[] sim) {
        similarityVector = sim;
    }

    public void renderBounds(ShapeDrawer d) {
        d.setColor(1f, .5f, .25f, 1f);
        d.rectangle(bounds);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        super.move(dt, parent);
        computeBounds();
    }

    private void normalizeSimilarity(float[] sim) {
        float sumsq = 0.0f;

        for (float num : sim) {
            sumsq += num * num;
        }

        float weight = 1.0f / (float) Math.sqrt(sumsq);

        for (int i = 0; i < sim.length; i++) {
            sim[i] *= weight;
        }
    }
}
