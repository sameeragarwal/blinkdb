package edu.berkeley.cs.amplab.awesomedb;

import java.util.Arrays;

public class StatisticalCount {
    public static double Count(double[] samples, double samplingRate, double target) {
        double[] newSample = samples.clone();
        Arrays.sort(newSample);
        long count = 0;
        for (double elt : newSample) {
            if (elt >= target) {
                break;
            }
            count++;
        }
        return ((double)count) * (1 / samplingRate);
    }
}
