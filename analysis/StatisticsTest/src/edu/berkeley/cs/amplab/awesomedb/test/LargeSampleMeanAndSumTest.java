package edu.berkeley.cs.amplab.awesomedb.test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.cs.amplab.awesomedb.BlbMean;
import edu.berkeley.cs.amplab.awesomedb.BlbSum;
import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.BootstrapSum;
import edu.berkeley.cs.amplab.awesomedb.StatisticalMean;
import edu.berkeley.cs.amplab.awesomedb.StatisticalSum;

public class LargeSampleMeanAndSumTest {
    final static int NUMBER_OF_BOOTSTRAPS = 150; // Picked at random
    final static int NUMBER_OF_BAGS = 200;
    final static int NUMBER_OF_BLB_BOOTSTRAPS = 50;
    final static double BAG_EXPONENT = 0.7;
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Must provide filename and sampling rate");
        }
        double samplingRate = Double.parseDouble(args[1]);
        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        List<Double> doublesList = new ArrayList<Double>();
        String line = null;
        while ((line = file.readLine()) != null) {
            doublesList.add(Double.parseDouble(line));
        }
        double[] sample = new double[doublesList.size()];
        for (int i = 0; i < sample.length; i++) {
            sample[i] = doublesList.get(i).doubleValue();
        }
        
        // Mean
        long start = System.nanoTime();
        double statisticalMean = StatisticalMean.Mean(sample);
        long statisticalMeanTime = Math.max(0, System.nanoTime() - start);
        start = 0;
        
        BootstrapMean bootstrapMeanObj = new BootstrapMean(sample, NUMBER_OF_BOOTSTRAPS);
        double bootstrapMean = bootstrapMeanObj.Mean();
        double bootstrapMeanTotalTime = 
                bootstrapMeanObj.getTimes()[bootstrapMeanObj.getTimes().length - 1];
        double bootstrapMeanPerBootstrapTime = bootstrapMeanObj.getMeanTime();
        
        BlbMean blbMeanObj = new BlbMean(sample, 
                BAG_EXPONENT, 
                NUMBER_OF_BAGS, 
                NUMBER_OF_BLB_BOOTSTRAPS);
        double blbMean = blbMeanObj.getMean();
        double blbMeanTotalTime = blbMeanObj.getTotalTime();
        double blbMeanPerBagTime = blbMeanObj.getPerBagTime();
        double blbMeanPerBootstrapTime = blbMeanObj.getPerBootstrapTime();
        
        // Sum
        start = System.nanoTime();
        double statisticalSum = StatisticalSum.Sum(sample, samplingRate);
        long statisticalSumTime = Math.max(0, System.nanoTime() - start);
        start = 0;
        
        BootstrapSum bootstrapSumObj = new BootstrapSum(sample, samplingRate, NUMBER_OF_BOOTSTRAPS);
        double bootstrapSum = bootstrapSumObj.Sum();
        double bootstrapSumTotalTime = 
                bootstrapSumObj.getTimes()[bootstrapSumObj.getTimes().length - 1];
        double bootstrapSumPerBootstrapTime = bootstrapSumObj.getMeanTime();
        
        BlbSum blbSumObj = new BlbSum(sample,
                samplingRate,
                BAG_EXPONENT, 
                NUMBER_OF_BAGS, 
                NUMBER_OF_BLB_BOOTSTRAPS);
        double blbSum = blbSumObj.getSum();
        double blbSumTotalTime = blbSumObj.getTotalTime();
        double blbSumPerBagTime = blbSumObj.getPerBagTime();
        double blbSumPerBootstrapTime = blbSumObj.getPerBootstrapTime();
        
        System.out.println(String.format(
                "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s,%10$s,%11$s,%12$s,%13$s,%14$s,%15$s,%16$s,%17$s,%18$s", 
                statisticalMean, //1
                statisticalMeanTime, //2
                bootstrapMean, //3
                bootstrapMeanPerBootstrapTime, //4
                bootstrapMeanTotalTime, //5
                blbMean, //6
                blbMeanPerBootstrapTime, //7
                blbMeanPerBagTime, //8
                blbMeanTotalTime, //9
                statisticalSum, //10
                statisticalSumTime, //11
                bootstrapSum, //12
                bootstrapSumPerBootstrapTime, //13
                bootstrapSumTotalTime, //14
                blbSum, //15
                blbSumPerBootstrapTime, //16
                blbSumPerBagTime, //17
                blbSumTotalTime //18
                ));
    }
}
