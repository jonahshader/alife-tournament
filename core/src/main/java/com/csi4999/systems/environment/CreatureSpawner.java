package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class CreatureSpawner {
    public ReentrantLock creatureLock = new ReentrantLock();
    private List<Creature> creatures = new ArrayList<>();
    private EnvProperties properties;
    private FoodSpawner foodSpawner;

    private float toSpawn = 0;
    public CreatureSpawner() {}
    public CreatureSpawner(Random r, PhysicsEngine physics, EnvProperties properties, FoodSpawner foodSpawner) {
        this.properties = properties;
        this.foodSpawner = foodSpawner;
        for (int i = 0; i < properties.initialCreatures; i++) {
            addRandomCreature(r, physics, properties.sensorBuilders, properties.toolBuilders);
        }
    }

    // used for loading a specific creature
    public CreatureSpawner(Random r, PhysicsEngine physics, EnvProperties properties, FoodSpawner foodSpawner, Creature creature) {
        this.properties = properties;
        this.foodSpawner = foodSpawner;
        for (int i = 0; i < properties.initialCreatures; i++) {
            addSpecificCreature(creature, physics, r);
        }
    }

    public void run(PhysicsEngine engine, Random rand, float mutateAmount, float dt) {
        handleRemoval();
        List<Creature> newCreatures = new ArrayList<>();
        while (toSpawn > 1) {
            toSpawn -= 1;
            addRandomCreature(rand, engine, properties.sensorBuilders, properties.toolBuilders);
        }
        for (Creature c : creatures) {
            List<Creature> newOffspring = c.getNewOffspring(engine, rand, mutateAmount);
            if (newOffspring != null) {
                newCreatures.addAll(newOffspring);
            }
        }
        creatureLock.lock();
        creatures.addAll(newCreatures);
        creatureLock.unlock();

        toSpawn += properties.creaturesPerSecond * dt;
    }

    public void handleRemoval() {
        creatureLock.lock();
        for (Creature c : creatures) {
            if (c.removeQueued && c.energy >= 1) {
                // spawn food
                foodSpawner.addFoodAtPos(new Vector2(c.position), c.energy);
            }
        }
        creatures.removeIf(c -> c.removeQueued);
        creatureLock.unlock();
    }

    public int getCreatureCount() {
        return creatures.size();
    }

    public float getAllCreatureEnergy() {
        float totalEnergy = 0;
        for (Creature c : creatures)
            totalEnergy += c.energy;
        return totalEnergy;
    }

    public void merge(CreatureSpawner toMerge) {
        creatureLock.lock();
        creatures.addAll(toMerge.creatures);
        creatureLock.unlock();
    }

    private void addRandomCreature(Random r, PhysicsEngine physics, List<SensorBuilder> sensorBuilders, List<ToolBuilder> toolBuilders) {
        Creature c = new Creature(new Vector2((float) r.nextGaussian(0f, properties.creatureSpawnStd), (float) r.nextGaussian(0f, properties.creatureSpawnStd)),
            sensorBuilders, toolBuilders, r.nextInt(properties.minSensors, properties.maxSensors + 1), r.nextInt(properties.minTools, properties.maxTools + 1), physics, r);
        physics.addObject(c);
        physics.addCollider(c);
        creatureLock.lock();
        creatures.add(c);
        creatureLock.unlock();
    }

    private void addSpecificCreature(Creature creature, PhysicsEngine physics, Random r) {
        Creature c = new Creature(creature, physics);
        c.position = new Vector2((float) r.nextGaussian(0f, properties.creatureSpawnStd), (float) r.nextGaussian(0f, properties.creatureSpawnStd));
        physics.addObject(c);
        physics.addCollider(c);
        creatureLock.lock();
        creatures.add(c);
        creatureLock.unlock();
    }

    public List<Creature> getCreatures() {
        return creatures;
    }
}
