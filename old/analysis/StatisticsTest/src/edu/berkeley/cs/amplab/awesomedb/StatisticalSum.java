package edu.berkeley.cs.amplab.awesomedb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatisticalSum {
    public static double Sum(double[] samples, double samplingRate) {
        double sum = 0;
        for (double elt : samples) {
            sum += elt;
        }
        return sum * (1.0d / samplingRate);
    }
    
    public static double Sum(HashMap<Double, Integer> frequencySamples, double samplingRate) {
        Set<Map.Entry<Double,Integer>> entrySet = frequencySamples.entrySet(); 
        double sum = 0.0;
        for (Map.Entry<Double,Integer> entry : entrySet) {
            sum += (entry.getKey().doubleValue() * ((double) entry.getValue().intValue()));
        }
        return (sum * (1.0d / samplingRate));
    }
}
