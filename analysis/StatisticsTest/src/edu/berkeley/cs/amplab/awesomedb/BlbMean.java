package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class BlbMean {
    //double[][] bootstrappedSamples;
    double[] bagMeans;
    long[] cummulativeTime;
    double meanMean;
    double stdev;

    public BlbMean(double[] sample, int nbags, int bootstraps) {
        Mean mean = new Mean();
        int[] division = SamplingUtilities.ShuffledIndexes(sample.length);
        int bagsize = sample.length / nbags;
        Mean[] bagMeanGenerator = new Mean[bootstraps];
        cummulativeTime = new long[bootstraps];
        for (int i = 0; i < bagMeanGenerator.length; i++) {
            bagMeanGenerator[i] = new Mean();
        }
        for (int i = 0; i < nbags; i++) {
            double[] bag = new double[(i < nbags - 1 ? bagsize : (sample.length - (i * bagsize)))];
            for (int j = 0; j < bag.length; j++) {
                bag[j] = sample[division[(i * bagsize) + j]];
            }
            BootstrapMean bagmean = new BootstrapMean(bag, bootstraps);
            mean.increment(bagmean.Mean());
            for (int j = 0; j < bagmean.getMeans().length; j++) {
                bagMeanGenerator[j].increment(bagmean.getMeans()[j]);
                cummulativeTime[j] += bagmean.getTimes()[j];
            }
        }
        bagMeans = new double[bagMeanGenerator.length];
        for (int i = 0; i < bagMeans.length; i++) {
            bagMeans[i] = bagMeanGenerator[i].getResult();
        }
        meanMean = mean.getResult();
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(bagMeans);
    }

    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[] getMeans() {
        return bagMeans;
    }

    public double Mean() {
        return meanMean;
    }

    public double StDev() {
        return stdev;
    }
}
