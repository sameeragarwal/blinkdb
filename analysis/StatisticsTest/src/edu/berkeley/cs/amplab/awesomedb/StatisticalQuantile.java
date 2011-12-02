package edu.berkeley.cs.amplab.awesomedb;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.util.FastMath;

public class StatisticalQuantile {
    public static double Quantile(double[] samples, double quantile) {
        double[] newSample = samples.clone();
        Arrays.sort(newSample);
        double position = quantile * ((double)samples.length);
        double result = samples[(int)FastMath.ceil(position - 0.5)];//+ 
//                (position - FastMath.floor(position)) * (samples[(int)FastMath.floor(position) + 1] - 
//                        samples[(int)FastMath.floor(position)]);
        return result;
    }
    public static double QuantileVar(double[] samples, double quantile, double quantileValue) {
        int count = 0;
        for (double sample : samples) {
            if (sample == quantileValue) {
                count++;
            }
        }
        double p = ((double)count) / ((double)samples.length);
        double inv_p2 = 1.0 / (p * p);
        double term = (quantile * (1 - quantile)) / ((double)samples.length);
        return inv_p2 * term;
    }
    public static double Quantile(HashMap<Double, Integer> frequencySamples, double quantile) {
        Object[] keysObjects;
        keysObjects = frequencySamples.keySet().toArray();
        Double[] keys;
        keys = new Double[keysObjects.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = (Double) keysObjects[i];
        }
        Arrays.sort(keys, null);
        Set<Map.Entry<Double,Integer>> entrySet = frequencySamples.entrySet();
        long size = 0;
        for (Map.Entry<Double,Integer> entry : entrySet) {
            size += entry.getValue();
        }
        double position = ((double)size) * quantile;
        long position0 = (long)FastMath.floor(position);
        long position1 = position0 + 1;
        double val0 =0.0
        , val1 = 0.0;
        int current = 0, prev = 0;
        for (Double key : keys) {
            Integer value = frequencySamples.get(key);
            current += value;
            if (prev < position0 && position0 <= current) {
                val0 = key;
            }
            if (prev < position1 && position1 <=current) {
                val1 = key;
                break;
            }
            prev = current;
        }
        double result = val0 + 
                (position - FastMath.floor(position)) * (val1 - val0);
        return result;
        
    }
}
