package edu.berkeley.cs.amplab.awesomedb;
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
}
