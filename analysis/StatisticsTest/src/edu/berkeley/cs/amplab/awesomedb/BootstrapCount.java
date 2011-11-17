package edu.berkeley.cs.amplab.awesomedb;

import java.util.HashMap;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class BootstrapCount {
    double[] sampleCounts;
    long[] cummulativeTime;
    double meanCount;
    double stdev;
    double meanTime;
    public BootstrapCount(double[] sample, double samplingRate, double target, int bootstraps, int size) {
        double[] subsample;
        cummulativeTime = new long[bootstraps];
        sampleCounts = new double[bootstraps];
        Mean meanTimePerBootstrap = new Mean();
        for (int i = 0; i < bootstraps; i++) {
            long start = System.nanoTime();
            subsample = BootstrapSample.GenerateSampleWithReplacement(sample, size);
            sampleCounts[i] = StatisticalCount.Count(subsample, samplingRate, target);
            long time = Math.max(System.nanoTime() - start, 0);
            cummulativeTime[i] = time;
            meanTimePerBootstrap.increment(time);
        }
        meanCount = StatisticalMean.Mean(sampleCounts);
        meanTime = meanTimePerBootstrap.getResult();
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(sampleCounts);
    }
    public BootstrapCount(double[] sample, double samplingRate, double target, int bootstraps) {
        this(sample, samplingRate, target, bootstraps, sample.length);
    }
    
    public double getMeanTime() {
        return meanTime;
    }
    
    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[] getQuantiles() {
        return sampleCounts;
    }

    public double Count() {
        return meanCount;
    }

    public double StDev() {
        return stdev;
    }
}
