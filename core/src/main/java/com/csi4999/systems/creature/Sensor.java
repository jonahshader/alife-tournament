package com.csi4999.systems.creature;

import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.PhysicsEngine;

public interface Sensor extends Mutable, CreatureComponent {
    float[] read();
    void remove(PhysicsEngine engine);
}
