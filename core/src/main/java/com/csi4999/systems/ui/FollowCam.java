package com.csi4999.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.systems.creature.Creature;

public class FollowCam implements InputProcessor {

    private final ExtendViewport viewport;
    private final OrthographicCamera cam;

    private Creature c;

    public FollowCam(ExtendViewport viewport, OrthographicCamera cam) {
        this.viewport = viewport;
        this.cam = cam;
    }

    public void updateCamera() {
        if (this.c != null) {
            cam.position.set(c.position, 0);
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // zoom in/out, centered at mouse pos
        float zoomScalar = (float) Math.pow(1.125f, amountY);
        Vector2 translation = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY())).sub(new Vector2(cam.position.x, cam.position.y));

        cam.translate(translation);
        cam.update();
        cam.zoom *= zoomScalar;
        cam.update();
        cam.translate(translation.scl(-1f * zoomScalar));
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    public void assignCreature(Creature c) {
        this.c = c;
        cam.position.set(c.position, 0);
    }

    public void unassign() {
        this.c = null;
    }
}
