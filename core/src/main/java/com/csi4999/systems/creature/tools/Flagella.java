package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Flagella extends PhysicsObject implements Tool {
    private static final float MUTATE_POS_STD = 0.15f;
    private static final float MUTATE_ROT_STD = 1f;
    private static final float FORCE = 300f;
    private Vector2 thrust;
    private float lastStrength = 0f;

    private float animationProgress = 0;

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

//        shapeDrawer.filledTriangle(0f, 3f, 0f, -3f, 8f, 0f, color);
//        shapeDrawer.line(0f, 0f, 10f, 0f, color);
        shapeDrawer.filledCircle(0f, 0f, 3f, color);
        int amount = (int) (10 * lastStrength);
        for (int i = 0; i < amount; i++) {
            float a = 1f - ((i + 1f) / amount);
            shapeDrawer.setColor(color.r, color.g, color.b, a);
            float scl = (float)Math.pow(.6f + lastStrength * .2f, i);
            shapeDrawer.ellipse((-i*3 - (animationProgress * 40f) % 3) * lastStrength, 0, 2f * scl, 3f * scl);
        }
//        float lastHeight = 0f;
//        for (int i = 0; i < 16; i++) {
//            float height = (float) Math.cos(i * .8f - animationProgress * 30);

//            shapeDrawer.line(-i, lastHeight, -i + 1, height, color);
//            lastHeight = height;
//        }
    }

    @Override
    public void use(float strength, float dt, Creature parent) {
        float theta = (float) ((rotationDegrees + parent.rotationDegrees) * Math.PI / 180);
        strength = strength * .5f + .5f;
        lastStrength = strength;
        float scl = strength * FORCE;
        thrust.set(scl / parent.getMass(), 0f).rotateRad(theta);
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
    public void remove(PhysicsEngine engine) {

    }
}
