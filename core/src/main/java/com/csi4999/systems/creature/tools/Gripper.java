package com.csi4999.systems.creature.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.csi4999.singletons.CustomGraphics;
import com.csi4999.systems.PhysicsObject;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.Sensor;
import com.csi4999.systems.creature.Tool;
import com.csi4999.systems.environment.Food;
import com.csi4999.systems.physics.Circle;
import com.csi4999.systems.physics.Collider;
import com.csi4999.systems.physics.PhysicsEngine;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Gripper extends Circle implements Tool {
    private PhysicsObject holding;
    private Creature creatureParent;

    private boolean tryHolding = true;

    private static final Color holdColor = new Color(.8f, 1f, .8f, .25f);

    public Gripper() {}

    public Gripper(Vector2 pos, Creature creatureParent) {
        super(pos, 3);
        this.creatureParent = creatureParent;
        color = new Color(.6f, .4f, .4f, .5f);
    }

    public Gripper(Gripper g) {
        super(g.position.cpy(), g.radius);
        computedTransform.set(g.computedTransform);
        oldTransform.set(g.oldTransform);
        worldTransform.set(g.worldTransform);
        color = new Color(g.color);
    }


    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer, float parentAlpha) {
        Sprite circle = CustomGraphics.getInstance().circle;
        if (holding != null) {
            circle.setColor(holdColor);
            circle.setScale(radius * 20f / circle.getWidth());
        } else {
            circle.setColor(color);
            circle.setScale(radius * 2f / circle.getWidth());
        }

        circle.setOriginBasedPosition(0f, 0f);
        circle.draw(batch);
    }

    @Override
    public void move(float dt, PhysicsObject parent) {
        computeTransform(parent);
        computeBounds();
        if (tryHolding && holding != null && !holding.removeQueued) {
            holding.position.set(transformedPos.x, transformedPos.y);
        } else {
            holding = null;
        }
        getChildren().forEach(child -> child.move(dt, this));
    }


    @Override
    public float getEnergyConsumption() {
        return 0;
    }

    @Override
    public void use(float strength, float dt, Creature parent) {
        tryHolding = strength > 0;
    }

    @Override
    public Tool copyTool(Creature newParent, PhysicsEngine engine) {
        Gripper g = new Gripper(this);
        g.creatureParent = newParent;
        engine.addCollider(g);
        newParent.getChildren().add(g);
        return g;
    }

    @Override
    public void handleColliders() {
        if (tryHolding && !collision.isEmpty()) {
            holding = collision.get(0);
        }
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (tryHolding && holding == null) {
//            if (other instanceof Food || other instanceof Creature)
//                if (other != creatureParent)
//                    return circleCheck((Circle) other);
            if (other instanceof Food)
                return circleCheck((Circle) other);
        }

        return false;
    }
}
