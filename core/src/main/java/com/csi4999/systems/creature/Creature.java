package com.csi4999.systems.creature;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.Mutable;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.ai.Brain;
import com.csi4999.systems.ai.SparseBrain;
import com.csi4999.systems.creature.sensors.Eye;
import com.csi4999.systems.environment.Food;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Creature extends Circle implements Mutable {

    private static final float BASE_RADIUS = 10f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_HEALTH = 10f;
    private static final float BASE_MAX_ACCEL = 10f; // units per second. meters?
    private static final float DRAG = 6f; // accel per velocity
    private static final float ANGULAR_DRAG = 15f; // accel per velocity (degrees)

    private static final float COLOR_CHANGE_VELOCITY = 8f; // units per second

    private static final int MISC_INPUTS = 7;
    private static final int MISC_OUTPUTS = 1;
    private static final float BASE_MAX_ENERGY_SCALAR = 1.5f;
    private static final float BASE_ENERGY = 50f;
    private static final float REPLICATE_DELAY = 3f;
    private static final int REPLICATE_AMOUNT = 2;

    private float health;
    public float energy;
    private float maxAccel;
    private float replicateTimer;

    private boolean collidingWithFood = false;

    private List<Sensor> sensors;
    private List<Tool> tools;
    private Brain brain;
    private float[] inputs;

    private Vector2 rotatedVelocity = new Vector2();

    //Creature Data for user
    public long userID;
    public long creatureID;
    public String creatureName;
    public String creatureDescription;

    public Creature() {}

    public Creature(Creature c, PhysicsEngine engine) {
        super(new Vector2(c.position), new Vector2(c.velocity), new Vector2(c.acceleration), c.radius);
        transformedRadius = c.transformedRadius;
        similarityVector = c.similarityVector.clone();
        health = MAX_HEALTH;
        maxAccel = c.maxAccel;
        energy = c.energy;

        sensors = new ArrayList<>();
        tools = new ArrayList<>();

        for (Sensor s : c.sensors)
            sensors.add(s.copy(this, engine));
        for (Tool t : c.tools)
            tools.add(t.copy(this, engine));


        brain = c.brain.copy();
        inputs = c.inputs.clone();
        replicateTimer = REPLICATE_DELAY;
    }

    public Creature(Vector2 pos, List<SensorBuilder> sensorBuilders, List<ToolBuilder> toolBuilders, int initialSensors, int initialTools, PhysicsEngine engine, Random rand) {
        super(pos, new Vector2().setZero(), new Vector2().setZero(), BASE_RADIUS);
        // these may change based on passives the creatures has, but initially they are the base values
        health = MAX_HEALTH;
        maxAccel = BASE_MAX_ACCEL;
        energy = BASE_ENERGY;
        replicateTimer = REPLICATE_DELAY;

        sensors = new ArrayList<>();
        tools = new ArrayList<>();

        // make some sensors
        // also keep track of input size
        int inputSize = MISC_INPUTS;
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

        similarityVector = new float[SIMILARITY_VECTOR_SIZE];
        for (int i = 0; i < similarityVector.length; i++)
            similarityVector[i] = (float) rand.nextGaussian();
        normalizeSimilarity(similarityVector);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        // zero out accel, apply drag
        acceleration.setZero();
        acceleration.set(velocity).scl(-DRAG);
        rotationalAccel = rotationalVel * -ANGULAR_DRAG;

        // the replicate timer constantly increases, unless the corresponding brain output is high
        replicateTimer += dt;
        if (replicateTimer > REPLICATE_DELAY) replicateTimer = REPLICATE_DELAY;

        // copy sensor values into inputs array
        int inputIndex = 0;
        for (Sensor sensor : sensors) {
            float[] sensorValues = sensor.read();
            System.arraycopy(sensorValues, 0, inputs, inputIndex, sensorValues.length);
            inputIndex += sensorValues.length;
        }
        inputs[inputIndex++] = energy / BASE_ENERGY;
        inputs[inputIndex++] = collidingWithFood ? 1 : -1;
        inputs[inputIndex++] = (float) Math.cos(rotationDegrees * Math.PI / 180);
        inputs[inputIndex++] = (float) Math.sin(rotationDegrees * Math.PI / 180);
        rotatedVelocity.set(velocity).rotateDeg(-rotationDegrees);
        inputs[inputIndex++] = (float) Math.tanh(rotatedVelocity.x / 10);
        inputs[inputIndex++] = (float) Math.tanh(rotatedVelocity.y / 10);
        inputs[inputIndex++] = (float) Math.tanh(rotationalVel / 100);


        // run brain with inputs
        float[] output = brain.run(inputs);

        // apply outputs to tools
        for (int i = 0; i < tools.size(); i++)
            tools.get(i).use(output[i], dt, this);

        // apply outputs to misc (just color for now)
        int miscOutputIndex = tools.size();
//        color.r = towardsValue(color.r, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);
//        color.g = towardsValue(color.g, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);
//        color.b = towardsValue(color.b, output[miscOutputIndex++] * .5f + .5f, COLOR_CHANGE_VELOCITY * dt);
        if (output[miscOutputIndex++] > 0) {
            replicateTimer -= dt * 2;
        }

        // compute energy
        float energyLoss = 0f;
        for (Tool t : tools) energyLoss += t.getEnergyConsumption();
        for (Sensor s : sensors) energyLoss += s.getEnergyConsumption();
        energyLoss += brain.getEnergyConsumption();
        energy -= energyLoss * dt;
        if (energy < 0) {
            energy = 0;
            queueRemoval();
        }

        float scl = ((float)Math.sqrt(energy / BASE_ENERGY) + MIN_SCALE) / (1 + MIN_SCALE);
        scl = Math.min(scl, BASE_MAX_ENERGY_SCALAR);
        scale.set(scl, scl);



        // continue with default move behavior
        super.move(dt, parent);
    }
    @Override
    public void mutate(float amount, Random rand) {
        super.mutate(amount, rand);
        sensors.forEach(sensor -> sensor.mutate(amount, rand));
        tools.forEach(tool -> tool.mutate(amount, rand));
        brain.mutate(amount, rand);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        color.r = (float) (Math.tanh(similarityVector[0]) * .5f + .5f);
        color.g = (float) (Math.tanh(similarityVector[1]) * .5f + .5f);
        color.b = (float) (Math.tanh(similarityVector[2]) * .5f + .5f);
//        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
//        shapeDrawer.filledCircle(0f, 0f, this.radius);
//        // TODO: make this better
        Sprite circle = CustomGraphics.getInstance().circle;
        circle.setScale(radius * 2f / circle.getWidth());
        circle.setOriginBasedPosition(0f, 0f);
        circle.setColor(color);
        circle.draw(batch);
        float avgColor = (color.r + color.g + color.b) / 3;
        shapeDrawer.setColor(avgColor * color.r, avgColor * color.g, avgColor * color.b, parentAlpha);
        shapeDrawer.circle(0f, 0f, this.radius * (health/MAX_HEALTH));
    }

    public List<Creature> getNewOffspring(PhysicsEngine engine, Random rand, float mutateAmount) {
        if (replicateTimer < 0) {
//            System.out.println("tryna replicate");
            replicateTimer += REPLICATE_DELAY;
            // create offspring
            List<Creature> offspring = new ArrayList<>(REPLICATE_AMOUNT);
            energy /= (1 + REPLICATE_AMOUNT); // divide energy equally. offsprings will inherit this
            for (int i = 0; i < REPLICATE_AMOUNT; i++) {
                Creature newCreature = new Creature(this, engine);
                if (mutateAmount > 0)
                    newCreature.mutate(mutateAmount, rand);
                offspring.add(newCreature);
                engine.addCollider(newCreature);
                engine.addObject(newCreature);
            }
            return offspring;
        }
        return null;
    }

    @Override
    public void handleColliders() {
        collidingWithFood = false;
        for (Collider c : collision) {
            if (c instanceof Food) {
                collidingWithFood = true;
                break;
            }
        }
        // TODO: replicate eye behavior
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

    public void takeDamage(float damageAmount) {
        health -= damageAmount;

        if (health <= 0) queueRemoval();
    }
}
