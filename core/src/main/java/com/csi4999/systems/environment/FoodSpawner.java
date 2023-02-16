package com.csi4999.systems.environment;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.Random;

public class FoodSpawner {
    private ArrayList<Food> allFood;

    public FoodSpawner(int initialSize, Random r, PhysicsEngine physics) {
        this.allFood = new ArrayList<>();

        for (int i = 0; i < initialSize; i++) {
            Food f = new Food(new Vector2((float) r.nextGaussian(0f, 128f), (float) r.nextGaussian(0f, 64f)), new Vector2((float) r.nextGaussian(0f, 4f), (float) r.nextGaussian(0f, 4f)), new Vector2(0f, 0f), 8f);
            physics.addCollider(f);
            physics.addObject(f);
            allFood.add(f);
        }
    }

    public void addToPhysics(PhysicsEngine physics, Food foodBubble) {
        physics.addCollider(foodBubble);
    }
}
