package com.csi4999.systems.creature;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;
import java.util.Random;

public class Creature extends Circle implements Mutable {
    private List<Sensor> sensors;
    private List<Tool> tools;
    @Override
    public void mutate(float amount, Random rand) {

    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {

    }

    @Override
    public void receiveActiveColliders(List<Collider> activeColliders) {

    }
}
