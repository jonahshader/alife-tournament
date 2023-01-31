package com.csi4999.lwjgl3.systems.creature;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.csi4999.lwjgl3.systems.Mutable;

public abstract class Sensor extends Group implements Mutable {
    public abstract float[] read();
}
