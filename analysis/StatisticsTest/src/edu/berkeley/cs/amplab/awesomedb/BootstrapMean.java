package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class BootstrapMean {
    //double[][] bootstrappedSamples;
    double[] sampleMeans;
    long[] cummulativeTime;
    double meanMean;
    double stdev;

    public BootstrapMean(double[] sample, int bootstraps) {
        double[] subsample;
        cummulativeTime = new long[bootstraps];
        sampleMeans = new double[bootstraps];
        long start = System.nanoTime();
        for (int i = 0; i < bootstraps; i++) {

            subsample = BootstrapSample.GenerateSampleWithReplacement(sample);
            sampleMeans[i] = StatisticalMean.Mean(subsample);
            cummulativeTime[i] = Math.max(System.nanoTime() - start, 0);
        }
        meanMean = StatisticalMean.Mean(sampleMeans);
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(sampleMeans);

    }

    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[] getMeans() {
        return sampleMeans;
    }

    public double Mean() {
        return meanMean;
    }

    public double StDev() {
        return stdev;
    }
}
