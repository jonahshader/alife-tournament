package com.csi4999.systems.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.csi4999.screens.SimScreen;
import com.csi4999.screens.TournamentWaitScreen;
import com.csi4999.singletons.CustomAssetManager;
import com.csi4999.singletons.ScreenStack;
import com.csi4999.systems.environment.Environment;
import com.csi4999.systems.networking.GameClient;
import com.csi4999.systems.networking.packets.RequestTournamentPacket;
import com.csi4999.systems.networking.wrappers.Chunk;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Output;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.csi4999.singletons.CustomAssetManager.UI_FONT;
import static com.csi4999.systems.networking.GameClient.BUFFER_SIZE;

public class ChunkSelector implements InputProcessor {
    // selects a chunk from an environment, so use the world viewport/cam
    public static final int CHUNK_SIZE = 1024;

    private Viewport viewport;
    private Camera cam;
    private SimScreen sim;
    private BitmapFont font;

    private boolean active = false;

    private float animPhase = 0f;

    public ChunkSelector(Viewport viewport, Camera cam, SimScreen sim) {
        this.viewport = viewport;
        this.cam = cam;
        this.sim = sim;
        font = CustomAssetManager.getInstance().manager.get(UI_FONT);
    }

    public void activate() {
        active = true;
    }

    public void render(ShapeDrawer drawer, float dt) {
        if (active) {
            drawer.getBatch().setProjectionMatrix(cam.combined);
            drawer.getBatch().begin();
            Vector2 worldPos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            float opacity = (float) Math.cos(animPhase) * .5f + .5f;
            opacity = opacity * .25f + .5f;
            drawer.rectangle(worldPos.x - CHUNK_SIZE/2, worldPos.y - CHUNK_SIZE/2, CHUNK_SIZE, CHUNK_SIZE,
                new Color(.5f, 1f, .5f, opacity), 3f);
            drawer.filledRectangle(worldPos.x - CHUNK_SIZE/2, worldPos.y - CHUNK_SIZE/2, CHUNK_SIZE, CHUNK_SIZE,
                new Color(.5f, 1f, .5f, opacity * .5f));
            drawer.getBatch().end();

            animPhase += dt * 15;
            animPhase %= Math.PI*2;
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        // cancel on escape
        if (active && keycode == Input.Keys.ESCAPE) {
            active = false;
            return true;
        }
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
        if (active && button == Input.Buttons.LEFT) {
            Output o = new Output(BUFFER_SIZE);
            GameClient.getInstance().client.getKryo().writeClassAndObject(o, sim.env);

            // dumb logic to clone environment. can't use the copy function because the serializers behave differently
            ByteBufferInput i = new ByteBufferInput(o.getBuffer());
            Environment newEnv = (Environment) GameClient.getInstance().client.getKryo().readClassAndObject(i);
            Vector2 worldPos = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            newEnv.removeOutsideOfRectangle(new Rectangle(worldPos.x - CHUNK_SIZE/2, worldPos.y - CHUNK_SIZE/2, CHUNK_SIZE, CHUNK_SIZE));
            newEnv.physics.shiftObjects(worldPos.scl(-1f));
//            newEnv.update();

            newEnv.userID = GameClient.getInstance().user.userID;

            RequestTournamentPacket p = new RequestTournamentPacket(new Chunk(newEnv), GameClient.getInstance().user.rank);
            ScreenStack.switchTo(new TournamentWaitScreen(sim.app));
            System.out.println("Sending RequestTournamentPacket to server.");
            GameClient.getInstance().client.sendTCP(p);
            i.close();
            o.close();
            active = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return active;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
