package com.csi4999.screens;

import com.badlogic.gdx.Game;
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
import com.csi4999.systems.networking.clientListeners.RankingResponseListener;
import com.csi4999.systems.networking.common.RankingInfo;
import com.csi4999.systems.networking.common.SavedCreatureDescription;

import java.util.ArrayList;

import static com.csi4999.singletons.CustomAssetManager.SKIN_MAIN;

public class LeaderboardScreen implements Screen {

    private Skin skin;
    private final OrthographicCamera leaderboardCam;
    private final FitViewport leaderboardViewport;
    private final ALifeApp app;
    private Stage stage;
    public ArrayList<RankingInfo> rankings;


    public LeaderboardScreen(ALifeApp app) {

        this.rankings = RankingResponseListener.getInstance().leaderboard;
        this.app = app;
        skin = CustomAssetManager.getInstance().manager.get(SKIN_MAIN);
        leaderboardCam = new OrthographicCamera();
        leaderboardViewport =  new FitViewport(400, 400, leaderboardCam);
        leaderboardCam.position.set(leaderboardCam.viewportWidth/2, leaderboardCam.viewportHeight/2, 0);
        leaderboardCam.update();

        stage = new Stage(leaderboardViewport, app.batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table mainTable = new Table();
        Table leaderboardTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        mainTable.align(Align.center);
        mainTable.align(Align.top);
        leaderboardTable.align(Align.center);

        Label title = new Label("Leaderboard", skin);
        mainTable.add(title).expandX().pad(0,0,0,0);
        mainTable.row();

        leaderboardTable.add(new Label("Rank:", skin)).width(40).pad(0,0,15, 50).uniform();
        leaderboardTable.add(new Label("User:", skin)).expandX().pad(0,0,15,0);
        leaderboardTable.add(new Label("Score:", skin)).width(100).pad(0,0,15,0);
        leaderboardTable.row();

        Integer rank = 1;

        for (RankingInfo r : rankings) {
            Label num = new Label(rank.toString() , skin);
            Label username = new Label(r.username, skin);
            Label score = new Label(Float.toString(r.ranking), skin );

            if (rank == 1) {
                num.setColor(0.854f,0.647f,0.125f, 1f);
                username.setColor(0.854f,0.647f,0.125f, 1f);
                score.setColor(0.854f,0.647f,0.125f, 1f);
            } else if (rank == 2) {
                num.setColor(0.501f,0.501f,0.501f, 1f);
                username.setColor(0.501f,0.501f,0.501f, 1f);
                score.setColor(0.501f,0.501f,0.501f, 1f);
            } else if (rank == 3) {
                num.setColor(0.545f,0.270f,0.074f, 1f);
                username.setColor(0.545f,0.270f,0.074f, 1f);
                score.setColor(0.545f,0.270f,0.074f, 1f);
            }
            leaderboardTable.add(num).width(40).pad(0,0,0, 50).uniform();
            leaderboardTable.add(username).expandX().pad(0,0,0,0);
            leaderboardTable.add(score).width(100);
            leaderboardTable.row();
            rank++;
        }

        mainTable.add(leaderboardTable).fillX();
        TextButton backButton = new TextButton("Go Back", skin);
        mainTable.row();
        mainTable.add(backButton).expandX();


        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(app));
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        leaderboardCam.update();
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
}
