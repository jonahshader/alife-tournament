package com.csi4999.systems;

import com.badlogic.gdx.math.Vector2;
public class PhysicsObject {

    Vector2 velocity;
    Vector2 acceleration;
    float pos_X, pos_Y;


    public PhysicsObject(float initial_vX, float initial_vY, float initial_aX, float initial_aY,
                         float initial_pX, float initial_pY) {

        this.velocity = new Vector2(initial_vX , initial_vY);
        this.acceleration = new Vector2(initial_aX , initial_aY);
        this.pos_X = initial_pX;
        this.pos_Y = initial_pY;
    }

    public PhysicsObject(Vector2 velocity , Vector2 acceleration, float initial_pX, float initial_pY) {

        this.velocity = velocity;
        this.acceleration = acceleration;
        this.pos_X = initial_pX;
        this.pos_Y = initial_pY;
    }

    public PhysicsObject() {
        velocity = new Vector2(0,0);
        acceleration = new Vector2(0,0);
        this.pos_X = 0;
        this.pos_Y = 0;
    }
    public void move(float dt) {

        velocity = velocity.mulAdd(acceleration, dt);
        this.pos_X += velocity.x * dt;
        this.pos_Y += velocity.y * dt;

    }
    public float getX () {
        return pos_X;
    }

    public void setX (float X) {
        if (pos_X != X) {
            pos_X = X;
        }
    }

    public float getY () {
        return  pos_Y;
    }

    public void setY (float Y) {
        if (pos_Y != Y) {
            pos_Y = Y;
        }
    }

}
