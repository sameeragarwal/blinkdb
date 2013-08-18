/**
 * 
 */
package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.util.FastMath;

import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.StatisticalMean;

public class BootstrapPerformanceTest {
    
    static double[] GenerateNormalSample(int sampleSize) throws MathException {
        MersenneTwister rng = new MersenneTwister();
        double mean = rng.nextDouble() * 100;
        double stdev = rng.nextDouble() * 1000;
        NormalDistributionImpl normalDistribution = new NormalDistributionImpl(
                mean, stdev);
        AbstractContinuousDistribution distribution = normalDistribution;
        return distribution.sample(sampleSize);
    }
    public static void main(String[] args) throws MathException, IOException {
        final long TESTS = 500;
        final int SMOOTHING = 1000;
        final int SampleSize = 10000000;
        double[] samples = GenerateNormalSample(SampleSize);
        double closedFormError = StatisticalMean.StandardError(samples);
        System.out.println("Start computing bootstraps");
        BootstrapMean bootstraps[] = new BootstrapMean[SMOOTHING];
        for(int i = 0; i < bootstraps.length; i++) {
            bootstraps[i] = new BootstrapMean(samples, (int)(TESTS*100));
        }
        System.out.println("Done computing bootstraps");
        FileWriter file = new FileWriter(String.format("bootstrap.dat"));
        file.write("bootstraps,time,original,computed,difference\n");
        for(int i = 2; i < TESTS * 100; i++) {
            Mean meanDeviation = new Mean();
            Mean meanTime = new Mean();
            for (int j = 0; j < bootstraps.length; j++) {
                StandardDeviation stdev = new StandardDeviation();
                double deviation = stdev.evaluate(bootstraps[j].getMeans(), 0, i);
                meanDeviation.increment(deviation);
                meanTime.increment(bootstraps[j].getTimes()[i]);
            }
            double normalizedDifference = FastMath.abs(meanDeviation.getResult() - closedFormError) 
                    / closedFormError;
            file.write(String.format("%1$s,%2$s,%3$s,%4$s,%5$s\n",
                    i,
                    meanTime.getResult(),
                    closedFormError,
                    meanDeviation.getResult(),
                    normalizedDifference));
            file.flush();
            System.out.println(String.format("Done with %1$s", i));
        }
    }

}
