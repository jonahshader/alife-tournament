package com.csi4999.systems.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class ToolBar implements InputProcessor, Disposable {

    private Skin skin;
    private OrthographicCamera cam;
    private ExtendViewport viewport;
    private Stage stage;

    public ToolBar(Batch batch) {
        cam = new OrthographicCamera();
        viewport = new ExtendViewport(640, 360, cam);
        stage = new Stage(viewport, batch);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

        Table mainTable = new Table();
    }

    public void render(Batch batch) {

    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        cam.update();
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
