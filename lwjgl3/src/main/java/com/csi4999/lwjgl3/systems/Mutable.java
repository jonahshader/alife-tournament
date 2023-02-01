package com.csi4999.lwjgl3.systems;

import java.util.Random;

public interface Mutable {
    void mutate(float amount, Random rand);
}
