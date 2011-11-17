package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.util.FastMath;

import edu.berkeley.cs.amplab.awesomedb.BlbKMeansWithTruth;
import edu.berkeley.cs.amplab.awesomedb.BootstrapKMeansWithTruth;
import edu.berkeley.cs.amplab.awesomedb.BootstrapSample;
import edu.berkeley.cs.amplab.awesomedb.KMeansClustering;
import edu.berkeley.cs.amplab.awesomedb.SamplingUtilities;

public class KMeansTruthTest {
    private static double s_alpha;
    private static double s_beta;
    public static double[] generateSample(int sampleSize) throws MathException {
        s_alpha = 9; // shape
        s_beta = 0.8; //scale;
        GammaDistributionImpl normalDistribution = new GammaDistributionImpl(s_alpha, s_beta);
        AbstractContinuousDistribution distribution = normalDistribution;
        return distribution.sample(sampleSize);
    }

    public static double[] BinomialSample(double p, double[] population) throws MathException {
        ArrayList<Double> sampleObjects = new ArrayList<Double>();
        BinomialDistributionImpl binomial = new BinomialDistributionImpl(1, p);
        
        for (double value : population) {
            int c = binomial.sample();
            assert(c == 0 || c == 1);
            if (c == 1) {
                sampleObjects.add(new Double(value));
            }
            
        }
        double[] sample = new double[sampleObjects.size()];
        for (int ii = 0; ii < sample.length; ii++) {
            sample[ii] = ((Double)sampleObjects.get(ii)).doubleValue();
        }
        return sample;
    }
    public static void main(String[] args) throws MathException, IOException {
        final int SMOOTHING = 100;
        final int POPULATION_SIZE = 100000;
        final int MEAN_ESTIMATES = 20000;
        final int START_SAMPLE_SIZE = 200;
        final int END_SAMPLE_SIZE = 10000;
        final int NUMBER_OF_BOOTSTRAPS = 300; // Picked at random
        final int NUMBER_OF_BAGS = 200;
        final int NUMBER_OF_BLB_BOOTSTRAPS = 50;
        final double BAG_EXP = 0.7;
        final double DELTA = 1.0e-7;
        final int NITTER = 1000;
        final int K = 5;
        double[] baseSample = generateSample(POPULATION_SIZE);
        double[][] scratchKMeans = new double[MEAN_ESTIMATES][];
        FileWriter file = new FileWriter(String.format("kmeans.dat"));
        file.write("size,truth,truth_time,bootstrap,per_bootstrap_time,bootstrap_total_time,blb,per_bootstrap_blb_time,per_bag_time,total_blb_time\n");
        for (int i = START_SAMPLE_SIZE; i < END_SAMPLE_SIZE; i+= 10) {
            long startAccurate = System.nanoTime();
            for (int estimate = 0; estimate < MEAN_ESTIMATES; estimate++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                scratchKMeans[estimate] = KMeansClustering.kmeanClustering(sample, K, NITTER, DELTA);
            }
            double[] trueCenter = SamplingUtilities.VectorAvg(scratchKMeans);
            double trueKMeanVar = SamplingUtilities.VectorVariance(scratchKMeans);
            System.out.println("Finished estimating true variance");
            long endAccurate = System.nanoTime();
            long accurateTime = endAccurate - startAccurate;
            Mean bootstrapDistanceMean = new Mean();
            Mean perbootstrapTime = new Mean();
            Mean bootstrapTotalTime = new Mean();
            Mean blbDistanceMean = new Mean();
            Mean perbootstrapBlbTime = new Mean();
            Mean perbagTime = new Mean();
            Mean blbTotalTime = new Mean();
            for (int exp = 0; exp < SMOOTHING; exp++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                bootstrapExperiment(sample, K, NITTER, DELTA, NUMBER_OF_BOOTSTRAPS, trueCenter, 
                        trueKMeanVar,
                        bootstrapDistanceMean, perbootstrapTime,
                        bootstrapTotalTime);
                
                blbExperiment(sample, K, NITTER, DELTA, BAG_EXP, NUMBER_OF_BAGS,
                        NUMBER_OF_BLB_BOOTSTRAPS,
                        trueCenter, trueKMeanVar, blbDistanceMean, perbootstrapBlbTime,
                        perbagTime, blbTotalTime);
            }
            System.out.println(String.format("bag actual = %1$s, blb = %2$s, boot = %3$s", trueKMeanVar,
                    blbDistanceMean.getResult(), bootstrapDistanceMean.getResult()));
            file.write(String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s,%10$s\n", 
                    i, //1
                    trueKMeanVar, //2
                    accurateTime,//3
                    bootstrapDistanceMean.getResult(),//4
                    perbootstrapTime.getResult(),//5
                    bootstrapTotalTime.getResult(),//6
                    blbDistanceMean.getResult(),//7
                    perbootstrapBlbTime.getResult(),//8
                    perbagTime.getResult(),//9
                    blbTotalTime.getResult()));//10
                    
            file.flush();
            System.out.println(String.format("Explored %1$s of %2$s", i, END_SAMPLE_SIZE));
        }
        
    }

    private static void bootstrapExperiment(double[] sample,
            int k, int nitter, double delta,
            final int numberOfBootstraps, double[] trueCenter, double trueVariance,
            Mean bootstrapDistanceMean, Mean perbootstrapTime,
            Mean bootstrapTotalTime) {
        BootstrapKMeansWithTruth bootstrap = 
                new BootstrapKMeansWithTruth(sample,k, nitter, delta, trueCenter, numberOfBootstraps);
        perbootstrapTime.increment(bootstrap.getMeanTime());
        bootstrapTotalTime.increment(bootstrap.getTimes()[bootstrap.getTimes().length - 1]);
        //Variance bootstrapVarianceObj = new Variance();
        double bootstrapVariance = bootstrap.Variance();
        bootstrapDistanceMean.increment(FastMath.abs(bootstrapVariance - trueVariance));
    }
    
    private static void blbExperiment(double[] sample, 
            int k,
            int nitter,
            double delta,
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            double[] trueCenter,
            double trueVariance,
            Mean blbDistanceMean, 
            Mean perbootstrapTime, 
            Mean perbagTime,
            Mean blbTotalTime) {
        BlbKMeansWithTruth blb = 
                new BlbKMeansWithTruth(sample, k, nitter, delta, trueCenter, bagExp, numberOfBags, numberOfBootstraps);
        blbDistanceMean.increment(FastMath.abs(blb.getVariance() - 
                trueVariance));
        blbTotalTime.increment(blb.getTotalTime());
        perbootstrapTime.increment(blb.getPerBootstrapTime());
        perbagTime.increment(blb.getPerBagTime());
    }

}
