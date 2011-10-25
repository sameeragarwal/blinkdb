package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;

public class BlbCount {
    double[] bagCounts;
    double meanCount;
    long totalTime;
    Mean perbootstrapTime;
    Mean perbagTime;
    public BlbCount(double[] sample,
            double samplingRate,
            double target,
            double bagExp, 
            int numberOfBags, 
            int numberOfBootstraps) {
        perbootstrapTime = new Mean();
        perbagTime = new Mean();
        calculateBlbCount(sample, 
                samplingRate,
                target,
                bagExp, 
                numberOfBags, 
                numberOfBootstraps, 
                perbootstrapTime, 
                perbagTime);
    }
    
    public double getCount() {
        return meanCount;
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
    
    private void calculateBlbCount(double[] sample, 
            double samplingRate,
            double target,
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            Mean perbootstrapTime, 
            Mean perbagTime) {
        bagCounts = new double[numberOfBags];
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        long bootstrapTime = 0;
        Mean actualCount = new Mean();
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapCount count = new BootstrapCount(sampleBag, 
                    samplingRate,
                    target,
                    numberOfBootstraps, 
                    sample.length);
            bagCounts[ii] = count.Count();
            actualCount.increment(bagCounts[ii]);
            perbootstrapTime.increment(count.getMeanTime());
            bootstrapTime += count.getTimes()[count.getTimes().length - 1];
            perbagTime.increment(count.getTimes()[count.getTimes().length - 1]);
            index = origIndex.clone();
        }
        
        meanCount = actualCount.getResult();
        
        totalTime = bootstrapTime;
    }

}
