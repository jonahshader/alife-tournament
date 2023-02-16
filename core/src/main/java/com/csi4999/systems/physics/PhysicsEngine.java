package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    private List<Collider> colliders;

    public PhysicsEngine() {
        colliders = new ArrayList<>();
    }

    public void addCollider(Collider c) {
        colliders.add(c);
    }

    public void removeCollider(Collider c) {
        colliders.remove(c);
    }

    public void run() {
        insertionSort(colliders);
        // iterate through all colliders, comparing each to the surrounding colliders
        for (int i = 0; i < colliders.size(); i++) {
            // we are comparing the baseCollider to - and + neighbors in the sorted colliders list
            Collider baseCollider = colliders.get(i);
            // check + possible collisions
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x + baseCollider.bounds.width >= nextCollider.bounds.x) {
                    if (baseCollider.collidesWith(nextCollider))
                        baseCollider.collision.add(nextCollider);
                    if (nextCollider.collidesWith(baseCollider))
                        nextCollider.collision.add(baseCollider);
                } else {
                    break;
                }
            }
        }

        // parallelStream()?
        colliders.forEach(Collider::handleColliders);
        colliders.forEach(c -> c.collision.clear());
    }

    // using insertion sort because it performs well for nearly-sorted arrays
    private static void insertionSort(List<Collider> arr) {
        for (int i = 1; i < arr.size(); i++) {
            Collider key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j).bounds.x > key.bounds.x) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer) {
        for (Collider c: colliders) {
            c.draw(batch, shapeDrawer, null, 1f);
        }
    }

    public void move(float dt) {
        for (Collider c: colliders) {
            c.move(dt);
        }
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer) {
        for (Collider c: colliders) {
            c.draw(batch, shapeDrawer, null, 1f);
        }
    }

    public void move(float dt) {
        for (Collider c: colliders) {
            c.move(dt);
        }
    }
}
