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

    // from https://stackoverflow.com/questions/442758/which-java-library-computes-the-cumulative-standard-normal-distribution-function
    // returns the cumulative normal distribution function (CNDF)
    // for a standard normal: N(0,1)
    public static double CNDF(double x)
    {
        int neg = (x < 0d) ? 1 : 0;
        if ( neg == 1)
            x *= -1d;

        double k = (1d / ( 1d + 0.2316419 * x));
        double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
            k - 0.356563782) * k + 0.319381530) * k;
        y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

        return (1d - neg) * y + neg * (1d - y);
    }

    // adapted from https://www.johndcook.com/blog/cpp_phi_inverse/
    private static double rationalApproximation(double t) {
        double c[] = {2.515517, 0.802853, 0.010328};
        double d[] = {1.432788, 0.189269, 0.001308};
        return t - ((c[2]*t + c[1])*t + c[0]) /
            (((d[2]*t + d[1])*t + d[0])*t + 1.0);
    }

    public static double normalCDFInverse(double p) {

        if (p < 0.5)
        {
            // F^-1(p) = - G^-1(p)
            return -rationalApproximation( Math.sqrt(-2.0*Math.log(p)) );
        }
        else
        {
            // F^-1(p) = G^-1(1-p)
            return rationalApproximation( Math.sqrt(-2.0*Math.log(1-p)) );
        }
    }
}
