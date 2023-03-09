package com.csi4999.systems.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import jdk.vm.ci.meta.Constant;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class PhysicsEngine {
    private List<Collider> colliders;

    private List<PhysicsObject> objects;

    private ReentrantLock drawLock = new ReentrantLock();
    private ReentrantLock renderBoundsLock = new ReentrantLock();
    private long age = 1;
    private long quickSortTotalTime = 0;
    private long insertSortTotalTime = 0;

    public PhysicsEngine() {
        colliders = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public void addCollider(Collider c) {
        renderBoundsLock.lock();
        colliders.add(c);
        renderBoundsLock.unlock();
    }

    public void removeCollider(Collider c) {
        colliders.remove(c);
    }

    public void run(float dt) {
        runCollision();
        move(dt);

        // handle removal of things
        renderBoundsLock.lock();
        colliders.removeIf(c -> c.removeQueued);
        renderBoundsLock.unlock();
        drawLock.lock();
        objects.removeIf(c -> c.removeQueued);
        drawLock.unlock();
        age++;
    }

    private void runCollision() {
        Comparator<Collider> colliderComparator = Comparator.comparingDouble(it -> it.bounds.x);
//        Arrays.parallelSort(colliders, colliderComparator);

        renderBoundsLock.lock();
//        if (age % 2 == 0) {
//            long tStart = System.nanoTime();
//            insertionSort(colliders);
//            long tEnd = System.nanoTime();
//            insertSortTotalTime += (tEnd - tStart);
//
//        } else {
//            long tStart = System.nanoTime();
//            colliders.sort(colliderComparator);
//            long tEnd = System.nanoTime();
//            quickSortTotalTime += (tEnd - tStart);
//
//        }
//
//        if (age % 1000 == 0) {
//            System.out.println("Insertion sort time (nanos): " + ((2 * insertSortTotalTime) / age));
//            System.out.println("Quick sort time (nanos): " + ((2 * quickSortTotalTime) / age));
//        }
        colliders.sort(colliderComparator);

        renderBoundsLock.unlock();
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
        renderBoundsLock.lock();
        colliders.parallelStream().forEach(Collider::handleColliders);
        colliders.forEach(c -> c.collision.clear());
        renderBoundsLock.unlock();
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

    private static void parallelEvenOddSort(List<Collider> arr) {
        int maxIndex = arr.size()/2;
        IntStream.range(0, maxIndex).parallel().forEach(i -> {
            if (arr.get(i*2).bounds.x > arr.get((i*2)+1).bounds.x) {
                Collider c = arr.get(i*2);
                arr.set(i*2, arr.get((i*2)+1));
                arr.set((i*2)+1, c);
            }
        });
        if (arr.size() % 2 == 0) maxIndex--;
        IntStream.range(0, maxIndex).parallel().forEach(i -> {
            if (arr.get((i*2)+1).bounds.x > arr.get((i*2)+2).bounds.x) {
                Collider c = arr.get((i*2)+1);
                arr.set((i*2)+1, arr.get((i*2)+2));
                arr.set((i*2)+2, c);
            }
        });
    }

    private boolean isSorted(List<Collider> arr) {
        for (int i = 0; i < arr.size()-1; i++) {
            if (arr.get(i).bounds.x > arr.get(i+1).bounds.x)
                return false;
        }
        return true;
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer) {
        drawLock.lock();
        for (PhysicsObject o: objects) {
            o.draw(batch, shapeDrawer, null, 1f);
        }
        drawLock.unlock();
    }

    public void renderBounds(ShapeDrawer shapeDrawer) {
        renderBoundsLock.lock();
        for (Collider c: colliders) {
            c.renderBounds(shapeDrawer);
        }
        renderBoundsLock.unlock();
    }

    public void move(float dt) {
//        for (PhysicsObject o: objects) {
//            o.move(dt, null);
//        }
        objects.parallelStream().forEach(o -> o.move(dt, null));
    }

    public void addObject(PhysicsObject o) {
        drawLock.lock();
        objects.add(o);
        drawLock.unlock();
    }


    public Creature getCreature(int x, int y) {
        renderBoundsLock.lock();
        int nearestIndex = 0;
        int closest = Integer.MAX_VALUE;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof Creature) {

                int distToCreature = (int) Math.sqrt(((x - objects.get(i).getX())) * (x - objects.get(i).getX()) + ((y - objects.get(i).getY()) * (y - objects.get(i).getY())));
                if (distToCreature <= closest) {
                    closest = distToCreature;
                    nearestIndex = i;
                }
            }
        }
        renderBoundsLock.unlock();
        return (Creature) objects.get(nearestIndex);
    }
}
