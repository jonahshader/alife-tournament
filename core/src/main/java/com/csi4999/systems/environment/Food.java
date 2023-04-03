package com.csi4999.systems.environment;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;
import java.util.Random;

public class Food extends Circle {
    private static final float BASE_ENERGY_TARGET = 100;
    private static final float BASE_ENERGY_TARGET_STD = 10f;
    private static final float RADIUS_PER_ENERGY_SQRT = 1.5f;
    private static final float GROW_RATE = 2f;
    private float targetEnergy;



    private float energy;
    private boolean growable = true;


    public Food() {}

    public Food(Vector2 position, Random rand) {
        super(position, 0f);
        targetEnergy = (float) Math.max(1.0, rand.nextGaussian(BASE_ENERGY_TARGET, BASE_ENERGY_TARGET_STD));
        energy = 1f;
        radius = energyToRadius(energy);
        color.set(rand.nextFloat() * .2f, rand.nextFloat() * .2f + .8f, rand.nextFloat() * .2f, 1f);
        computeTransform(null);
    }

    public Food(Vector2 position, Random rand, float initialEnergy, float targetEnergy) {
        super(position, 0f);
        this.targetEnergy = targetEnergy;
        energy = initialEnergy;
        radius = energyToRadius(energy);
        growable = false;
        color.set(Math.min(1f, (float) rand.nextGaussian() * .2f + 253/255f), Math.min(1f, (float) rand.nextGaussian() * .2f + 143/255f), Math.min(1f, (float) rand.nextGaussian() * .2f + 58/255f), 1f);
        computeTransform(null);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
//        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
//        shapeDrawer.filledCircle(0f, 0f, this.radius);
        Sprite circle = CustomGraphics.getInstance().circle;
        circle.setScale(radius * 2f / circle.getWidth());
        circle.setOriginBasedPosition(0f, 0f);
        circle.setColor(color);
        circle.draw(batch);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        if (!removeQueued) {
            if (growable)
                energy += Math.tanh(targetEnergy - energy) * dt * GROW_RATE;
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

    public float getEnergy() {
        return energy;
    }
}
