package com.csi4999.systems.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Random;

public class Environment {
    // One and only instance of random
    public PhysicsEngine physics;
    Random r;

    public FoodSpawner foodSpawner;
    public CreatureSpawner creatureSpawner;

    private EnvProperties properties;

    public static final float dt = 1/60f;
    public long age = 0;

    public String environmentName;
    public String EnvironmentDescription;

    public long userID; //for use in database
    public long EnvironmentID; //populated by server, will be needed if user can alter and resave environment

    public Environment() {}

    public Environment(EnvProperties properties) {
        this.properties = properties;
        this.physics = new PhysicsEngine();
        this.r = new RandomXS128();
        this.foodSpawner = new FoodSpawner(this.r, this.physics, properties);
        this.creatureSpawner = new CreatureSpawner(this.r, this.physics, properties);
    }

    public Environment(EnvProperties properties, Creature creature) {
        this.properties = properties;
        this.physics = new PhysicsEngine();
        this.r = new RandomXS128();
        this.foodSpawner = new FoodSpawner(this.r, this.physics, properties);
        this.creatureSpawner = new CreatureSpawner(creature, this.physics, properties, this.r);
    }

    public void draw(ShapeDrawer drawer, Batch batch, Camera cam) {
        physics.draw(batch, drawer, cam);
//        physics.renderBounds(drawer);
    }

    public void removeOutsideOfRectangle(Rectangle rectangle) {
        physics.removeOutsideOfRectangle(rectangle);
        foodSpawner.handleRemoval();
        creatureSpawner.handleRemoval();
    }

    public synchronized void update() {
        physics.run(dt);
        creatureSpawner.run(physics, r, properties.globalMutationRate);
        foodSpawner.run(r, physics);
        age++;
    }
    public Creature getCreature(int x, int y) {
        return physics.getCreature(x, y);
    }

    public void merge(Environment toMerge) {
        foodSpawner.merge(toMerge.foodSpawner);
        creatureSpawner.merge(toMerge.creatureSpawner);
        physics.merge(toMerge.physics);
    }
}
