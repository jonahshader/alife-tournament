package com.csi4999.systems.creature.sensors;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.Random;

public class EyeBuilder implements SensorBuilder {
    @Override
    public Sensor buildSensor(Creature parent, PhysicsEngine engine, Random rand) {
        Vector2 pos = new Vector2().setZero();
        float r = (float) (parent.radius * Math.sqrt(rand.nextFloat()));
        float theta = (float) (rand.nextFloat() * 2 * Math.PI);
        pos.set((float) (Math.cos(theta) * r), (float) (Math.sin(theta) * r));

        Eye eye = new Eye(pos, (float) Math.sqrt(rand.nextFloat()) * 120f + 10f, parent);
        eye.rotationDegrees = rand.nextFloat() * 90;
        engine.addCollider(eye);
        parent.getChildren().add(eye);
        return eye;
    }
}
