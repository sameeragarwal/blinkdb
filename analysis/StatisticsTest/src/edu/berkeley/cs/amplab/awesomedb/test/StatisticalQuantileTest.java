package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.util.FastMath;

import edu.berkeley.cs.amplab.awesomedb.BootstrapSample;
import edu.berkeley.cs.amplab.awesomedb.StatisticalQuantile;

public class StatisticalQuantileTest {
    private static double s_alpha;
    private static double s_beta;
    public static double[] generateSample(int sampleSize) throws MathException {
        s_alpha = 9; // shape
        s_beta = 0.8; //scale;
        GammaDistributionImpl normalDistribution = new GammaDistributionImpl(s_alpha, s_beta);
        AbstractContinuousDistribution distribution = normalDistribution;
        return distribution.sample(sampleSize);
    }

    public static double[] BinomialSample(double p, double[] population) throws MathException {
        ArrayList<Double> sampleObjects = new ArrayList<Double>();
        BinomialDistributionImpl binomial = new BinomialDistributionImpl(1, p);
        
        for (double value : population) {
            int c = binomial.sample();
            assert(c == 0 || c == 1);
            if (c == 1) {
                sampleObjects.add(new Double(value));
            }
            
        }
        double[] sample = new double[sampleObjects.size()];
        for (int ii = 0; ii < sample.length; ii++) {
            sample[ii] = ((Double)sampleObjects.get(ii)).doubleValue();
        }
        return sample;
    }
    
    public static void main(String[] args) throws MathException, IOException {
        final int SMOOTHING = 100;
        final int POPULATION_SIZE = 100000;
        final int MEAN_ESTIMATES = 2000;
        final int START_SAMPLE_SIZE = 200;
        final int END_SAMPLE_SIZE = 10000;
        double[] baseSample = generateSample(POPULATION_SIZE);
        FileWriter file = new FileWriter(String.format("quantiles.dat"));
        file.write("size,truth,error_stat\n");
        for (int i = START_SAMPLE_SIZE; i < END_SAMPLE_SIZE; i+= 10) {
            Variance var = new Variance();
            for (int estimate = 0; estimate < MEAN_ESTIMATES; estimate++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                var.increment(StatisticalQuantile.Quantile(sample, 0.95));
            }
            System.out.println("Finished estimating true variance");
            double trueVariance = var.getResult();
            Mean meanDistanceFromVar = new Mean();
            for (int ii = 0; ii < SMOOTHING; ii++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                double quantile = StatisticalQuantile.Quantile(sample, 0.95);
                double sampleVariance = StatisticalQuantile.QuantileVar(sample, 0.95, quantile);
                //System.out.println(String.format("Sample variance is %1$s", sampleVariance));
                //System.out.println(String.format("Found error %1$s for size %2$s", FastMath.abs(sampleVariance - trueVariance), i));
                meanDistanceFromVar.increment(FastMath.abs(sampleVariance - trueVariance));
            }
            System.out.println("Writing to file");
            file.write(String.format("%1$s,%2$s,%3$s\n", i, trueVariance, meanDistanceFromVar.getResult()));
            file.flush();
            System.out.println("Done with computation");
        }
        
    }

}
