package com.csi4999.systems.creature;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.Mutable;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.ai.Brain;
import com.csi4999.systems.ai.SparseBrain;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Creature extends Circle implements Mutable {

    private static final float BASE_RADIUS = 10f;
    private static final float MIN_SCALE = 0.5f;
    private static final float BASE_MAX_HEALTH = 10f;
    private static final float BASE_MAX_ACCEL = 10f; // units per second. meters?
    private static final float DRAG = 8f; // accel per velocity
    private static final float ANGULAR_DRAG = 12f; // accel per velocity (degrees)

    private static final float COLOR_CHANGE_VELOCITY = 8f; // units per second
    private static final int MISC_OUTPUTS = 3;
    private static final float BASE_MAX_ENERGY_SCALAR = 1.5f;
    private static final float BASE_ENERGY = 50f;

    private float maxHealth;
    private float energy;
    private float maxAccel;

    private boolean dead;

    private List<Sensor> sensors;
    private List<Tool> tools;
    private Brain brain;
    private float[] inputs;

    public Creature() {}

    public Creature(Vector2 pos, List<SensorBuilder> sensorBuilders, List<ToolBuilder> toolBuilders, int initialSensors, int initialTools, PhysicsEngine engine, Random rand) {
        super(pos, new Vector2().setZero(), new Vector2().setZero(), BASE_RADIUS);
        // these may change based on passives the creatures has, but initially they are the base values
        maxHealth = BASE_MAX_HEALTH;
        maxAccel = BASE_MAX_ACCEL;
        energy = BASE_ENERGY;
        dead = false;

        sensors = new ArrayList<>();
        tools = new ArrayList<>();

        // make some sensors
        // also keep track of input size
        int inputSize = 0;
        if (sensorBuilders.size() > 0) {
            for (int i = 0; i < initialSensors; i++) {
                Sensor newSensor = sensorBuilders.get(rand.nextInt(sensorBuilders.size())).buildSensor(this, engine, rand);
                inputSize += newSensor.read().length; // TODO: do we need a getSize method in Sensor? or is using read().length fine?
                sensors.add(newSensor);
            }
            inputs = new float[inputSize];
        } else {
            inputs = null;
        }

        // make some tools
        if (toolBuilders.size() > 0) {
            for (int i = 0; i < initialTools; i++) {
                Tool newTool  = toolBuilders.get(rand.nextInt(toolBuilders.size())).buildTool(this, engine, rand);
                tools.add(newTool);
            }
        }

        brain = new SparseBrain(inputSize, tools.size() + MISC_OUTPUTS, inputSize + tools.size() + MISC_OUTPUTS + 20,
            .33f, .1f, .25f, .5f, rand);
    }

    public void remove(PhysicsEngine engine) {
        tools.forEach(t -> t.remove(engine));
        sensors.forEach(s -> s.remove(engine));
        engine.removeCollider(this);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        // zero out accel, apply drag
        acceleration.setZero();
        acceleration.set(velocity).scl(-DRAG);
        rotationalAccel = rotationalVel * -ANGULAR_DRAG;

        // copy sensor values into inputs array
        int inputIndex = 0;
        for (Sensor sensor : sensors) {
            float[] sensorValues = sensor.read();
            System.arraycopy(sensorValues, 0, inputs, inputIndex, sensorValues.length);
            inputIndex += sensorValues.length;
        }

        // run brain with inputs
        float[] output = brain.run(inputs);

        // apply outputs to tools
        for (int i = 0; i < tools.size(); i++)
            tools.get(i).use(output[i], dt, this);

        // apply outputs to misc (just color for now)
        int miscOutputIndex = tools.size();
        color.r = towardsValue(color.r, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);
        color.g = towardsValue(color.g, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);
        color.b = towardsValue(color.b, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);

        // compute energy
        float energyLoss = 0f;
        for (Tool t : tools) energyLoss += t.getEnergyConsumption();
        //System.out.println(energyLoss);
        for (Sensor s : sensors) energyLoss += s.getEnergyConsumption();
        //System.out.println(energyLoss);
        energyLoss += brain.getEnergyConsumption();
        //System.out.println(energyLoss);
        energy -= energyLoss * dt;
        if (energy < 0) {
            energy = 0;
            dead = true;
        }

        float scl = ((float)Math.sqrt(energy / BASE_ENERGY) + MIN_SCALE) / (1 + MIN_SCALE);
        scale.set(scl, scl);



        // continue with default move behavior
        super.move(dt, parent);
    }
    @Override
    public void mutate(float amount, Random rand) {
        sensors.forEach(sensor -> sensor.mutate(amount, rand));
        tools.forEach(tool -> tool.mutate(amount, rand));
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.filledCircle(0f, 0f, this.radius);
//        Matrix4 current = batch.getTransformMatrix();
//        batch.setTransformMatrix(oldTransform);
//        renderBounds(shapeDrawer);
//        for (PhysicsObject o : getChildren()) {
//            if (o instanceof Eye) {
//
//                ((Eye)o).renderBounds(shapeDrawer);
//
//            }
//        }
//        batch.setTransformMatrix(current);
    }

    @Override
    public void handleColliders() {
        // TODO: do we have a built in body collision sensor?
    }

    public float getMass() {
        // TODO: maybe base this off of surface area, or "volume"?
        return 1f;
    }

    // TODO: unit test
    /**
     * takes a step towards desired from current, changing at most 'rate'
     * @param current - the current value we want to move
     * @param desired - the value we want to move towards
     * @param rate - the amount to change desired by to move towards current
     * @return - the moved value
     */
    private float towardsValue(float current, float desired, float rate) {
        if (desired > current + rate) {
            current += rate;
        } else if (desired > current) {
            current = desired;
        } else if (desired < current - rate) {
            current -= rate;
        } else if (desired < current) {
            current = desired;
        }

        return current;
    }
}
