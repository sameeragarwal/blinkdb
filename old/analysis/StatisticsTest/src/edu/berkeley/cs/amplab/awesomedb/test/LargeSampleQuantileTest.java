package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.cs.amplab.awesomedb.BlbQuantile;
import edu.berkeley.cs.amplab.awesomedb.BootstrapQuantile;
import edu.berkeley.cs.amplab.awesomedb.StatisticalQuantile;

public class LargeSampleQuantileTest {
    final static int NUMBER_OF_BOOTSTRAPS = 100; // Picked at random
    final static int NUMBER_OF_BAGS = 150;
    final static int NUMBER_OF_BLB_BOOTSTRAPS = 25;
    final static double BAG_EXPONENT = 0.7;
    final static double[] QUANTILES = {0.25, 0.50, 0.75, 0.90, 0.95, 0.99};
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Must provide filename and sampling rate");
        }
        System.err.println(String.format("%1$s Start reading files", System.currentTimeMillis()));
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
        System.err.println(String.format("%1$s Done reading files", System.currentTimeMillis()));
        
        double[] statisticalQuantiles = new double[QUANTILES.length];
        long[] statisticalQuantileTimes = new long[QUANTILES.length];
        double[] bootstrapQuantiles = new double[QUANTILES.length];
        double[] bootstrapQuantileTotalTimes = new double[QUANTILES.length];
        double[] bootstrapQuantilePerBootstrapTimes = new double[QUANTILES.length];
        double[] blbQuantiles = new double[QUANTILES.length];
        double[] blbQuantilesTotalTimes = new double[QUANTILES.length];
        double[] blbQuantilesPerBagTimes = new double[QUANTILES.length];
        double[] blbQuantilesPerBootstrapTimes = new double[QUANTILES.length];
        
        for (int i = 0; i < QUANTILES.length; i++) {
            long start = System.nanoTime();
            statisticalQuantiles[i] = StatisticalQuantile.Quantile(sample, QUANTILES[i]);
            statisticalQuantileTimes[i] = Math.max(0, System.nanoTime() - start);
            
            BlbQuantile blbQuantileObj =
                    new BlbQuantile(sample, 
                            QUANTILES[i], 
                            BAG_EXPONENT,
                            NUMBER_OF_BAGS,
                            NUMBER_OF_BLB_BOOTSTRAPS);
            blbQuantiles[i] = blbQuantileObj.getQuantile();
            blbQuantilesTotalTimes[i] = blbQuantileObj.getTotalTime();
            blbQuantilesPerBagTimes[i] = blbQuantileObj.getPerBagTime();
            blbQuantilesPerBootstrapTimes[i] = blbQuantileObj.getPerBootstrapTime();
            
            BootstrapQuantile bootstrapQuantileObj =
                    new BootstrapQuantile(sample, QUANTILES[i], NUMBER_OF_BOOTSTRAPS);
            bootstrapQuantiles[i] = bootstrapQuantileObj.Quantile();
            bootstrapQuantileTotalTimes[i] = 
                    bootstrapQuantileObj.getTimes()[bootstrapQuantileObj.getTimes().length - 1];
            bootstrapQuantilePerBootstrapTimes[i] = bootstrapQuantileObj.getMeanTime();
            
            
        }
        
        for(int i = 0; i < QUANTILES.length; i++) {
            if (i > 0) {
                System.out.print(',');
            }
            System.out.print(String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%6$s,%8$s,%9$s",
                    statisticalQuantiles[i], //1
                    statisticalQuantileTimes[i], //2
                    bootstrapQuantiles[i], //3
                    bootstrapQuantilePerBootstrapTimes[i], //4
                    bootstrapQuantileTotalTimes[i], //5
                    blbQuantiles[i], //6
                    blbQuantilesPerBootstrapTimes[i], //7
                    blbQuantilesPerBagTimes[i], //8
                    blbQuantilesTotalTimes[i] //9
                    ));
        }
        System.out.print('\n');
    }
}
