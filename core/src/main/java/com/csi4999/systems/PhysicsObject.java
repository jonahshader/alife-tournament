package com.csi4999.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;

public abstract class PhysicsObject {

    public Vector2 velocity;
    public Vector2 acceleration;
    public Vector2 position;

    private Matrix4 oldTransform;
    protected Matrix4 computedTransform;

    protected Vector3 transformedPos;
    protected Vector3 transformedScale;

    public Color color;

    public Vector2 scale;

    public float rotationDegrees;

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

    public void move(float dt) {
        velocity.mulAdd(acceleration, dt);
        position.mulAdd(velocity, dt);

        children.forEach(child -> child.move(dt));
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer, PhysicsObject parent, float parentAlpha) {
        applyTransform(batch, computeTransform(parent));
        draw(batch, shapeDrawer, parentAlpha);
        parentAlpha *= this.color.a;
        float finalParentAlpha = parentAlpha;
        children.forEach(child -> child.draw(batch, shapeDrawer, this, finalParentAlpha));
        resetTransform(batch);
    }

    abstract void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha);

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

    private void commonInit() {
        this.children = new ArrayList<>();
        this.worldTransform = new Affine2().idt();
        this.scale = new Vector2(1, 1);
        this.oldTransform = new Matrix4();
        this.computedTransform = new Matrix4();
        this.color = new Color(1, 1, 1, 1);
        this.transformedPos = new Vector3();
        this.transformedScale = new Vector3();
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

    private void resetTransform (Batch batch) {
        batch.setTransformMatrix(oldTransform);
    }

}
