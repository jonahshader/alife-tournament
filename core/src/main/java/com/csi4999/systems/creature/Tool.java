package com.csi4999.systems.creature;

import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.PhysicsEngine;

public interface Tool extends Mutable, CreatureComponent {
    void use(float strength, float dt, Creature parent);

    // copy is similar to ToolBuilder.buildTool
    // needs to add it as a child and add to physics engine if applicable
    Tool copyTool(Creature newParent, PhysicsEngine engine);
}
