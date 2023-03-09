package com.csi4999.systems.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.csi4999.screens.NameDescriptionScreen;
import com.csi4999.screens.SimScreen;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.packets.SaveEnvironmentPacket;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class ToolBar implements InputProcessor, Disposable {
    private static final int WIDTH = (int) (360 * 1.5f);
    private static final int HEIGHT = (int) (360 * 1.5f);

    private Skin skin;
    private OrthographicCamera cam;
    private ExtendViewport viewport;
    public Stage stage;

    private Table mainTable;

    private SimScreen sim;

    public ToolBar(Batch batch, SimScreen sim) {
        this.sim = sim;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WIDTH, HEIGHT, cam);
        stage = new Stage(viewport, batch);
        cam.position.set(0, 0, 0);
        cam.update();

        show();
    }

    public void show() {
        mainTable = new Table();
        mainTable.setSize(WIDTH, 80);
        mainTable.setPosition(-WIDTH/2, -80 + viewport.getWorldHeight()/2);
        mainTable.align(Align.center);

        TextButton saveButton = new TextButton("Save", skin);
        TextButton pauseButton = new TextButton("Pause", skin);
        TextButton noDrawButton = new TextButton("No Render", skin);
        TextButton limitSpeedButton = new TextButton("Unlock Speed", skin);

        saveButton.addListener(new ClickListener(){
           @Override
           public void clicked(InputEvent event, float x, float y) {
               NameDescriptionScreen.NameDescriptionCallback c = (name, description) -> {
                   sim.env.environmentName = name;
                   sim.env.EnvironmentDescription = description;
                   sim.env.userID = GameClient.getInstance().user.userID;
                   GameClient.getInstance().client.sendTCP(new SaveEnvironmentPacket(sim.env));
               };
               ScreenStack.push(new NameDescriptionScreen(sim.app, "Save Environment", "Save", c));
           }
        });
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.playing = !pauseButton.isChecked();
                if (pauseButton.isChecked()) {
                    pauseButton.setText("Play");
                } else {
                    pauseButton.setText("Pause");
                }
            }
        });

        noDrawButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.renderingEnabled = !noDrawButton.isChecked();
                if (noDrawButton.isChecked()) {
                    noDrawButton.setText("Render");
                } else {
                    noDrawButton.setText("No Render");
                }
            }
        });

        limitSpeedButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sim.limitSpeed = !limitSpeedButton.isChecked();
                if (limitSpeedButton.isChecked()) {
                    limitSpeedButton.setText("Lock Speed");
                } else {
                    limitSpeedButton.setText("Unlock Speed");
                }
            }
        });

        int w = 105;
        int pad = 4;
        mainTable.row().center();
        mainTable.add(saveButton).fill().width(w).pad(pad);
        mainTable.add(pauseButton).fill().width(w).pad(pad);
        mainTable.add(noDrawButton).fill().width(w).pad(pad);
        mainTable.add(limitSpeedButton).fill().width(w).pad(pad);

        stage.addActor(mainTable);
    }

    public void render() {
        viewport.apply();
        stage.getBatch().setProjectionMatrix(cam.combined);
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        mainTable.setPosition(-WIDTH/2, -80 + viewport.getWorldHeight()/2);
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
