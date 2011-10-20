package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.moment.Mean;


public class BootstrapMean {
    //double[][] bootstrappedSamples;
    double[] sampleMeans;
    long[] cummulativeTime;
    double meanMean;
    double stdev;
    double meanTime;
    public BootstrapMean(double[] sample, int bootstraps) {
        double[] subsample;
        cummulativeTime = new long[bootstraps];
        sampleMeans = new double[bootstraps];
        Mean meanTimePerBootstrap = new Mean();
        
        for (int i = 0; i < bootstraps; i++) {
            long start = System.nanoTime();
            subsample = BootstrapSample.GenerateSampleWithReplacement(sample);
            sampleMeans[i] = StatisticalMean.Mean(subsample);
            long time = Math.max(System.nanoTime() - start, 0);
            cummulativeTime[i] = time;
            meanTimePerBootstrap.increment(time);
        }
        meanMean = StatisticalMean.Mean(sampleMeans);
        meanTime = meanTimePerBootstrap.getResult();
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(sampleMeans);

    }
    
    public double getMeanTime() {
        return meanTime;
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
