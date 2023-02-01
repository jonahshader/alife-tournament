package com.csi4999.systems.creature;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.csi4999.systems.Mutable;

public abstract class Sensor extends Group implements Mutable {
    public abstract float[] read();
}
