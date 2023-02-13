package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.PhysicsObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    private List<Collider> colliders;
    private List<Collider> collision;

    public PhysicsEngine() {
        colliders = new ArrayList<>();
        collision = new ArrayList<>();
    }

    public void addCollider(Collider c) {
        colliders.add(c);
    }

    public void removeCollider(Collider c) {
        colliders.remove(c);
    }

    public void run() {
        // NOTE: currently not thread safe!
        // compute the bounds of each collider, as it is used for the sweep & prune
        colliders.forEach(Collider::computeBounds);
        insertionSort();
        // iterate through all colliders, comparing each to the surrounding colliders
        for (int i = 0; i < colliders.size(); i++) {
            // we are comparing the baseCollider to - and + neighbors in the sorted colliders list
            Collider baseCollider = colliders.get(i);
            // check + possible collisions
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x + baseCollider.bounds.width >= nextCollider.bounds.x) {
                    if (baseCollider.collidesWith(nextCollider)) {
                        collision.add(nextCollider);
                    }
                } else {
                    break;
                }
            }
            // check - possible collisions
            for (int j = i - 1; j >= 0; j--) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x <= nextCollider.bounds.x + nextCollider.bounds.width) {
                    if (baseCollider.collidesWith(nextCollider)) {
                        collision.add(nextCollider);
                    }
                } else {
                    break;
                }
            }
            // notify the collider of whom it collided with
            baseCollider.receiveActiveColliders(collision);
            collision.clear();
        }
    }

    // using insertion sort because it performs well for nearly-sorted arrays
    private void insertionSort() {
        for (int i = 1; i < colliders.size(); i++) {
            Collider key = colliders.get(i);
            int j = i - 1;
            while (j >= 0 && colliders.get(j).bounds.x > key.bounds.x) {
                colliders.set(j + 1, colliders.get(j));
                j--;
            }
            colliders.set(j + 1, key);
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
