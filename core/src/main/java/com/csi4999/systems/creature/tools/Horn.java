package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Horn extends PhysicsObject implements Tool {

    private static final float MUTATE_POS_STD = 0.15f;
    private static final float MUTATE_ROT_STD = 1f;
    private static final float DAMAGE = 25f;
    private static final float ENERGY_CONSUMPTION = 0.1f; // units per second

    public Horn() {}

    public Horn(Vector2 position) {
        super(position, new Vector2().setZero(), new Vector2().setZero());
        this.color.r = 1f;
        this.color.g = 0f;
        this.color.b = 0f;
    }
    @Override
    public void mutate(float amount, Random rand) {
        position.x += rand.nextGaussian() * MUTATE_POS_STD;
        position.y += rand.nextGaussian() * MUTATE_POS_STD;
        rotationDegrees += rand.nextGaussian() * MUTATE_ROT_STD;
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        // Need to make this scale off of parent creature size
        shapeDrawer.filledTriangle(0f, 0f, 5f, 0f, 2.5f, 5f, color);
    }

    @Override
    public void use(float strength, float dt, Creature parent) {

    }

    @Override
    public float getEnergyConsumption() {return ENERGY_CONSUMPTION;}

    @Override
    public void remove(PhysicsEngine engine) {
        // Not a collider, so it is not in engine
    }
}
