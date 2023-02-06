package com.csi4999.systems;

import com.badlogic.gdx.math.Vector2;
public class PhysicsObject {

    Vector2 velocity;
    Vector2 acceleration;
    Vector2 position;


    public PhysicsObject(float initial_vX, float initial_vY, float initial_aX, float initial_aY,
                         float initial_pX, float initial_pY) {

        this.velocity = new Vector2(initial_vX , initial_vY);
        this.acceleration = new Vector2(initial_aX , initial_aY);
        this.position = new Vector2(initial_pX, initial_pY);
    }

    public PhysicsObject(Vector2 velocity , Vector2 acceleration, Vector2 position) {

        this.velocity = velocity;
        this.acceleration = acceleration;
        this.position = position;
    }

    public PhysicsObject() {    }
    public void move(float dt) {

        velocity.mulAdd(acceleration, dt);
        position.mulAdd(velocity, dt);

    }
    public float getX () {
        return position.x;
    }

    public void setX (float X) {
            position.x = X;
    }

    public float getY () {
        return position.y;
    }

    public void setY (float Y){
            position.y = Y;

    }

}
