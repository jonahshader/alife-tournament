package com.csi4999.systems.creature;

import com.csi4999.systems.physics.PhysicsEngine;

import java.util.Random;

public interface ToolBuilder {
    Tool buildTool(Creature parent, PhysicsEngine engine, Random rand);
}
