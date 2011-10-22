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

public class SampleSizeTest {

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
        final int NUMBER_OF_BOOTSTRAPS = 150; // Picked at random
        final int SMOOTHING = 1000;
        final int SampleSizeBegin = 8;
        final int SampleSizeEnd = Integer.MAX_VALUE / 256;
        System.out.println("Done computing bootstraps");
        FileWriter file = new FileWriter(String.format("sample.dat"));
        file.write("samples,time,deviation\n");
        for (int sampleSize = SampleSizeBegin; sampleSize < SampleSizeEnd; sampleSize *= 2) {
            Mean meanDeviation = new Mean();
            Mean meanTime = new Mean();
            for (int i = 0; i < SMOOTHING; i++) {
                double[] samples = GenerateNormalSample(sampleSize);
                double closedFormError = StatisticalMean.StandardError(samples);
                BootstrapMean bootstrap = new BootstrapMean(samples, NUMBER_OF_BOOTSTRAPS);
                StandardDeviation stdev = new StandardDeviation();
                double deviation = stdev.evaluate(bootstrap.getMeans());
                meanTime.increment(bootstrap.getTimes()[NUMBER_OF_BOOTSTRAPS - 1]);
                meanDeviation.increment(FastMath.abs(deviation - closedFormError) / 
                        closedFormError);
            }
            file.write(String.format("%1$s,%2$s,%3$s\n", 
                    sampleSize, 
                    meanTime.getResult(),
                    meanDeviation.getResult()));
            file.flush();
            System.out.println(String.format("Done computing for %1$s", sampleSize));
        }
    }

}
