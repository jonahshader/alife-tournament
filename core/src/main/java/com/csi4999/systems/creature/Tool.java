package com.csi4999.systems.creature;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.csi4999.systems.Mutable;
import com.csi4999.systems.physics.PhysicsEngine;

public abstract class Tool extends Group implements Mutable {
    public abstract void use(float strength);
    public abstract void removeFromEngine(PhysicsEngine engine); // for tools that are also Collider
}
