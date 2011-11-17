package edu.berkeley.cs.amplab.awesomedb;

import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.moment.Mean;

public class BootstrapKMeans {
    double[][] sampleCenters;
    long[] cummulativeTime;
    double[] meanCenter;
    double variance;
    double meanTime;
    long totalIterations;
    public BootstrapKMeans(double[] sample, int k, int nitter, double limit, int bootstraps, int size) {
        double[] subsample;
        cummulativeTime = new long[bootstraps];
        sampleCenters = new double[bootstraps][];
        Mean meanTimePerBootstrap = new Mean();
        
        for (int i = 0; i < bootstraps; i++) {
            totalIterations++;
            long start = System.nanoTime();
            subsample = BootstrapSample.GenerateSampleWithReplacement(sample, size);
            sampleCenters[i] = KMeansClustering.kmeanClustering(subsample, k, nitter, limit);
            long time = Math.max(System.nanoTime() - start, 0);
            Arrays.sort(sampleCenters[i]);
            cummulativeTime[i] = time;
            meanTimePerBootstrap.increment(time);
        }
        double[] avg = SamplingUtilities.VectorAvg(sampleCenters);
        double[] distances = new double[bootstraps];
        for (int i = 0; i < distances.length; i++) {
            totalIterations++;
            distances[i] = SamplingUtilities.L2Norm2(avg, sampleCenters[i]);
        }
        variance = StatisticalMean.Mean(distances);
        variance *= ((double)distances.length)/((double)(distances.length - 1));
        meanCenter = avg;
        meanTime = meanTimePerBootstrap.getResult();
        totalIterations += distances.length;
        
    }
    public BootstrapKMeans(double[] sample, int k, int nitter, double limit, int bootstraps) {
        this(sample, k, nitter, limit, bootstraps, sample.length);
    }
    
    public long getTotalIterations() {
        return totalIterations;
    }
    
    public double getMeanTime() {
        return meanTime;
    }
    
    public long[] getTimes() {
        return cummulativeTime;
    }

    public double[][] getMeans() {
        return sampleCenters;
    }

    public double[] Center() {
        return meanCenter;
    }

    public double Variance() {
        return variance;
    }
}
