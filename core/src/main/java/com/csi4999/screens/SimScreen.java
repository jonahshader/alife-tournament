package com.csi4999.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.csi4999.ALifeApp;
import com.csi4999.systems.TestBall;
import com.csi4999.systems.TestLineSegment;
import com.csi4999.systems.creature.Creature;
import com.csi4999.systems.creature.SensorBuilder;
import com.csi4999.systems.creature.ToolBuilder;
import com.csi4999.systems.creature.sensors.Eye;
import com.csi4999.systems.physics.PhysicsEngine;
import com.csi4999.systems.ui.PanCam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimScreen implements Screen {
    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 360;
    private final OrthographicCamera worldCam;
    private final ExtendViewport worldViewport;
    private final ALifeApp app;

    private TestBall ball1, ball2;
    //private TestLineSegment line1;
    private Eye eye1;

    private List<TestBall> balls = new ArrayList<>();
    private List<Creature> creatures = new ArrayList<>();
    private PhysicsEngine physics = new PhysicsEngine();

    private float time = 0;

    public SimScreen(ALifeApp app) {
        this.app = app;

        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(GAME_WIDTH, GAME_HEIGHT, worldCam);
        Gdx.input.setInputProcessor(new PanCam(worldViewport, worldCam)); // TODO: use multiplexer

        ball1 = new TestBall(new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), 16f);
        ball1.color.b = 0;
        ball1.rotationDegrees = 0;
        ball1.velocity.x = 6;
        ball2 = new TestBall(new Vector2(32, 0), new Vector2(0, 0), new Vector2(0, 0), 12f);
        ball2.color.g = 0;
        ball1.getChildren().add(ball2);



        //line1 = new TestLineSegment(new Vector2(64f, 0f));
        //line1.color.r = 0;
        //ball2.getChildren().add(line1);

        eye1 = new Eye(new Vector2(64f, 0f));
        ball2.getChildren().add(eye1);
        Random r = new RandomXS128();
        for (int i = 0; i < 100; i++) {
            TestBall newBall = new TestBall(new Vector2((float) r.nextGaussian(0f, 128f), (float) r.nextGaussian(0f, 64f)), new Vector2((float) r.nextGaussian(0f, 4f), (float) r.nextGaussian(0f, 4f)), new Vector2(0f, 0f), 8f);

            // Sets random similarity vector for each ball for testing purposes
            //newBall.setSimilarity(new float[] {r.nextFloat(), r.nextFloat(), r.nextFloat(), r.nextFloat()});
            physics.addCollider(newBall);
            balls.add(newBall);
        }

        for (int i = 0; i < 25; i++) {
            Creature c = new Creature(new Vector2((float) r.nextGaussian(0f, 128f), (float) r.nextGaussian(0f, 64f)), new ArrayList<>(), new ArrayList<>(), 0, 0, physics, r);
            physics.addCollider(c);
            creatures.add(c);
        }

        //physics.addCollider(line1);
        physics.addCollider(eye1);
    }

    @Override
    public void render(float delta) {
        ball1.rotationDegrees = time * 80;
        ball1.move(delta);
        balls.forEach(b -> b.move(delta));
        physics.run();
        worldViewport.apply();
        app.batch.setProjectionMatrix(worldCam.combined);

        // set clear color
        Gdx.gl.glClearColor(.5f, .5f, .5f, 1f);
        // apply clear color to screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();
        app.shapeDrawer.setColor(1f, 1f, 1f, 1f);
        app.shapeDrawer.filledCircle(0f, 0f, 32f);

        balls.forEach(b -> b.draw(app.batch, app.shapeDrawer, null, 1f));
        ball1.draw(app.batch, app.shapeDrawer, null, 1f);
        ball1.renderBounds(app.shapeDrawer);
        ball2.renderBounds(app.shapeDrawer);
        //line1.renderBounds(app.shapeDrawer);
        eye1.renderBounds(app.shapeDrawer);
        creatures.forEach(c -> c.move(delta));
        creatures.forEach(c -> c.draw(app.batch, app.shapeDrawer, null, 1f));


        app.batch.end();
        time += delta;
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
    }

    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
