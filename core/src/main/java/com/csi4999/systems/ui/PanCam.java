package com.csi4999.systems.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class PanCam implements InputProcessor {
    /**
     * This implements zooming in/out at the current mouse position, and panning by clicking and dragging.
     */
    // TODO: currently works with any mouse button. is this okay?
    // TODO: add configurable zoom constraints, home button

    private int lastTouchX, lastTouchY;

    private final ExtendViewport viewport;
    private final OrthographicCamera cam;

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // perform view panning
        int dx = screenX - lastTouchX;
        int dy = screenY - lastTouchY;
        lastTouchX = screenX;
        lastTouchY = screenY;
        float screenToWorldRatio = Math.min(viewport.getWorldWidth() / (float) Gdx.graphics.getWidth(),
            viewport.getWorldHeight() / (float) Gdx.graphics.getHeight());
        float scaleFactor = cam.zoom * screenToWorldRatio;
        cam.translate(-dx * scaleFactor, dy * scaleFactor, 0);
        return true;
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

    public PanCam(ExtendViewport viewport, OrthographicCamera cam) {
        this.viewport = viewport;
        this.cam = cam;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastTouchX = screenX;
        lastTouchY = screenY;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) { return false; }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
}
