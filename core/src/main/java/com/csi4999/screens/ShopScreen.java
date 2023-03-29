package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.shop.ShopItem;

import java.util.List;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;
import static com.csi4999.systems.shop.ShopStuffKt.makeShopItems;

public class ShopScreen implements Screen {

    private Skin skin;
    private final OrthographicCamera shopCam;
    private final FitViewport shopViewport;
    private final ALifeApp app;
    private Stage stage;

    public ShopScreen(ALifeApp app) {
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        shopCam = new OrthographicCamera();
        shopViewport = new FitViewport(1280, 720, shopCam);
        shopCam.position.set(shopCam.viewportWidth/2, shopCam.viewportHeight/2, 0);
        shopCam.update();

        stage = new Stage(shopViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Table mainTable = new Table();
        Table shopItemsTable = new Table();
        List<ShopItem> items = makeShopItems();



        mainTable.setFillParent(true);
        mainTable.align(Align.top);


        Label currencyLabel = new Label("Coins: 5", skin);
        currencyLabel.setFontScale(1.25f);

        Label shopLabel = new Label("SHOP", skin);
        shopLabel.setFontScale(1.25f);

        Label descriptionLabel = new Label("", skin);
        Label costLabel = new Label("", skin);

        Button returnButton = new TextButton("Return", skin);
        returnButton.setScale(1.25f);

        Button buyButton = new TextButton("Buy", skin);
        Button sellButton = new TextButton("Sell", skin);

        returnButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenStack.switchTo(new MainMenuScreen(app));
            }
        });

        buyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Purchased");
            }
        });

        shopItemsTable.add(items.get(0).makeComponent(skin, descriptionLabel, costLabel, buyButton, sellButton));

        mainTable.row().pad(15, 0, 0, 0);
        mainTable.add(currencyLabel).width(400);
        mainTable.add(shopLabel).width(400);
        mainTable.add(returnButton).width(100);
        mainTable.row().pad(30, 0, 0, 0);
        mainTable.add(shopItemsTable);


        stage.addActor(mainTable);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        app.batch.setProjectionMatrix(shopCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(35,45, shopViewport.getWorldWidth() - 70, shopViewport.getWorldHeight() - 90);
        app.shapeDrawer.setColor(.26f, .28f, .36f, 1);
        app.shapeDrawer.filledRectangle(75,75, shopViewport.getWorldWidth() - 150, shopViewport.getWorldHeight() - 600);
        app.shapeDrawer.setColor(0f, 0f, 0f, 1);
        app.shapeDrawer.filledRectangle(1035, 95, 125, 75);
        app.shapeDrawer.setColor(.90f, .90f, 0f, 1);
        app.shapeDrawer.filledRectangle(1040, 100, 115, 65);
        app.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        shopCam.update();
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
