package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.Random;

public class CreatureSpawner {
        private ArrayList<Creature> allCreatures;
        public CreatureSpawner(int initialSize, Random r, PhysicsEngine physics) {
            this.allCreatures = new ArrayList<>();

            for (int i = 0; i < initialSize; i++) {
                Creature c = new Creature(new Vector2((float) r.nextGaussian(0f, 128f), (float) r.nextGaussian(0f, 64f)), new ArrayList<>(), new ArrayList<>(), 0, 0, physics, r);
                addToPhysics(physics, c);
                allCreatures.add(c);
            }
        }

        public void addToPhysics(PhysicsEngine physics, Creature creature) {
            physics.addCollider(creature);
        }
}
