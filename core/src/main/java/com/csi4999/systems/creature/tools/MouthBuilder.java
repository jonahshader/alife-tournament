package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.math.Vector2;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;

import java.util.Random;

public class MouthBuilder implements ToolBuilder {
    @Override
    public Tool buildTool(Creature parent, PhysicsEngine engine, Random rand) {
        float rot = rand.nextFloat() * 360;
        Vector2 pos = new Vector2().set(parent.radius, 0f).rotateDeg(rot);
        Mouth m = new Mouth(pos, rot, parent);
        parent.getChildren().add(m);
        engine.addCollider(m);

        return m;
    }
}
