package com.csi4999.systems.cosmetic.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public abstract class Particle {
    public Vector2 position;
    public Vector2 velocity;
    public Sprite sprite;

    public boolean removeQueued;

    public void move(float dt) {
        position.mulAdd(velocity, dt);
        sprite.setOriginBasedPosition(position.x, position.y);
    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
