package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.LineSegment;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Horn extends LineSegment implements Tool {
    private static final float MUTATE_ROT_STD = 1f;
    private static final float MAX_DAMAGE_RATE = 10f;
    private static final float ENERGY_CONSUMPTION_DYNAMIC = 0.8f;
    private static final float ENERGY_CONSUMPTION_STATIC = 0.1f;

    private Creature parent;

    private float animationProgress;
    private float lastStrength;
    private float lastDt;

    public Horn() {}

    public Horn(Horn h) {
        super(h.position, h.lineLength);
        rotationDegrees = h.rotationDegrees;
        lastStrength = h.lastStrength;
        lastDt = h.lastDt;
    }

    public Horn(Vector2 pos, float lineLength, float rotation, Creature parent) {
        super(pos, lineLength);
        this.rotationDegrees = rotation;
        this.parent = parent;
        animationProgress = 0f;
        lastStrength = 0f;
        lastDt = 0f;
    }

    @Override
    public void mutate(float amount, Random rand) {
        float rotateAmount = (float) (rand.nextGaussian() * MUTATE_ROT_STD * amount);
        rotationDegrees += rotateAmount;
        position.rotateDeg(rotateAmount);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {

        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledTriangle(0f, 1.5f, lineLength, 0f, 0f, -1.5f, color);
    }

    @Override
    public void use(float strength, float dt, Creature parent) {
        lastDt = dt;
        lastStrength = (strength * .5f + .5f);
        animationProgress += lastStrength * dt * 20;
        animationProgress %= Math.PI * 2;
        lineLength = (float) ((Math.cos(animationProgress) * .5 + .5) * 20);
    }

    @Override
    public Tool copy(Creature newParent, PhysicsEngine engine) {
        Horn h = new Horn(this);
        h.parent = newParent;
        newParent.getChildren().add(h);
        engine.addCollider(h);
        return h;
    }


    @Override
    public void handleColliders() {
        color.r = 1f;
        for (Collider c : collision) {
            if (c != parent && c instanceof Creature) {
                Creature cr = (Creature) c;
                cr.takeDamage(lastStrength * lastDt * MAX_DAMAGE_RATE);
                color.r = 0f;
            }
        }
    }

    @Override
    public float getEnergyConsumption() {
        return lastStrength * ENERGY_CONSUMPTION_DYNAMIC + ENERGY_CONSUMPTION_STATIC;
    }
}
