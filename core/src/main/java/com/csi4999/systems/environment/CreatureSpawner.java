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
    public CreatureSpawner() {}
    public CreatureSpawner(Random r, PhysicsEngine physics, EnvProperties properties) {
        this.properties = properties;
        for (int i = 0; i < properties.initialCreatures; i++) {
            addRandomCreature(r, physics, properties.sensorBuilders, properties.toolBuilders);
        }
    }

    public void run(PhysicsEngine engine, Random rand, float mutateAmount) {
        handleRemoval();
        List<Creature> newCreatures = new ArrayList<>();
        for (Creature c : creatures) {
            List<Creature> newOffspring = c.getNewOffspring(engine, rand, mutateAmount);
            if (newOffspring != null) {
                newCreatures.addAll(newOffspring);
            }
        }
        creatureLock.lock();
        creatures.addAll(newCreatures);
        creatureLock.unlock();
    }

    public void handleRemoval() {
        creatures.removeIf(c -> c.removeQueued);
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
            sensorBuilders, toolBuilders, r.nextInt(properties.minSensors, properties.maxSensors), r.nextInt(properties.minTools, properties.maxTools), physics, r);
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
