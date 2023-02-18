package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.environment.Food;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Mouth extends Circle implements Tool {
    private static final float ENERGY_CONSUMPTION_DYNAMIC = 0.8f; // units per strength squared per second
    private static final float ENERGY_CONSUMPTION_STATIC = 0.1f; // units per second
    private static final float MUTATE_ROTATION_STD = 0.3f;
    private static final float MAX_CONSUME_RATE = 3f;

    private Creature parent;
    private MouthPart left, right;

    private float animationProgress;
    private float lastStrength;
    private float lastDt;

    public Mouth() {}

    public Mouth(Vector2 pos, float rotation, Creature parent) {
        super(pos, 7f);
        this.rotationDegrees = rotation;
        this.parent = parent;
        left = new MouthPart();
        left.scale.y = -1;
        right = new MouthPart();
        getChildren().add(left);
        getChildren().add(right);
        animationProgress = 0f;
        lastStrength = 0f;
        lastDt = 0f;
        collidable = false;
    }

    @Override
    public void mutate(float amount, Random rand) {
        float rotateAmount = (float) (rand.nextGaussian() * MUTATE_ROTATION_STD * amount);
        rotationDegrees += rotateAmount;
        position.rotateDeg(rotateAmount); // TODO: this can probably drift over time
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        float rot = (float) ((Math.cos(animationProgress) * .5 + .5) * 30);
        left.rotationDegrees = -rot;
        right.rotationDegrees = rot;
    }

    @Override
    public float getEnergyConsumption() {
        return lastStrength * ENERGY_CONSUMPTION_DYNAMIC + ENERGY_CONSUMPTION_STATIC;
    }

    @Override
    public void use(float strength, float dt, Creature parent) {
        lastDt = dt;
        lastStrength = (strength * .5f + .5f);
        animationProgress += lastStrength * dt * 20;
        animationProgress %= Math.PI * 2;
    }

    @Override
    public void remove(PhysicsEngine engine) {

    }

    @Override
    public void handleColliders() {
        for (Collider c : collision) {
            if (c instanceof Food) {
                Food f = (Food) c;
                parent.energy += f.eat(lastStrength * lastDt * MAX_CONSUME_RATE);
            }
        }
    }
}
