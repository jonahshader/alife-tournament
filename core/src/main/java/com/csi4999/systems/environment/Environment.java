package com.csi4999.systems.environment;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.FlagellaBuilder;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Environment {
    // One and only instance of random
    PhysicsEngine physics;
    Random r;
    // Environment will eventually have all sorts of spawners in it (food, creature, artifacts)

    FoodSpawner foodSpawner;
    CreatureSpawner creatureSpawner;

    ArrayList<SensorBuilder> availableSensors;
    ArrayList<ToolBuilder> availableTools;

    public Environment(int initialFood, int initalCreatures) {
        this.availableSensors = new ArrayList<>();
        this.availableTools = new ArrayList<>();
        this.availableSensors.add(new EyeBuilder());
        this.availableTools.add(new FlagellaBuilder());

        this.physics = new PhysicsEngine();
        this.r = new RandomXS128();
        this.foodSpawner = new FoodSpawner(initialFood, this.r, this.physics);
        this.creatureSpawner = new CreatureSpawner(initalCreatures, this.r, this.physics, availableSensors, availableTools);
    }

    public void drawObjects(ShapeDrawer drawer, Batch batch) {
        physics.draw(batch, drawer);
    }

    public void moveObjects(float dt) {
        physics.run();
        physics.move(dt);
    }
}
