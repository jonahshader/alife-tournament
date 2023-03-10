package com.csi4999.systems;

import java.util.List;

public class CustomMath {
    // TODO: unit test
    /**
     * takes a step towards desired from current, changing at most 'rate'
     * @param current - the current value we want to move
     * @param desired - the value we want to move towards
     * @param rate - the amount to change desired by to move towards current
     * @return - the moved value
     */
    public static float towardsValue(float current, float desired, float rate) {
        if (desired > current + rate) {
            current += rate;
        } else if (desired > current) {
            current = desired;
        } else if (desired < current - rate) {
            current -= rate;
        } else if (desired < current) {
            current = desired;
        }

        return current;
    }

    // from https://cdn.arduino.cc/reference/en/language/functions/math/map/
    public static float map(float x, float inMin, float inMax, float outMin, float outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public static float findMin(List<Float> values, float emptyVal) {
        if (values.size() == 0) return emptyVal;
        float min = values.get(0);
        for (int i = 1; i < values.size(); i++)
            min = Math.min(values.get(i), min);
        return min;
    }

    public static float findMax(List<Float> values, float emptyVal) {
        if (values.size() == 0) return emptyVal;
        float max = values.get(0);
        for (int i = 1; i < values.size(); i++)
            max = Math.max(values.get(i), max);
        return max;
    }
}
