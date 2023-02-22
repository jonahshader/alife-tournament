package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreatureSpawner {
    private static final int INITIAL_AMOUNT = 200;
    private static final int MINIMUM_AMOUNT = 50;
    private static final float SPREAD_STD = 512f;
    private List<Creature> creatures = new ArrayList<>();
    public CreatureSpawner() {}
    public CreatureSpawner(Random r, PhysicsEngine physics, List<SensorBuilder> sensorBuilders, List<ToolBuilder> toolBuilders) {
        for (int i = 0; i < INITIAL_AMOUNT; i++) {
            addRandomCreature(r, physics, sensorBuilders, toolBuilders);
        }
    }

    public void run(PhysicsEngine engine, Random rand, float mutateAmount) {
        creatures.removeIf(c -> c.removeQueued);
        List<Creature> newCreatures = new ArrayList<>();
        for (Creature c : creatures) {
            List<Creature> newOffspring = c.getNewOffspring(engine, rand, mutateAmount);
            if (newOffspring != null) {
                newCreatures.addAll(newOffspring);
            }
        }
        creatures.addAll(newCreatures);
    }

    private void addRandomCreature(Random r, PhysicsEngine physics, List<SensorBuilder> sensorBuilders, List<ToolBuilder> toolBuilders) {
        Creature c = new Creature(new Vector2((float) r.nextGaussian(0f, SPREAD_STD), (float) r.nextGaussian(0f, SPREAD_STD)),
            sensorBuilders, toolBuilders, 4, 4, physics, r);
        physics.addObject(c);
        physics.addCollider(c);
        creatures.add(c);
    }
}
