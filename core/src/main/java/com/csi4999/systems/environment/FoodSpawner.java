package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodSpawner {
    private static final int INITIAL_AMOUNT = 200;
    private static final int MINIMUM_AMOUNT = 1000;
    private static final float SPREAD_STD = 1024f;
    private List<Food> food = new ArrayList<>();

    public FoodSpawner(Random r, PhysicsEngine physics) {
        for (int i = 0; i < INITIAL_AMOUNT; i++) {
            addRandomFood(r, physics, true);
        }
    }

    public void run(Random r, PhysicsEngine physics) {
        food.removeIf(f -> f.removeQueued);
        if (food.size() < MINIMUM_AMOUNT) {
            addRandomFood(r, physics, false);
        }
    }

    private void addRandomFood(Random r, PhysicsEngine physics, boolean growFully) {
        Food f = new Food(new Vector2((float) r.nextGaussian(0f, SPREAD_STD), (float) r.nextGaussian(0f, SPREAD_STD)), r);
        if (growFully) f.growFully();
        physics.addCollider(f);
        physics.addObject(f);
        food.add(f);
    }
}
