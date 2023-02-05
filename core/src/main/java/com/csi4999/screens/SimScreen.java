package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.csi4999.ALifeApp;

public class SimScreen implements Screen, InputProcessor {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    private int lastTouchX, lastTouchY;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();
        app.shapeDrawer.setColor(1f, 1f, 1f, 1f);
        app.shapeDrawer.filledCircle(0f, 0f, 32f);

        app.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastTouchX = screenX;
        lastTouchY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // perform view panning
        // TODO: pull logic into a separate class that implements InputProcessor to make it easier to multiplex inputs in the future
        int dx = screenX - lastTouchX;
        int dy = screenY - lastTouchY;
        lastTouchX = screenX;
        lastTouchY = screenY;
        float screenToWorldRatio = Math.min(worldViewport.getWorldWidth() / (float) Gdx.graphics.getWidth(),
            worldViewport.getWorldHeight() / (float) Gdx.graphics.getHeight());
        System.out.println(screenToWorldRatio);
        float scaleFactor = worldCam.zoom * screenToWorldRatio;
        worldCam.translate(-dx * scaleFactor, dy * scaleFactor, 0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // zoom in/out, centered at mouse pos
        float zoomScalar = (float) Math.pow(1.125f, amountY);
        Vector2 translation = worldViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY())).sub(new Vector2(worldCam.position.x, worldCam.position.y));

        worldCam.translate(translation);
        worldCam.update();
        worldCam.zoom *= zoomScalar;
        worldCam.update();
        worldCam.translate(translation.scl(-1f * zoomScalar));
        return false;
    }
}
