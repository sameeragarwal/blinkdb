package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;

public class BlbMean {
    double[] bagMeans;
    double meanMean;
    long totalTime;
    Mean perbootstrapTime;
    Mean perbagTime;
    public BlbMean(double[] sample, double bagExp, int numberOfBags, int numberOfBootstraps) {
        perbootstrapTime = new Mean();
        perbagTime = new Mean();
        calculateBlbMean(sample, 
                bagExp, 
                numberOfBags, 
                numberOfBootstraps, 
                perbootstrapTime, 
                perbagTime);
    }
    
    public double getMean() {
        return meanMean;
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
    
    private void calculateBlbMean(double[] sample, 
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            Mean perbootstrapTime, 
            Mean perbagTime) {
        bagMeans = new double[numberOfBags];
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        long bootstrapTime = 0;
        Mean actualMean = new Mean();
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapMean mean = new BootstrapMean(sampleBag, numberOfBootstraps, sample.length);
            bagMeans[ii] = mean.Mean();
            actualMean.increment(bagMeans[ii]);
            perbootstrapTime.increment(mean.getMeanTime());
            bootstrapTime += mean.getTimes()[mean.getTimes().length - 1];
            perbagTime.increment(mean.getTimes()[mean.getTimes().length - 1]);
            index = origIndex.clone();
        }
        
        meanMean = actualMean.getResult();
        
        totalTime = bootstrapTime;
    }
    
}
