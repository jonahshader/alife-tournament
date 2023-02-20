package com.csi4999.systems.environment;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.EyeBuilder;
import com.csi4999.systems.creature.tools.FlagellaBuilder;
import com.csi4999.systems.creature.tools.HornBuilder;
import com.csi4999.systems.creature.tools.MouthBuilder;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Environment {
    // One and only instance of random
    PhysicsEngine physics;
    Random r;

    FoodSpawner foodSpawner;
    public CreatureSpawner creatureSpawner;

    List<SensorBuilder> sensorBuilders;
    List<ToolBuilder> toolBuilders;

    public float mutationRate = 1f;

    public Environment() {}

    public Environment(int initialFood, int initalCreatures) {
        this.sensorBuilders = new ArrayList<>();
        this.toolBuilders = new ArrayList<>();
        this.sensorBuilders.add(new EyeBuilder());
        this.toolBuilders.add(new FlagellaBuilder());
        this.toolBuilders.add(new HornBuilder());
        this.toolBuilders.add(new MouthBuilder());

        this.physics = new PhysicsEngine();
        this.r = new RandomXS128();
        this.foodSpawner = new FoodSpawner(this.r, this.physics);
        this.creatureSpawner = new CreatureSpawner(this.r, this.physics, sensorBuilders, toolBuilders);
    }

    public void draw(ShapeDrawer drawer, Batch batch) {
        physics.draw(batch, drawer);
    }

    public void update(float dt) {
        physics.run(dt);
        creatureSpawner.run(physics, r, mutationRate);
        foodSpawner.run(r, physics);
    }
}
