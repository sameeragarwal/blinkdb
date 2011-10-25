package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class BootstrapQuantile {
    double[] sampleQuantiles;
    long[] cummulativeTime;
    double meanQuantile;
    double stdev;
    double meanTime;
    public BootstrapQuantile(double[] sample, double quantile, int bootstraps, int size) {
        double[] subsample;
        cummulativeTime = new long[bootstraps];
        sampleQuantiles = new double[bootstraps];
        Mean meanTimePerBootstrap = new Mean();
        for (int i = 0; i < bootstraps; i++) {
            long start = System.nanoTime();
            subsample = BootstrapSample.GenerateSampleWithReplacement(sample, size);
            sampleQuantiles[i] = StatisticalQuantile.Quantile(subsample, quantile);
            long time = Math.max(System.nanoTime() - start, 0);
            cummulativeTime[i] = time;
            meanTimePerBootstrap.increment(time);
        }
        meanQuantile = StatisticalMean.Mean(sampleQuantiles);
        meanTime = meanTimePerBootstrap.getResult();
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(sampleQuantiles);
    }
    public BootstrapQuantile(double[] sample, double quantile, int bootstraps) {
        this(sample, quantile, bootstraps, sample.length);
    }
    
    public double getMeanTime() {
        return meanTime;
    }
    
    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[] getQuantiles() {
        return sampleQuantiles;
    }

    public double Quantile() {
        return meanQuantile;
    }

    public double StDev() {
        return stdev;
    }
}