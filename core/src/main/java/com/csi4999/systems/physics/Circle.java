package com.csi4999.systems.physics;

import com.badlogic.gdx.math.Vector2;

public abstract class Circle extends Collider {
    /**
     * Circle collider. Radius specifies the pre-transform radius.
     */
    public float radius;
    float transformedRadius;

    public Circle() {}

    public Circle(float radius) {
        this.radius = radius;
        this.transformedRadius = 0;
    }

    public Circle(float initial_pX, float initial_pY, float initial_vX, float initial_vY, float initial_aX, float initial_aY, float radius) {
        super(initial_pX, initial_pY, initial_vX, initial_vY, initial_aX, initial_aY);
        this.radius = radius;
        this.transformedRadius = 0;
    }

    public Circle(Vector2 position, Vector2 velocity, Vector2 acceleration, float radius) {
        super(position, velocity, acceleration);
        this.radius = radius;
        this.transformedRadius = 0;
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other instanceof Circle) {
            return circleCheck((Circle) other);
        } else {
            return false;
        }
    }

    private boolean circleCheck(Circle o) {
        return (o.transformedPos.x - transformedPos.x) * (o.transformedPos.x - transformedPos.x) +
            (o.transformedPos.y - transformedPos.y) * (o.transformedPos.y - transformedPos.y) <=
            (o.transformedRadius + transformedRadius) * (o.transformedRadius + transformedRadius);
    }

    @Override
    public void computeBounds() {
        // hopefully scale is uniform. take the max just in case
        transformedRadius = radius * Math.max(Math.abs(transformedScale.x), Math.abs(transformedScale.y));

        bounds.setSize(transformedRadius * 2);
        bounds.setCenter(transformedPos.x, transformedPos.y);
    }

//    abstract void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha);
}
