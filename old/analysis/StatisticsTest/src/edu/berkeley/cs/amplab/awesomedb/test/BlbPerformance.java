package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.BetaDistributionImpl;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.util.FastMath;

import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.SamplingUtilities;

public class BlbPerformance {
    private static double s_alpha;
    private static double s_beta;
    static double[] generateSample(int sampleSize) throws MathException {
        MersenneTwister rng = new MersenneTwister();
        s_alpha = rng.nextDouble() * 100;
        s_beta = rng.nextDouble() * 1000;
        BetaDistributionImpl normalDistribution = new BetaDistributionImpl(
                s_alpha, s_beta);
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
        final int NUMBER_OF_BOOTSTRAPS = 150; // Picked at random
        final int SMOOTHING = 100;
        final int POPULATION_SIZE = 100000;
        final int MEAN_ESTIMATES = 2000;
        final int START_SAMPLE_SIZE = 400;
        final int END_SAMPLE_SIZE = 10000;
        double[] baseSample = generateSample(POPULATION_SIZE);
        FileWriter file = new FileWriter(String.format("convergeblb.dat"));
        file.write("sample,rate,truth,truth_time,bootstrap,perBootstrapTime,perBagTime,bag_size,totalTime\n");
        for (int i = START_SAMPLE_SIZE; i < END_SAMPLE_SIZE; i+= 10) {
            Variance var = new Variance();
            double samplingRate = ((double) i) / ((double)POPULATION_SIZE);
            System.out.println(String.format("Starting for probability %1$s", samplingRate));
            long startAccurate = System.nanoTime();
            for (int estimate = 0; estimate < MEAN_ESTIMATES; estimate++) {
                double[] sample = BinomialSample(samplingRate, baseSample);
                Mean mean = new Mean();
                var.increment(mean.evaluate(sample));
            }
            
            double trueVariance = var.getResult();
            long endAccurate = System.nanoTime();
            long accurateTime = endAccurate - startAccurate;
            System.out.println(String.format("Finished exact calculation rate=%1$s", 
                    samplingRate));
            
            Mean bootstrapDistanceMean = new Mean();
            Mean perbootstrapTime = new Mean();
            Mean perbagTime = new Mean();
            Mean bootstrapTotalTime = new Mean();
            Mean avgBagSize = new Mean();
            for (int exp = 0; exp < SMOOTHING; exp++) {
                double[] sample = BinomialSample(samplingRate, baseSample);
                double bagExp = 0.5;
                blbExperiment(sample, bagExp, NUMBER_OF_BOOTSTRAPS,
                        trueVariance, bootstrapDistanceMean, perbootstrapTime,
                        perbagTime, bootstrapTotalTime, avgBagSize);
            }
            file.write(String.format(
                    "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s\n", 
                    i,
                    samplingRate,
                    trueVariance,
                    accurateTime,
                    bootstrapDistanceMean.getResult(),
                    perbootstrapTime.getResult(),
                    perbagTime.getResult(),
                    avgBagSize.getResult(),
                    bootstrapTotalTime.getResult()));
        }
    }

    private static void blbExperiment(double[] sample, double bagExp,
            final int numberOfBootstraps, double trueVariance,
            Mean bootstrapDistanceMean, Mean perbootstrapTime, Mean perbagTime,
            Mean bootstrapTotalTime, Mean avgBagSize) {
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        SamplingUtilities.KnuthShuffle(index);
        Mean varianceBagMean = new Mean();
        long bootstrapTime = 0;
        avgBagSize.increment(bag_size);
        int bagNumber = 0;
        for (int ii = 0; ii < index.length; ii += bag_size) {
            System.out.println(String.format("Starting bag %1$s",bagNumber++));
            double[] sampleBag = new double[FastMath.min(ii + bag_size, index.length) - ii];
            for (int jj = ii; jj < FastMath.min(ii + bag_size, index.length); jj++) {
                sampleBag[jj - ii] = sample[index[jj]];
            }
            BootstrapMean mean = new BootstrapMean(sampleBag, numberOfBootstraps);
            Variance bootstrapVarianceObj = new Variance();
            double bootstrapVariance = bootstrapVarianceObj.evaluate(mean.getMeans());
            varianceBagMean.increment(bootstrapVariance);
            perbootstrapTime.increment(mean.getMeanTime());
            bootstrapTime += mean.getTimes()[mean.getTimes().length - 1];
            perbagTime.increment(mean.getTimes()[mean.getTimes().length - 1]);
        }
        bootstrapDistanceMean.increment(FastMath.abs(varianceBagMean.getResult() - 
                trueVariance));
        bootstrapTotalTime.increment(bootstrapTime);
    }
}
