package com.csi4999.systems.physics;


import com.badlogic.gdx.math.Vector3;

public abstract class Line extends Collider {
    private Vector3 line;
    @Override
    public boolean collidesWith(Collider other) {
        return false;
    }
}
