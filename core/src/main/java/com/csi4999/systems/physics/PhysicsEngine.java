package com.csi4999.systems.physics;

import com.csi4999.systems.PhysicsObject;

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
        colliders.forEach(Collider::computeBounds);
        insertionSort();
        for (int i = 0; i < colliders.size(); i++) {
            Collider baseCollider = colliders.get(i);

            for (int j = i + 1; j < colliders.size(); j++) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x + baseCollider.bounds.width > nextCollider.bounds.x) {
                    if (baseCollider.collidesWith(nextCollider)) {
                        collision.add(nextCollider);
                    }
                } else {
                    break;
                }
            }
            for (int j = i - 1; j >= 0; j--) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x + baseCollider.bounds.width > nextCollider.bounds.x) {
                    if (baseCollider.collidesWith(nextCollider)) {
                        collision.add(nextCollider);
                    }
                } else {
                    break;
                }
            }
            baseCollider.receiveActiveColliders(collision);
            collision.clear();
        }
    }

    // using insertion sort because it performs well for nearly sorted arrays
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
}
