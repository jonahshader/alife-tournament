package com.csi4999.systems.creature.sensors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.environment.Food;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.LineSegment;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class Eye extends LineSegment implements Sensor {
    private float[] visionData;
    private static final float MUTATE_LENGTH_STD = 0.5f;
    private static final float MUTATE_ROTATION_STD = 0.5f;

    private static final float ENERGY_CONSUMPTION = 0.025f; // energy per second

    private Color colorTransparent;
    private Collider parent;

    private float lastHitDist;

    public Eye(Eye e) {
        super(e.lineLength);
        lastHitDist = e.lastHitDist;
        position.set(e.position);
        visionData = e.visionData.clone(); // clone is shallow but its fine because its floats
        colorTransparent = new Color(e.colorTransparent);
        rotationDegrees = e.rotationDegrees;

    }

    public Eye() {} //Kryo

    // Constructor will have to take in parent creature info
    public Eye(Vector2 pos, float lineLength, Collider parent) {
        super(lineLength);
        lastHitDist = lineLength;
        this.parent = parent;
        position.set(pos);
        // similarity score, isFood, distance
        visionData = new float[] {-1f, 0f, 0f};
        colorTransparent = new Color();
    }

    @Override
    public void mutate(float amount, Random rand) {
        super.mutate(amount, rand);
        // Wiggle vision line length
        lineLength += rand.nextGaussian() * MUTATE_LENGTH_STD;
        rotationDegrees += rand.nextGaussian() * MUTATE_ROTATION_STD;
    }

    @Override
    public float[] read() {
        return visionData;
    }

    @Override
    public Sensor copy(Creature newParent, PhysicsEngine engine) {
        Eye e = new Eye(this);
        e.parent = newParent;
        newParent.getChildren().add(e);
        engine.addCollider(e);
        return e;
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        shapeDrawer.setColor(color.r, color.g, color.b, parentAlpha);
        colorTransparent.set(color);
        colorTransparent.a = 0;
        shapeDrawer.filledRectangle(0f, -.5f, lastHitDist, 1f, colorTransparent, color);
//        shapeDrawer.filledCircle(0f,0f,3f);
        Sprite circle = CustomGraphics.getInstance().circle;
        circle.setScale(2 * 2f / circle.getWidth());
        circle.setOriginBasedPosition(0f, 0f);
        circle.setColor(color);
        circle.draw(batch);
    }

    @Override
    public void handleColliders() {

        if (collision.size() <= 1) {
            Arrays.fill(visionData, 0f);
            visionData[0] = -1; // similarity defaults to 0
            lastHitDist = lineLength;
            this.color.set(1f, 1f, 1f, 1f);
        } else {
            collision.sort((o1, o2) -> {
                float d1 = getDistToCollider(o1);
                float d2 = getDistToCollider(o2);
                return Float.compare(d1, d2);
            });
            Collider nearest = collision.get(0);
            if (nearest == parent)
                nearest = collision.get(1);
            lastHitDist = (float) Math.sqrt(getDistToCollider(nearest)) / parent.scale.x;
            visionData[0] = nearest.getSimilarity(parent);
            visionData[1] = nearest instanceof Food ? 1f : -1f;
            if (nearest instanceof Circle) {
                visionData[2] = lastHitDist - (((Circle) nearest).radius  / parent.scale.x);
            } else {
                visionData[2] = lastHitDist;
            }
            visionData[2] = (lineLength - visionData[2]) / lineLength;

            // Line changes to color of seen object
            this.color.set(nearest.color);
        }


    }

    private float getDistToCollider(Collider c) {
//        return (transformedPos.x - c.transformedPos.x) * (transformedPos.x - c.transformedPos.x) +
//            (transformedPos.y - c.transformedPos.y) * (transformedPos.y - c.transformedPos.y);
        return transformedPos.dst2(c.transformedPos);
    }

    @Override
    public float getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }
}
