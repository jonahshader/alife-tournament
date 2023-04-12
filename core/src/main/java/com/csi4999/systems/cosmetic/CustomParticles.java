package com.csi4999.systems.cosmetic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.cosmetic.particles.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class CustomParticles {
    private static Random r = new RandomXS128();
    private static List<Particle> particles = new ArrayList<>();

    private static ReentrantLock lock = new ReentrantLock();

    public static boolean enabled = true;

    public static void update(float dt) {
        lock.lock();
        particles.parallelStream().forEach(it -> it.move(dt));
//        particles.forEach(it -> it.move(dt));
        particles.removeIf(it -> it.removeQueued);
        lock.unlock();
    }

    public static void addParticle(Color color, Vector2 pos, Vector2 vel, float radius, float velNoise, float maxAge, float maxOpacity) {
        if (enabled) {
            lock.lock();
            vel.add((float) (r.nextGaussian() * velNoise), (float) (r.nextGaussian() * velNoise));
            Particle p = new Particle(pos, vel, new Sprite(CustomGraphics.getInstance().circle), color, radius);
            p.maxOpacity = maxOpacity;
            p.maxAge = maxAge;
            particles.add(p);
            lock.unlock();
        }
    }

    public static void render(Batch batch) {
        lock.lock();
//        for (int i = 0; i < particles.size(); i += 8000) {
//            batch.begin();
//            for (int j = i; j < i + 8000 && j < particles.size(); j++) {
//                particles.get(j).render(batch);
//            }
//            batch.flush();
//            batch.end();
//        }
        particles.forEach(it -> it.render(batch));
//        for (Particle p : particles) p.render(batch);
        lock.unlock();
    }

}
