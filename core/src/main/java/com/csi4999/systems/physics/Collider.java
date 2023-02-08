package com.csi4999.systems.physics;

import com.badlogic.gdx.math.Rectangle;
import com.csi4999.systems.PhysicsObject;

import java.util.List;

public abstract class Collider extends PhysicsObject {
    abstract public boolean collidesWith(Collider other);
    abstract public Rectangle getBounds();
    abstract public void receiveActiveColliders(List<Collider> activeColliders);

    // default implementation
    public float[] getVisionVector() {
        return null;
    }
}
