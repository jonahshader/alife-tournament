package com.csi4999.lwjgl3.systems.ai;

import com.csi4999.lwjgl3.systems.Mutable;

public interface Brain extends Mutable {
    float[] run(float[] input);
}
