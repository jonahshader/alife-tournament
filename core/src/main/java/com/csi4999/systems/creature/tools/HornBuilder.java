package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.Random;

public class HornBuilder implements ToolBuilder {
    @Override
    public Tool buildTool(Creature parent, PhysicsEngine engine, Random rand) {
        Vector2 pos = new Vector2().setZero();
        float r = (float) (parent.radius * Math.sqrt(rand.nextFloat()));
        float theta = (float) (rand.nextFloat() * 2 * Math.PI);
        pos.set((float) (Math.cos(theta) * r), (float) (Math.sin(theta) * r));

        Horn h = new Horn(pos);
        h.rotationDegrees = rand.nextFloat() * 360;
        parent.getChildren().add(h);

        return h;
    }
}
