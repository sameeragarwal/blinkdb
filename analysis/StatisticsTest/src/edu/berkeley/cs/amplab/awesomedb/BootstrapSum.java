package edu.berkeley.cs.amplab.awesomedb;

import java.util.HashMap;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class BootstrapSum {
    double[] sampleSums;
    long[] cummulativeTime;
    double meanSum;
    double stdev;
    double meanTime;
    public BootstrapSum(double[] sample, double samplingRate, int bootstraps, int size) {
        HashMap<Double, Integer> subsample;
        cummulativeTime = new long[bootstraps];
        sampleSums = new double[bootstraps];
        Mean meanTimePerBootstrap = new Mean();
        for (int i = 0; i < bootstraps; i++) {
            long start = System.nanoTime();
            subsample = BootstrapSample.GenerateSampleWithReplacementInFrequency(sample, size);
            sampleSums[i] = StatisticalSum.Sum(subsample, samplingRate);
            long time = Math.max(System.nanoTime() - start, 0);
            cummulativeTime[i] = time;
            meanTimePerBootstrap.increment(time);
        }
        meanSum = StatisticalMean.Mean(sampleSums);
        meanTime = meanTimePerBootstrap.getResult();
        StandardDeviation dev = new StandardDeviation(false);
        stdev = dev.evaluate(sampleSums);
    }
    public BootstrapSum(double[] sample, double samplingRate, int bootstraps) {
        this(sample, samplingRate, bootstraps, sample.length);
    }
    
    public double getMeanTime() {
        return meanTime;
    }
    
    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[] getSums() {
        return sampleSums;
    }

    public double Sum() {
        return meanSum;
    }

    public double StDev() {
        return stdev;
    }
}
