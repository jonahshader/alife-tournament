package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Flagella extends PhysicsObject implements Tool {
    private static final float MUTATE_POS_STD = 0.5f;
    private static final float MUTATE_ROT_STD = 1f;
    private static final float FORCE = 300f;
    private static final float ENERGY_CONSUMPTION_DYNAMIC = 0.2f; // units per strength squared per second
    private static final float ENERGY_CONSUMPTION_STATIC = 0.02f; // units per second
    private Vector2 thrust;
    private float lastStrength = 0f;
    private float animationProgress = 0;
    private boolean opposingThrust = false;

    public Flagella(){}
    // copy constructor
    public Flagella(Flagella f) {
        super(new Vector2(f.position), new Vector2(f.velocity), new Vector2(f.acceleration));
        thrust = new Vector2(f.thrust);
        lastStrength = f.lastStrength;
        animationProgress = f.animationProgress;
        rotationDegrees = f.rotationDegrees;
    }

    public Flagella(Vector2 position) {
        super(position, new Vector2().setZero(), new Vector2().setZero());
        thrust = new Vector2();
    }

    @Override
    public void mutate(float amount, Random rand) {
        position.x += rand.nextGaussian() * MUTATE_POS_STD;
        position.y += rand.nextGaussian() * MUTATE_POS_STD;
        rotationDegrees += rand.nextGaussian() * MUTATE_ROT_STD;
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        if (opposingThrust) {
            color.set(1f, 0.6f, 0.6f, 1f);
        } else {
            color.set(0.7f, 0.95f, 0.95f, 1f);
        }
        Sprite circle = CustomGraphics.getInstance().circle;
        circle.setScale(2 * 2f / circle.getWidth());
        circle.setOriginBasedPosition(0f, 0f);
        circle.setColor(color);
        circle.draw(batch);
        shapeDrawer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeDrawer.circle(0f, 0f, 2f, 0.5f);
        float str = Math.abs(lastStrength);
        int amount = (int) (10 * str);
        for (int i = 0; i < amount; i++) {
            float a = 1f - ((i + 1f) / amount);
            float scl = (float)Math.pow(.6f + str * .2f, i);
//            shapeDrawer.ellipse((-i*3 - (animationProgress * 40f) % 3) * lastStrength, 0, 2f * scl, 3f * scl);
            circle.setOriginBasedPosition((-i*3 - (animationProgress * 40f) % 3) * lastStrength, 0);
            circle.setColor(color.r, color.g, color.b, a);
            circle.setScale(2f * scl / circle.getWidth(), 3f * scl / circle.getWidth());
            circle.draw(batch);
        }

    }

    @Override
    public void use(float strength, float dt, Creature parent) {
        float theta = (float) ((rotationDegrees + parent.rotationDegrees) * Math.PI / 180);
//        strength = strength * .5f + .5f;
        lastStrength = strength;
        float scl = strength * FORCE;
        thrust.set(scl / parent.getMass(), 0f).rotateRad(theta);
//        float scl2 = thrust.dot(parent.velocity) / (thrust.len() * parent.velocity.len());
//        if (Float.isNaN(scl2)) scl2 = -1;
//        scl2 = (2-scl2)/3;
//        System.out.println(scl2);
//        thrust.scl(scl2);
        if (thrust.dot(parent.velocity) < 0) {
            thrust.scl(4); // increase thrust if reverse
            opposingThrust = true;
        } else {
            opposingThrust = false;
        }
        parent.acceleration.add(thrust);

        float I = .5f * parent.radius * parent.radius * parent.getMass();
        thrust.set(scl, 0f).rotateDeg(rotationDegrees);
        float thrustProportion = position.crs(thrust);
        float rAccel = thrustProportion / I;
        parent.rotationalAccel += rAccel * 180 / Math.PI;

        animationProgress += strength * dt;
//        float upperLim = 1f;
        if (animationProgress >= Math.PI * 2) animationProgress -= Math.PI * 2;
        else if (animationProgress < 0)animationProgress += Math.PI * 2;
    }

    @Override
    public Tool copy(Creature newParent, PhysicsEngine engine) {
        Flagella newF = new Flagella(this);
        newParent.getChildren().add(newF);
        return newF;
    }


    @Override
    public float getEnergyConsumption() {
        return (lastStrength * lastStrength * ENERGY_CONSUMPTION_DYNAMIC) + ENERGY_CONSUMPTION_STATIC;
    }
}
