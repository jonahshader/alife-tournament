package com.csi4999.systems.environment;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;
import java.util.Random;

public class Food extends Circle {
    private static final float BASE_ENERGY_TARGET = 60;
    private static final float BASE_ENERGY_TARGET_STD = 10f;
    private static final float RADIUS_PER_ENERGY_SQRT = 1.25f;
    private float targetEnergy;
    private float energy;


    public Food() {}

    public Food(Vector2 position, Random rand) {
        super(position, 0f);
        targetEnergy = (float) Math.max(1.0, rand.nextGaussian(BASE_ENERGY_TARGET, BASE_ENERGY_TARGET_STD));
        energy = 1f;
        color.set(rand.nextFloat() * .2f, rand.nextFloat() * .2f + .8f, rand.nextFloat() * .2f, 1f);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, this.radius);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        if (!removeQueued) {
            energy += Math.tanh(targetEnergy - energy) * dt;
            radius = energyToRadius(energy);
            super.move(dt, parent);
        }
    }

    @Override
    public void handleColliders() {
    }

    @Override
    public boolean collidesWith(Collider other) {
        return false;
    }

    public float eat(float amount) {
        if (energy > amount) {
            energy -= amount;
            return amount;
        } else {
            float tempEnergy = energy;
            energy = 0;
            radius = 0;
            queueRemoval();
            return tempEnergy;
        }
    }

    private float energyToRadius(float energy) {
        return (float) (Math.sqrt(energy) * RADIUS_PER_ENERGY_SQRT);
    }

    public void growFully() {
        energy = targetEnergy;
    }
}
