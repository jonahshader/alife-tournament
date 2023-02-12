package com.csi4999.systems.creature;

import com.csi4999.systems.physics.PhysicsEngine;

import java.util.Random;

public interface SensorBuilder {
    // constructs a sensor and adds it to the engine if applicable
    Sensor buildSensor(Creature parent, PhysicsEngine engine, Random rand);
}
