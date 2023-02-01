package com.csi4999.systems.ai;

import com.csi4999.systems.Mutable;

public interface Brain extends Mutable {
    float[] run(float[] input);

    void resizeInput(int newInputSize);
    void resizeOutput(int newOutputSize);
}
