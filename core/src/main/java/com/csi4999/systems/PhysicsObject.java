package com.csi4999.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public abstract class PhysicsObject {

    public Vector2 position;
    public Vector2 velocity;
    public Vector2 acceleration;


    public float rotationDegrees;
    public float rotationalVel;
    public float rotationalAccel;

    protected Matrix4 oldTransform;
    protected Matrix4 computedTransform;

    public Vector3 transformedPos;
    public Vector3 transformedScale;

    public Color color;

    public Vector2 scale;

    public boolean removeQueued;



    public Affine2 worldTransform;

    private List<PhysicsObject> children;

    public PhysicsObject(float initial_pX, float initial_pY, float initial_vX, float initial_vY, float initial_aX, float initial_aY) {
        this.position = new Vector2(initial_pX, initial_pY);
        this.velocity = new Vector2(initial_vX, initial_vY);
        this.acceleration = new Vector2(initial_aX, initial_aY);
        commonInit();
    }

    public PhysicsObject(Vector2 position, Vector2 velocity, Vector2 acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        commonInit();
    }

    public PhysicsObject() {}

    public void move(float dt, PhysicsObject parent) {
        // integrate vel, accel
        velocity.mulAdd(acceleration, dt);
        position.mulAdd(velocity, dt);
        rotationalVel += rotationalAccel * dt;
        rotationDegrees += rotationalVel * dt;
        computeTransform(parent);
        // move children
        children.forEach(child -> child.move(dt, this));
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer, PhysicsObject parent, float parentAlpha) {
        applyTransform(batch, computedTransform);
        // draw this
        draw(batch, shapeDrawer, parentAlpha);
        parentAlpha *= this.color.a;
        float finalParentAlpha = parentAlpha;
        // draw children
        children.forEach(child -> child.draw(batch, shapeDrawer, this, finalParentAlpha));
        resetTransform(batch);
    }

    // this is for rendering this object. transformations are applied prior to this method call,
    // so drawing something at (0, 0) will be drawn at the transformed position.
    // this needs to be implemented by concrete classes
    abstract public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha);

    public float getX() {
        return position.x;
    }

    public void setX(float X) {
        position.x = X;
    }

    public float getY() {
        return position.y;
    }

    public void setY(float Y) {
        position.y = Y;
    }

    public List<PhysicsObject> getChildren() {
        return children;
    }

    public void queueRemoval() {
        removeQueued = true;
        children.forEach(PhysicsObject::queueRemoval);
    }

    private void commonInit() {
        this.children = new ArrayList<>();
        this.worldTransform = new Affine2().idt();
        this.scale = new Vector2(1, 1);
        this.oldTransform = new Matrix4();
        this.computedTransform = new Matrix4();
        this.color = new Color(1, 1, 1, 1);
        this.transformedPos = new Vector3();
        this.transformedScale = new Vector3();
        this.rotationDegrees = 0f;
        this.rotationalVel = 0;
        this.rotationalAccel = 0f;
        this.removeQueued = false;
    }

    // assumes the parent's worldTransform is accurate
    private Matrix4 computeTransform(PhysicsObject parent) {
        worldTransform.setToTrnRotScl(position, rotationDegrees, scale);
        if (parent != null)
            worldTransform.preMul(parent.worldTransform);
        computedTransform.set(worldTransform);

        // get translation and scale for collision use
        computedTransform.getTranslation(transformedPos);
        computedTransform.getScale(transformedScale);

        return computedTransform;
    }

    private void applyTransform(Batch batch, Matrix4 transform) {
        oldTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform);
    }

    protected void resetTransform(Batch batch) {
        batch.setTransformMatrix(oldTransform);
    }

}
