package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;

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
        menuViewport = new FitViewport(640, 360, menuCam);

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

        Label info1 = new Label("This is a game about training creatures to fight in tournaments. Train new creatures by going to 'Training'. Start a tournament by clicking 'Tournament' and selecting a region with creatures. Get money by having more creatures left by the end of the tournament. Use that money to buy upgrades in the shop to evolve better creatures.", skin);
        info1.setWrap(true);

//        info1.setSize(menuViewport.getWorldWidth(), menuViewport.getWorldHeight());
//        info1.setWidth(menuViewport.getWorldWidth());
//        System.out.println(menuViewport.getWorldWidth());

        mainTable.row().pad(35);
//        mainTable.add(info1).setActorWidth(menuViewport.getWorldWidth());
        mainTable.add(info1).width(menuViewport.getWorldWidth() - 70);

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
