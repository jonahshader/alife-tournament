package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.singletons.CustomAssetManager.UI_FONT;

public class NameDescriptionScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;
    private BitmapFont titleFont;
    private Color titleFontColor;

    private InputProcessor oldInputProcessor;


    public interface NameDescriptionCallback {
        void callback(String name, String description);
    }

    public NameDescriptionScreen(ALifeApp app, String prompt, String submitButtonText, NameDescriptionCallback callback) {
        this.app = app;

        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        titleFont = CustomAssetManager.getInstance().manager.get(UI_FONT);
        titleFontColor = new Color(1f, 1f, 1f, 1f);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(400, 400, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);
        oldInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(stage);

        // make ui
        Table t = new Table();
        Table body = new Table();
        t.setFillParent(true);
        t.top();
//        body.setFillParent(true);
//        body.top();

        body.align(Align.center);
        t.align(Align.center);

        Label nameLabel = new Label("Name", skin);
        Label descriptionLabel = new Label("Description", skin);
        TextField nameField = new TextField("", skin);
        TextArea descriptionField = new TextArea("", skin);
        descriptionField.setAlignment(Align.topLeft);
        TextButton submitButton = new TextButton(submitButtonText, skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y) {
                callback.callback(nameField.getText(), descriptionField.getText());
                ScreenStack.pop(); // go back
            }
        });

        cancelButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y) {
                ScreenStack.pop(); // go back
            }
        });

        int vPadAmount = 10;
        int hPadAmount = 3;
//        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
//        body.add(promptLabel).fill().uniform();
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        body.add(nameLabel).fill();
        body.add(nameField).fill();
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        body.add(descriptionLabel).top();
        body.add(descriptionField).fillX().height(120);
        body.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        body.add(submitButton).fill();
        body.add(cancelButton).fill();

        Label promptLabel = new Label(prompt, skin);

        t.row().pad(0, hPadAmount, vPadAmount, hPadAmount);
        t.add(promptLabel).center();
        t.row();
        t.add(body).fill();

        stage.addActor(t);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(menuCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(25,25, menuViewport.getWorldWidth() - 50, menuViewport.getWorldHeight() - 50); // Why are these the values that produce a somewhat symmetrical result?
        app.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        menuCam.update();
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
        stage.dispose();
        Gdx.input.setInputProcessor(oldInputProcessor); // go back to using the prior input processor
    }
}
