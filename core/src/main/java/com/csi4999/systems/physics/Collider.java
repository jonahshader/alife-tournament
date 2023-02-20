package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.Mutable;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Collider extends PhysicsObject implements Mutable {
    private static final float SIMILARITY_MUTATE_STD = 0.05f;
    public static final int SIMILARITY_VECTOR_SIZE = 8;

    public List<Collider> collision;
    Rectangle bounds;
    protected float[] similarityVector;

    public boolean collidable = true;

    abstract public boolean collidesWith(Collider other);
    abstract public void computeBounds();
    abstract public void handleColliders();

    public Collider(){}

    public Collider(float initial_pX, float initial_pY, float initial_vX, float initial_vY, float initial_aX, float initial_aY) {
        super(initial_pX, initial_pY, initial_vX, initial_vY, initial_aX, initial_aY);
        this.bounds = new Rectangle();
        this.collision = new ArrayList<>();
    }

    public Collider(Vector2 position, Vector2 velocity, Vector2 acceleration) {
        super(position, velocity, acceleration);
        this.bounds = new Rectangle();
        this.collision = new ArrayList<>();
    }

    // default implementation
    public float[] getVisionVector() {
        return null;
    }

    // get similarity for another collider, dot product between both similarities, return -1 if one or both are null
    public float getSimilarity(Collider c) {
        float res = -1f;

        if (similarityVector != null && c.similarityVector != null) {
            res = 0;
            for (int i = 0; i < similarityVector.length; i++) {
                res += similarityVector[i] * c.similarityVector[i];
            }
        }

        return res;
    }

    public void renderBounds(ShapeDrawer d) {
        d.setColor(1f, .5f, .25f, 1f);
        d.rectangle(bounds);
        for (PhysicsObject o : getChildren()) {
            if (o instanceof Collider) {
                ((Collider) o).renderBounds(d);
            }
        }
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        super.move(dt, parent);
        computeBounds();
    }

    @Override
    public void mutate(float amount, Random rand) {
        if (similarityVector != null) {
            for (int i = 0; i < similarityVector.length; i++)
                similarityVector[i] += rand.nextGaussian() * amount * SIMILARITY_MUTATE_STD;
            normalizeSimilarity(similarityVector);
        }
    }

    public void normalizeSimilarity(float[] sim) {
        float sumsq = 0.0f;

        for (float num : sim) {
            sumsq += num * num;
        }

        float weight = 1.0f / (float) Math.sqrt(sumsq);

        for (int i = 0; i < sim.length; i++) {
            sim[i] *= weight;
        }
    }

    public synchronized void addCollider(Collider c) {
        collision.add(c);
    }

}
