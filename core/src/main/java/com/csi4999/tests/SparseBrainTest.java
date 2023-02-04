package com.csi4999.tests;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.csi4999.systems.ai.SparseBrain;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class SparseBrainTest {
    public static void main(String[] args) {
        SparseBrain b1 = new SparseBrain(10, 5, 7, 0.5f, 0.1f, 0.25f, 0.7f, new RandomXS128(0));
        SparseBrain b2 = new SparseBrain(10, 5, 7, 0.5f, 0.1f, 0.25f, 0.7f, new RandomXS128(0));
        float[] input = new float[10];
        Arrays.fill(input, 0f);
//        float[] input2 = new float[110];
//        Arrays.fill(input2, 0f);
//        b2.resizeInput(110);
//
//        System.out.println(Arrays.toString(b1.run(input)));
//        System.out.println(Arrays.toString(b2.run(input2)));
//
//        b2.resizeInput(1);
//        System.out.println(Arrays.toString(b1.run(input)));
//        System.out.println(Arrays.toString(b2.run(new float[]{0f})));

//        b1.insertNeuron(10, 0f);
        System.out.println(Arrays.toString(b1.run(input)));
        System.out.println(Arrays.toString(b2.run(input)));

        Random r = new RandomXS128(1);
        for (int i = 0; i < 1000; i++) {
            b1.mutate(1f, r);
            System.out.println(Arrays.toString(b1.run(input)));
        }
    }
}
