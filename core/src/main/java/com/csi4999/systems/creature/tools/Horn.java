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
        lineLength = (float) ((Math.cos(animationProgress) * .5 + .5) * 20);
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledTriangle(-1f, -2f, lineLength, 0f, -1f, 2f, color);
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
        if (collision.size() > 1) {
            this.color.r = 0f;
            collision.sort((o1, o2) -> {
                float d1 = getDistToCollider(o1);
                float d2 = getDistToCollider(o2);
                return Float.compare(d1, d2);
            });

            Collider nearest = collision.get(0);
            if (nearest == parent) {
                for (int i = 1; i < collision.size(); i++) {
                    if (collision.get(i) instanceof Creature) {
                        Creature cr = (Creature) collision.get(i);
                        cr.takeDamage(lastStrength * lastDt * MAX_DAMAGE_RATE);
                    }
                }
            }
        } else {
            this.color.r = 1f;
        }
    }

    private float getDistToCollider (Collider c) {
        return transformedPos.dst2(c.transformedPos);
    }

    @Override
    public float getEnergyConsumption() {
        return lastStrength * ENERGY_CONSUMPTION_DYNAMIC + ENERGY_CONSUMPTION_STATIC;
    }
}
