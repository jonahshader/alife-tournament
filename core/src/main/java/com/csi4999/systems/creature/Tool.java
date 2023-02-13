package com.csi4999.systems.creature;

import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.PhysicsEngine;

public interface Tool extends Mutable {
    void use(float strength, float dt, Creature parent);
    void remove(PhysicsEngine engine);
}
