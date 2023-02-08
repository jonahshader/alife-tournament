package com.csi4999.systems.physics;


public abstract class Line extends Collider {

    @Override
    public boolean collidesWith(Collider other) {
        return false;
    }
}
