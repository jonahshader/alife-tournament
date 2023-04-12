package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlagellaBuilder implements ToolBuilder {
    @Override
    public List<Tool> buildTool(Creature parent, PhysicsEngine engine, Random rand) {
        Vector2 pos = new Vector2().setZero();
        float r = (float) (parent.radius * Math.sqrt(rand.nextFloat()));
        float theta = (float) (rand.nextFloat() * 2 * Math.PI);
        pos.set((float) (Math.cos(theta) * r), (float) (Math.sin(theta) * r));

        Flagella f = new Flagella(pos);
        f.rotationDegrees = rand.nextFloat() * 360;
        parent.getChildren().add(f);

        List<Tool> newTools = new ArrayList<>();
        newTools.add(f);
        return newTools;
    }
}
