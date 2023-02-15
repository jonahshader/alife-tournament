package com.csi4999.systems.environment;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;
import java.util.Random;

public class Environment {
    // One and only instance of random
    PhysicsEngine physics;
    Random r;
    // Environment will eventually have all sorts of spawners in it (food, creature, artifacts)

    FoodSpawner foodSpawner;
    CreatureSpawner creatureSpawner;

    List<SensorBuilder> availableSensors;
    List<ToolBuilder> availableTools;

    public Environment(int initialFood, int initalCreatures) {
        this.physics = new PhysicsEngine();
        this.r = new RandomXS128();
        this.foodSpawner = new FoodSpawner(initialFood, this.r, this.physics);
        this.creatureSpawner = new CreatureSpawner(initalCreatures, this.r, this.physics);
    }

    public void drawObjects(ShapeDrawer drawer, Batch batch) {
        physics.draw(batch, drawer);
    }

    public void moveObjects(float dt) {
        physics.run();
        physics.move(dt);
    }
}
