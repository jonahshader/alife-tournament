package com.csi4999.systems.ai;

import com.csi4999.systems.Mutable;
import com.csi4999.systems.creature.CreatureComponent;

import java.util.Random;

public interface Brain extends Mutable, CreatureComponent {
    float[] run(float[] input);

    void insertInput(int inputIndex, Random rand);
    void insertOutput(int outputIndex, Random rand);
    void removeInput(int inputIndex);
    void removeOutput(int outputIndex);

    Brain copy();
}
