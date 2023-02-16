package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.Random;

public class CreatureSpawner {
        private ArrayList<Creature> allCreatures;
        public CreatureSpawner(int initialSize, Random r, PhysicsEngine physics, ArrayList<SensorBuilder> sensorBuilders, ArrayList<ToolBuilder> toolBuilders) {
            this.allCreatures = new ArrayList<>();

            for (int i = 0; i < initialSize; i++) {
                Creature c = new Creature(new Vector2((float) r.nextGaussian(0f, 128f), (float) r.nextGaussian(0f, 64f)),
                    sensorBuilders, toolBuilders, 3, 3, physics, r);
                physics.addCollider(c);
                allCreatures.add(c);
            }
        }
}
