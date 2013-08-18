package edu.berkeley.cs.amplab.awesomedb;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.util.FastMath;
public class StatisticalMean {
    
    public static double Mean(double[] samples) {
        Mean mean = new Mean();
        return mean.evaluate(samples);
    }
    
    // http://en.wikipedia.org/wiki/Standard_error_(statistics)#Standard_error_of_the_mean
    public static double StandardError(double[] samples) {
        StandardDeviation dev = new StandardDeviation(false);
        double s = dev.evaluate(samples);
        double sqrt_n = FastMath.sqrt((double)samples.length);
        return s/sqrt_n;
    }
    
    public static double Mean(HashMap<Double, Integer> frequencySamples) {
        Set<Map.Entry<Double,Integer>> entrySet = frequencySamples.entrySet(); 
        double sum = 0.0;
        long count = 0;
        for (Map.Entry<Double,Integer> entry : entrySet) {
            sum += (entry.getKey().doubleValue() * ((double) entry.getValue().intValue()));
            count += entry.getValue().intValue();
        }
        return (sum / (double) count);
    }
}
