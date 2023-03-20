package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodSpawner {
    private List<Food> food = new ArrayList<>();
    private EnvProperties properties;

    public FoodSpawner(){}

    public FoodSpawner(Random r, PhysicsEngine physics, EnvProperties properties) {
        this.properties = properties;
        for (int i = 0; i < properties.initialFood; i++) {
            addRandomFood(r, physics, true);
        }
    }

    public void run(Random r, PhysicsEngine physics) {
        handleRemoval();
        if (food.size() < properties.foodTarget) {
            addRandomFood(r, physics, false);
        }
    }

    public void handleRemoval() {
        food.removeIf(f -> f.removeQueued);
    }

    public float getAllFoodEnergy() {
        float totalEnergy = 0;
        for (Food f : food)
            totalEnergy += f.getEnergy();
        return totalEnergy;
    }

    private void addRandomFood(Random r, PhysicsEngine physics, boolean growFully) {
        Food f = new Food(new Vector2((float) r.nextGaussian(0f, properties.foodSpawnStd), (float) r.nextGaussian(0f, properties.foodSpawnStd)), r);
        if (growFully) f.growFully();
        physics.addCollider(f);
        physics.addObject(f);
        food.add(f);
    }
}
