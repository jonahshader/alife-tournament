package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GripperBuilder implements ToolBuilder {
    @Override
    public List<Tool> buildTool(Creature parent, PhysicsEngine engine, Random rand) {
        Vector2 pos = new Vector2().setZero();
        float r = (float) (parent.radius * Math.sqrt(rand.nextFloat()));
        float theta = (float) (rand.nextFloat() * 2 * Math.PI);
        pos.set((float) (Math.cos(theta) * r), (float) (Math.sin(theta) * r));

        Gripper g = new Gripper(pos, parent);
        engine.addCollider(g);
        parent.getChildren().add(g);

        List<Tool> t = new ArrayList<>();
        t.add(g);
        return t;
    }

    private float lineLength(int segment, Random rand) {
        return ((float) Math.sqrt(rand.nextFloat()) * 80f + 10f) / (segment * .2f + 1f);
    }
}
