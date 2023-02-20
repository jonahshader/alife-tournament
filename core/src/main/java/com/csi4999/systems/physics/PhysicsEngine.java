package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class PhysicsEngine {
    private List<Collider> colliders;

    private List<PhysicsObject> objects;

    private ReentrantLock l = new ReentrantLock();

    public PhysicsEngine() {
        colliders = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public void addCollider(Collider c) {
        colliders.add(c);
    }

    public void removeCollider(Collider c) {
        colliders.remove(c);
    }

    public void run(float dt) {
        runCollision();
        move(dt);

        // handle removal of things
        colliders.removeIf(c -> c.removeQueued);
        l.lock();
        objects.removeIf(c -> c.removeQueued);
        l.unlock();
    }

    private void runCollision() {
        insertionSort(colliders);
        // iterate through all colliders, comparing each to the surrounding colliders
        IntStream.range(0, colliders.size()).parallel().forEach(i -> {
            // we are comparing the baseCollider to (+) neighbors in the sorted colliders list
            Collider baseCollider = colliders.get(i);
            // check + possible collisions
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider nextCollider = colliders.get(j);
                if (baseCollider.bounds.x + baseCollider.bounds.width >= nextCollider.bounds.x) {
                    if (nextCollider.collidable && baseCollider.collidesWith(nextCollider))
                        baseCollider.addCollider(nextCollider);
                    if (baseCollider.collidable && nextCollider.collidesWith(baseCollider))
                        nextCollider.addCollider(baseCollider);
                } else {
                    break;
                }
            }
        });
//        for (int i = 0; i < colliders.size(); i++) {
//            // we are comparing the baseCollider to (+) neighbors in the sorted colliders list
//            Collider baseCollider = colliders.get(i);
//            // check + possible collisions
//            for (int j = i + 1; j < colliders.size(); j++) {
//                Collider nextCollider = colliders.get(j);
//                if (baseCollider.bounds.x + baseCollider.bounds.width >= nextCollider.bounds.x) {
//                    if (nextCollider.collidable && baseCollider.collidesWith(nextCollider))
//                        baseCollider.addCollider(nextCollider);
//                    if (baseCollider.collidable && nextCollider.collidesWith(baseCollider))
//                        nextCollider.addCollider(baseCollider);
//                } else {
//                    break;
//                }
//            }
//        }

        // parallelStream()?
        colliders.parallelStream().forEach(Collider::handleColliders);
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
        l.lock();
        for (PhysicsObject o: objects) {
            o.draw(batch, shapeDrawer, null, 1f);
        }
        l.unlock();
    }


    public void move(float dt) {
//        for (PhysicsObject o: objects) {
//            o.move(dt, null);
//        }
        objects.parallelStream().forEach(o -> o.move(dt, null));
    }

    public void addObject(PhysicsObject o) {
        l.lock();
        objects.add(o);
        l.unlock();
    }
}
