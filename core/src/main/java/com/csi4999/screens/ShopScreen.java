package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csi4999.ALifeApp;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.networking.GameClient;
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
    Pixmap bgPixmap;
    Color shopElementColor;

    public ShopScreen(ALifeApp app) {
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        shopElementColor = new Color(.26f, .28f, .36f, 1);
        shopCam = new OrthographicCamera();
        shopViewport = new FitViewport(1280, 720, shopCam);
        shopCam.position.set(shopCam.viewportWidth/2, shopCam.viewportHeight/2, 0);
        shopCam.update();

        stage = new Stage(shopViewport, app.batch);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        //bgPixmap.setColor(Color.BLACK);
        bgPixmap.setColor(shopElementColor);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        Table mainTable = new Table();
        Table topBarTable = new Table();
        Table shopItemsTable = new Table();
        Table descriptionBarTable = new Table();
        Table transactionTable = new Table();

        List<ShopItem> items = makeShopItems();

        mainTable.setFillParent(true);
        mainTable.align(Align.top);


        Label currencyLabel = new Label("Money: " + GameClient.getInstance().user.money, skin);
        currencyLabel.setFontScale(1.25f);

        Label shopLabel = new Label("SHOP", skin);
        shopLabel.setFontScale(1.25f);

        Label descriptionLabel = new Label("Select a component", skin);
        descriptionLabel.setWrap(true);

        Label costLabel = new Label("Cost: ", skin);

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

        // Buy and sell buttons
        transactionTable.row();
        transactionTable.add(buyButton).uniform().fill();
        transactionTable.row();
        transactionTable.add(sellButton).uniform().fill();

        // Make each shop item
        for (int row = 0; row < 3; row++) {
            shopItemsTable.row().align(Align.center);
            for (int col = 0; col < 3; col++) {
                Table wrap = new Table();
                Table item = items.get((row * 3) + col).makeComponent(skin, descriptionLabel, costLabel, buyButton, sellButton, currencyLabel);
                wrap.add(item);
                item.setBackground(textureRegionDrawableBg);
                wrap.pad(25,55,25,55);
                //shopItemsTable.add(items.get((row * 3) + col).makeComponent(skin, descriptionLabel, costLabel, buyButton, sellButton)).align(Align.center);
                shopItemsTable.add(wrap).align(Align.center);
            }
        }


        topBarTable.add(currencyLabel).width(400);
        topBarTable.add(shopLabel).width(400);
        topBarTable.add(returnButton).setActorWidth(400); // .width makes the button stretch

        descriptionBarTable.setBackground(textureRegionDrawableBg);
        descriptionBarTable.add(descriptionLabel).align(Align.left).width(450).pad(0,55,0,55);
        descriptionBarTable.add(costLabel).pad(0,55,0,55).size(50, 100);
        descriptionBarTable.add(transactionTable).size(50, 100).pad(0,0,0,25);

        mainTable.add(topBarTable).align(Align.center).pad(10, 0, 55, 0);
        mainTable.row();
        mainTable.add(shopItemsTable).align(Align.center).pad(0,0,55,0);
        mainTable.row();
        mainTable.add(descriptionBarTable).align(Align.center);

        stage.addActor(mainTable);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        app.batch.setProjectionMatrix(shopCam.combined);
        app.batch.begin();
        app.shapeDrawer.setColor(.18f, .2f, .28f, 1);

        app.shapeDrawer.filledRectangle(95,45, shopViewport.getWorldWidth() - 190, shopViewport.getWorldHeight() - 90);

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
