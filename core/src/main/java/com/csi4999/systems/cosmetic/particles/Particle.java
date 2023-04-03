package com.csi4999.systems.cosmetic.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Particle {
    public Vector2 position;
    public Vector2 velocity;
    public Sprite sprite;
    public Color color;

    public boolean removeQueued = false;

    public float maxAge = 1;
    public float age = 0;

    public float maxOpacity = 1f;

    public float diameter;

    public Particle(Vector2 position, Vector2 velocity, Sprite sprite, Color color, float diameter) {
        this.position = position;
        this.velocity = velocity;
        this.sprite = sprite;
        this.color = color;
        this.diameter = diameter;

        sprite.setOriginCenter();
    }

    public void move(float dt) {
        position.mulAdd(velocity, dt);
        if (age >= maxAge) removeQueued = true;
        age += dt;
    }

    public void render(Batch batch) {
        float opacity = 1 - (age / maxAge);
        if (opacity > 0) {
            sprite.setOriginBasedPosition(position.x, position.y);
            sprite.setScale(diameter * opacity / sprite.getWidth());
            sprite.setColor(color.r, color.g, color.b, opacity * maxOpacity);
            sprite.draw(batch);
        }

    }
}
