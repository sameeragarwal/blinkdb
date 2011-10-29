package edu.berkeley.cs.amplab.awesomedb;
import java.util.HashMap;

import org.apache.commons.math.random.MersenneTwister;

public class BootstrapSample {
    static MersenneTwister rng = new MersenneTwister();
    public static double[] GenerateSampleWithReplacement(double[] population) {
        /*double[] sample = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            sample[i] = population[rng.nextInt(population.length)];
        }
        return sample;*/
        return GenerateSampleWithReplacement(population, population.length);
    }
    
    public static double[] GenerateSampleWithReplacement(double[] population, int size) {
        double[] sample = new double[size];
        for (int i = 0; i < size; i++) {
            sample[i] = population[rng.nextInt(population.length)];
        }
        return sample;
    }
    
    public static HashMap<Double, Integer> GenerateSampleWithReplacementInFrequency
                    (double[] population, 
                            int size) {
        HashMap<Double, Integer> toReturn = new HashMap<Double, Integer>();
        for (int i = 0; i < size; i++) {
            double sample = population[rng.nextInt(population.length)];
            
            if (toReturn.containsKey(sample)) {
                toReturn.put(sample, new Integer(toReturn.get(sample).intValue() + 1));
            }
            else {
                toReturn.put(sample, new Integer(1));
            }
        }
        return toReturn;
        
    }
    
    public static HashMap<Double, Integer> GenerateSampleWithReplacementInFrequency(
                               double[] population) {
        return GenerateSampleWithReplacementInFrequency(population, population.length);
    }
    
    public static <T> T[] GenerateSampleWithReplacement(T[] population) {
        @SuppressWarnings({"unchecked"})
        T[] sample = (T[])new Object[population.length];
        for (int i = 0; i < population.length; i++) {
            sample[i] = population[rng.nextInt(population.length)];
        }
        return sample;
    }
}
