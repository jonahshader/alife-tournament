package com.csi4999;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ALifeApp extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}