package com.csi4999.systems.creature;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.csi4999.systems.Mutable;

public abstract class Tool extends Group implements Mutable {
    public abstract void use(float strength);
}
