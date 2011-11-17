package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.util.FastMath;

public class SamplingUtilities {
    static MersenneTwister rng = new MersenneTwister();
    
    public static int[] ShuffledIndexes(int length) {
        int[] indices = new int[length];
        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }
        return indices;
    }
    
    public static void KnuthShuffle(int[] indices) {
        
        for (int i = indices.length - 1; i > 0; i--) {
            int j = rng.nextInt(i);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }
    }
    
    public static double[] VectorAvg(double[][] vectors) {
        double[] representative = new double[vectors[0].length];
        for (double[] vector : vectors) {
            for (int i = 0; i < vector.length; i++) {
                representative[i] += vector[i];
            }
        }
        for (int i = 0; i < representative.length; i++) {
            representative[i] /= vectors.length;
        }
        return representative;
    }
    
    public static double L2Norm2(double[] v1, double[] v2) 
    {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += FastMath.pow((v1[i] - v2[i]), 2.0);
        }
        return sum;
    }
    
    public static double VectorVariance(double[][] vectors) {
        double[] avg = SamplingUtilities.VectorAvg(vectors);
        double[] distances = new double[vectors.length];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = SamplingUtilities.L2Norm2(avg, vectors[i]);
        }
        double variance = StatisticalMean.Mean(distances);
        variance *= ((double)distances.length)/((double)(distances.length - 1));
        return variance;
    }
    
    public static double VectorVariance(double[][] vectors, double[] truth) {
        double[] distances = new double[vectors.length];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = SamplingUtilities.L2Norm2(truth, vectors[i]);
        }
        double variance = StatisticalMean.Mean(distances);
        variance *= ((double)distances.length)/((double)(distances.length - 1));
        return variance;
    }

}
