package com.csi4999.systems.creature;

import com.csi4999.systems.Mutable;

public interface Sensor extends Mutable {
    float[] read();
}
