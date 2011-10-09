package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.random.MersenneTwister;

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

}
