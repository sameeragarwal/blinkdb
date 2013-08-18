package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;

public class BlbSum {
    double[] bagSums;
    double meanSum;
    long totalTime;
    Mean perbootstrapTime;
    Mean perbagTime;
    public BlbSum(double[] sample,
            double samplingRate, 
            double bagExp, 
            int numberOfBags, 
            int numberOfBootstraps) {
        perbootstrapTime = new Mean();
        perbagTime = new Mean();
        calculateBlbSum(sample, 
                samplingRate,
                bagExp, 
                numberOfBags, 
                numberOfBootstraps, 
                perbootstrapTime, 
                perbagTime);
    }
    
    public double getSum() {
        return meanSum;
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
    
    private void calculateBlbSum(double[] sample, 
            double samplingRate,
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            Mean perbootstrapTime, 
            Mean perbagTime) {
        bagSums = new double[numberOfBags];
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        long bootstrapTime = 0;
        Mean actualSum = new Mean();
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapSum sum = new BootstrapSum(sampleBag, 
                    samplingRate,
                    numberOfBootstraps, 
                    sample.length);
            bagSums[ii] = sum.Sum();
            actualSum.increment(bagSums[ii]);
            perbootstrapTime.increment(sum.getMeanTime());
            bootstrapTime += sum.getTimes()[sum.getTimes().length - 1];
            perbagTime.increment(sum.getTimes()[sum.getTimes().length - 1]);
            index = origIndex.clone();
        }
        
        meanSum = actualSum.getResult();
        
        totalTime = bootstrapTime;
    }
    
}
