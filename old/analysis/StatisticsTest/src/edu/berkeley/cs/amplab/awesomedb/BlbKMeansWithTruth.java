package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;

public class BlbKMeansWithTruth {
    double[][] bagCenter;
    double[] bagVariance;
    double[] meanCenter;
    double meanVariance;
    long totalTime;
    long totalIterations;
    Mean perbootstrapTime;
    Mean perbagTime;
    public BlbKMeansWithTruth(
            double[] sample, 
            int k, 
            int nitter, 
            double limit,
            double[] truth,
            double bagExp, 
            int numberOfBags, 
            int numberOfBootstraps) {
        perbootstrapTime = new Mean();
        perbagTime = new Mean();
        calculateBlbKMeans(sample,
                k,
                nitter,
                limit,
                truth,
                bagExp, 
                numberOfBags, 
                numberOfBootstraps, 
                perbootstrapTime, 
                perbagTime);
    }
    
    public double[] getCenter() {
        return meanCenter;
    }
    
    public double getVariance() {
        return meanVariance;
    }
    
    public double getTotalTime() {
        return (double)totalTime;
    }
    
    public double getPerBootstrapTime() {
        return perbootstrapTime.getResult();
    }
    
    public double getPerBagTime() {
        return perbagTime.getResult();
    }
    
    private void calculateBlbKMeans(
            double[] sample, 
            int k, 
            int nitter, 
            double limit,
            double[] truth,
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            Mean perbootstrapTime, 
            Mean perbagTime) {
        bagCenter = new double[numberOfBags][];
        bagVariance = new double[numberOfBags];
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        long bootstrapTime = 0;
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapKMeansWithTruth mean = new BootstrapKMeansWithTruth(
                    sampleBag,
                    k,
                    nitter,
                    limit,
                    truth,
                    numberOfBootstraps,
                    sample.length);
            bagCenter[ii] = mean.Center();
            bagVariance[ii] = mean.Variance();
            perbootstrapTime.increment(mean.getMeanTime());
            bootstrapTime += mean.getTimes()[mean.getTimes().length - 1];
            perbagTime.increment(mean.getTimes()[mean.getTimes().length - 1]);
            index = origIndex.clone();
        }
        
        meanCenter = SamplingUtilities.VectorAvg(bagCenter);
        meanVariance = StatisticalMean.Mean(bagVariance);
        
        totalTime = bootstrapTime;
    }


}
