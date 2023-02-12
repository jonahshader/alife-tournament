package com.csi4999.systems.creature.sensors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.LineSegment;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Eye extends LineSegment implements Sensor {
    private float[] visionData;
    private static final float MUTATE_WEIGHT_STD = 0.35f;

    public Eye() {} //Kryo

    // Constructor will have to take in parent creature info
    public Eye(Vector2 line) {
        super(line);
        // r, g, b, similarity score
        visionData = new float[] {0f, 0f, 0f, 0f};
    }

    @Override
    public void mutate(float amount, Random rand) {
        // Wiggle vision line length

        line.x += rand.nextGaussian() * MUTATE_WEIGHT_STD;
        line.y += rand.nextGaussian() * MUTATE_WEIGHT_STD;
    }

    @Override
    public float[] read() {
        return visionData;
    }

    @Override
    public void remove(PhysicsEngine engine) {
        engine.removeCollider(this);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        shapeDrawer.line(0f, 0f, line.x, line.y);
        shapeDrawer.filledCircle(0f,0f,3f);

        // Testing mutation
        mutate(5, new Random());
    }

    @Override
    public void receiveActiveColliders(List<Collider> activeColliders) {

        if (activeColliders.size() == 0) {
            Arrays.fill(visionData, 0f);
        } else {
            visionData[0] = activeColliders.get(0).color.r;
            visionData[1] = activeColliders.get(0).color.g;
            visionData[2] = activeColliders.get(0).color.b;

            // Might need some parent entity data to compare
            visionData[3] = activeColliders.get(0).getSimilarity(visionData);
        }

        // Line changes to color of seen object
        this.color.r = visionData[0];
        this.color.g = visionData[1];
        this.color.b = visionData[2];
    }

}
