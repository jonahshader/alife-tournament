package com.csi4999.systems.creature;

import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.PhysicsEngine;

public interface Sensor extends Mutable, CreatureComponent {
    float[] read();

    // copy is similar to SensorBuilder.buildSensor
    // needs to add it as a child and add to physics engine if applicable
    Sensor copySensor(Creature newParent, PhysicsEngine engine);
}
