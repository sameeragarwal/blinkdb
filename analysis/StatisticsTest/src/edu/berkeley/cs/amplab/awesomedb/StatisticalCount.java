package edu.berkeley.cs.amplab.awesomedb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public static double Count(
                                HashMap<Double, Integer> frequencySamples, 
                                double samplingRate, 
                                double target) {
        Double[] keys;
        keys = (Double[]) frequencySamples.keySet().toArray();
        Arrays.sort(keys, null);
        long count = 0;
        for (Double key : keys) {
            if (key >= target) {
                break;
            }
            count += frequencySamples.get(key);
        }
        
        return ((double)count) * (1 / samplingRate);
    }

}
