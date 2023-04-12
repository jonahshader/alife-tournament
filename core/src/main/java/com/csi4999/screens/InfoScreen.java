package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class InfoScreen implements Screen {
    private Skin skin;
    private final OrthographicCamera menuCam;
    private final FitViewport menuViewport;
    private final ALifeApp app;
    private Stage stage;

    public InfoScreen(ALifeApp app) {
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);

        menuCam = new OrthographicCamera();
        menuViewport = new FitViewport(480, 360, menuCam);

        menuCam.position.set(menuCam.viewportWidth/2, menuCam.viewportHeight/2, 0);
        menuCam.update();

        stage = new Stage(menuViewport, app.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table mainTable = new Table();
//        mainTable.setFillParent(true);
        mainTable.setSize(menuViewport.getWorldWidth(), menuViewport.getWorldHeight());
        mainTable.center();

        Label infoLabel = new Label("Information", skin);

        Label info1 = new Label("This is a game about training creatures to fight in tournaments. Press 'Play' from the main menu to start training new creatures. Start a tournament by clicking 'Tournament' from play, then select the creatures you want in the tournament. Get money by having more creatures remaining by the end of the tournament. Use that money to buy upgrades in the shop to evolve better creatures.", skin);
        info1.setWrap(true);

        TextButton back = new TextButton("Back", skin);

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.pop();
            }
        });

        mainTable.row().pad(35);
        mainTable.add(infoLabel).center();
        mainTable.row().pad(35);
        mainTable.add(info1).width(menuViewport.getWorldWidth() - 70);
        mainTable.row().pad(35);
        mainTable.add(back);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.setProjectionMatrix(menuCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(25,25, menuViewport.getWorldWidth() - 50, menuViewport.getWorldHeight() - 50);
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
    }
}
