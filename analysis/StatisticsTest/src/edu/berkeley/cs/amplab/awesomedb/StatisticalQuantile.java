package edu.berkeley.cs.amplab.awesomedb;

import java.util.Arrays;
import org.apache.commons.math.util.FastMath;

public class StatisticalQuantile {
    public static double Quantile(double[] samples, double quantile) {
        double[] newSample = samples.clone();
        Arrays.sort(newSample);
        double position = quantile * ((double)samples.length);
        double result = samples[(int)FastMath.floor(position)] + 
                (position - FastMath.floor(position)) * (samples[(int)FastMath.floor(position) + 1] - 
                        samples[(int)FastMath.floor(position)]);
        return result;
    }
}
