package edu.berkeley.cs.amplab.awesomedb;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;



public class BlbQuantile {
    double[] bagQuantiles;
    double meanQuantile;
    long totalTime;
    Mean perbootstrapTime;
    Mean perbagTime;
    public BlbQuantile(double[] sample, 
            double quantile,
            double bagExp,
            int numberOfBags, 
            int numberOfBootstraps) {
        perbootstrapTime = new Mean();
        perbagTime = new Mean();
        calculateBlbQuantile(sample, 
                quantile,
                bagExp, 
                numberOfBags, 
                numberOfBootstraps, 
                perbootstrapTime, 
                perbagTime);
    }
    
    public double getQuantile() {
        return meanQuantile;
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
    
    private void calculateBlbQuantile(double[] sample, 
            double quantile,
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            Mean perbootstrapTime, 
            Mean perbagTime) {
        bagQuantiles = new double[numberOfBags];
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        long bootstrapTime = 0;
        Mean actualQuantile = new Mean();
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapQuantile bootstrapQuantile= new BootstrapQuantile(
                    sampleBag, 
                    quantile, 
                    numberOfBootstraps, 
                    sample.length);
            bagQuantiles[ii] = bootstrapQuantile.Quantile();
            actualQuantile.increment(bagQuantiles[ii]);
            perbootstrapTime.increment(bootstrapQuantile.getMeanTime());
            bootstrapTime += bootstrapQuantile.getTimes()[bootstrapQuantile.getTimes().length - 1];
            perbagTime.increment(bootstrapQuantile.getTimes()[bootstrapQuantile.getTimes().length - 1]);
            index = origIndex.clone();
        }
        
        meanQuantile = actualQuantile.getResult();
        
        totalTime = bootstrapTime;
    }
    
}
