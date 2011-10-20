package edu.berkeley.cs.amplab.awesomedb;
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
    
    public static <T> T[] GenerateSampleWithReplacement(T[] population) {
        @SuppressWarnings({"unchecked"})
        T[] sample = (T[])new Object[population.length];
        for (int i = 0; i < population.length; i++) {
            sample[i] = population[rng.nextInt(population.length)];
        }
        return sample;
    }
}
