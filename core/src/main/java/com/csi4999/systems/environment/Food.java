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
    private static final float BASE_ENERGY_TARGET = 20;
    private static final float BASE_ENERGY_TARGET_STD = 5f;
    private static final float RADIUS_PER_ENERGY_SQRT = 2f;
    private float targetEnergy;
    private float energy;

    private boolean alive;

    public Food() {}

    public Food(Vector2 position, Random rand) {
        super(position, 0f);
        targetEnergy = (float) Math.max(1.0, rand.nextGaussian(BASE_ENERGY_TARGET, BASE_ENERGY_TARGET_STD));
        energy = 1f;
        color.set(rand.nextFloat() * .2f, rand.nextFloat() * .2f + .8f, rand.nextFloat() * .2f, 1f);
        alive = true;
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, this.radius);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        if (alive) {
            energy += Math.tanh(targetEnergy - energy) * dt;
            radius = energyToRadius(energy);
            super.move(dt, parent);
        }
    }

    @Override
    public void handleColliders() {
        // TODO: get pushed away? implement push system and use it here?
//        if (collision.size() > 0) {
//            radius -= 0.05f;
//            color.r = 1f;
//            color.g = 0f;
//        } else {
//            radius += 0.01f;
//            color.r = 0f;
//            color.g = 1f;
//        }
//        color.b -= 0.01f;
//        color.b = Math.max(color.b, 0f);
//        radius = Math.max(radius, 2f);
    }

    public float eat(float amount) {
        if (energy > amount) {
            energy -= amount;
            return amount;
        } else {
            float tempEnergy = energy;
            energy = 0;
            radius = 0;
            alive = false;
            return tempEnergy;
        }
    }

    private float energyToRadius(float energy) {
        return (float) (Math.sqrt(energy) * RADIUS_PER_ENERGY_SQRT);
    }
}
