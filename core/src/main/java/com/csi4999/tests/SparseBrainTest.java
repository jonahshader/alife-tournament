package com.csi4999.tests;

import com.badlogic.gdx.math.RandomXS128;
import com.csi4999.systems.ai.SparseBrain;

import java.util.Arrays;
import java.util.Random;

public class SparseBrainTest {
    public static void main(String[] args) {
        Random r = new RandomXS128(0);
        SparseBrain b = new SparseBrain(10, 5, 7, 0.5f, 0.1f, 0.25f, 0.7f, r);
        float[] input = new float[10];
        Arrays.fill(input, 0f);
        float[] output = b.run(input);
        System.out.println(Arrays.toString(output));
        Arrays.fill(input, 1f);

        for (int i = 0; i < 10; i++) {
            output = b.run(input);
            System.out.println(Arrays.toString(output));
        }
    }
}
