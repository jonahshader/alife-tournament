package com.csi4999.systems.physics;

import com.csi4999.systems.PhysicsObject;

public abstract class Circle extends Collider {
    float radius;
    @Override
    public boolean collidesWith(Collider other) {
        if (other instanceof Circle) {
            Circle o = (Circle) other;
            return o.position.dst2(position) < (o.radius + radius) * (o.radius + radius);
        } else {
            return false;
        }
    }
}
