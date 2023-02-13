package com.csi4999.systems.environment;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.List;

public class Environment {
    PhysicsEngine physics;
    // Environment will eventually have all sorts of spawners in it (food, creature, artifacts)

    List<SensorBuilder> availableSensors;
    List<ToolBuilder> availableTools;

    public Environment() {
        this.physics = new PhysicsEngine();

    }

    public void drawObjects(ShapeDrawer drawer, Batch batch) {
        physics.draw(batch, drawer);
    }

    public void moveObjects(float dt) {
        physics.move(dt);
    }

}
