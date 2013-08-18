package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;

import org.apache.commons.math.util.FastMath;

import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.BootstrapSample;
import edu.berkeley.cs.amplab.awesomedb.SamplingUtilities;
import edu.berkeley.cs.amplab.awesomedb.StatisticalMean;

public class ConvergenceTest {
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
        final int MEAN_ESTIMATES = 2000;
        final int START_SAMPLE_SIZE = 200;
        final int END_SAMPLE_SIZE = 10000;
        final int NUMBER_OF_BOOTSTRAPS = 150; // Picked at random
        final int NUMBER_OF_BAGS = 200;
        final int NUMBER_OF_BLB_BOOTSTRAPS = 50;
        double[] baseSample = generateSample(POPULATION_SIZE);
        FileWriter file = new FileWriter(String.format("converge.hugerbag.dat"));
        file.write("size,truth,truth_time,bootstrap,per_bootstrap_time,bootstrap_total_time,blb,per_bootstrap_blb_time,per_bag_time,total_blb_time,sem,sem_time\n");
        for (int i = START_SAMPLE_SIZE; i < END_SAMPLE_SIZE; i+= 10) {
            Variance var = new Variance();
            long startAccurate = System.nanoTime();
            for (int estimate = 0; estimate < MEAN_ESTIMATES; estimate++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                Mean mean = new Mean();
                var.increment(mean.evaluate(sample));
            }
            System.out.println("Finished estimating true mean");
            double trueVariance = var.getResult();
            long endAccurate = System.nanoTime();
            long accurateTime = endAccurate - startAccurate;
            Mean bootstrapDistanceMean = new Mean();
            Mean semDistanceMean = new Mean();
            Mean perbootstrapTime = new Mean();
            Mean bootstrapTotalTime = new Mean();
            Mean blbDistanceMean = new Mean();
            Mean perbootstrapBlbTime = new Mean();
            Mean perbagTime = new Mean();
            Mean blbTotalTime = new Mean();
            Mean semTime = new Mean();
            for (int exp = 0; exp < SMOOTHING; exp++) {
                double[] sample = BootstrapSample.GenerateSampleWithReplacement(baseSample, i);
                bootstrapExperiment(sample, NUMBER_OF_BOOTSTRAPS, trueVariance,
                        bootstrapDistanceMean, perbootstrapTime,
                        bootstrapTotalTime);
                
                semExperiment(sample, trueVariance, semDistanceMean, semTime);
                blbExperiment(sample, 0.9, NUMBER_OF_BAGS,
                        NUMBER_OF_BLB_BOOTSTRAPS,
                        trueVariance, blbDistanceMean, perbootstrapBlbTime,
                        perbagTime, blbTotalTime);
            }
            System.out.println(String.format("huge bag sem = %1$s, blb = %2$s, boot = %3$s", semDistanceMean.getResult(),
                    blbDistanceMean.getResult(), bootstrapDistanceMean.getResult()));
            file.write(String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s,%10$s,%11$s,%12$s\n", 
                    i, //1
                    trueVariance, //2
                    accurateTime,//3
                    bootstrapDistanceMean.getResult(),//4
                    perbootstrapTime.getResult(),//5
                    bootstrapTotalTime.getResult(),//6
                    blbDistanceMean.getResult(),//7
                    perbootstrapBlbTime.getResult(),//8
                    perbagTime.getResult(),//9
                    blbTotalTime.getResult(),//10
                    semDistanceMean.getResult(),//11
                    semTime.getResult())); //12
            file.flush();
            System.out.println(String.format("Explored %1$s of %2$s", i, END_SAMPLE_SIZE));
        }
        
    }

    private static void semExperiment(double[] sample, double trueVariance,
            Mean semDistanceMean, Mean semTimeMean) {
        long start = System.nanoTime();
        double semVar = FastMath.pow(StatisticalMean.StandardError(sample), 2);
        
        semDistanceMean.increment(FastMath.abs(semVar - trueVariance));
        semTimeMean.increment(FastMath.max(0, System.nanoTime() - start));
    }

    private static void bootstrapExperiment(double[] sample,
            final int numberOfBootstraps, double trueVariance,
            Mean bootstrapDistanceMean, Mean perbootstrapTime,
            Mean bootstrapTotalTime) {
        BootstrapMean bootstrap = new BootstrapMean(sample, numberOfBootstraps);
        perbootstrapTime.increment(bootstrap.getMeanTime());
        bootstrapTotalTime.increment(bootstrap.getTimes()[bootstrap.getTimes().length - 1]);
        Variance bootstrapVarianceObj = new Variance();
        double bootstrapVariance = bootstrapVarianceObj.evaluate(bootstrap.getMeans());
        bootstrapDistanceMean.increment(FastMath.abs(bootstrapVariance - trueVariance));
    }
    
    private static void blbExperiment(double[] sample, 
            double bagExp,
            final int numberOfBags,
            final int numberOfBootstraps, 
            double trueVariance,
            Mean bootstrapDistanceMean, 
            Mean perbootstrapTime, 
            Mean perbagTime,
            Mean bootstrapTotalTime) {
        int bag_size = (int)FastMath.ceil(FastMath.pow(sample.length, bagExp));
        int[] index = new int[sample.length];
        for (int ii = 0; ii < sample.length; ii++) {
            index[ii] = ii;
        }
        int[] origIndex = index.clone();
        Mean varianceBagMean = new Mean();
        long bootstrapTime = 0;
        for (int ii = 0; ii < numberOfBags; ii ++) {
            SamplingUtilities.KnuthShuffle(index);
            double[] sampleBag = new double[bag_size];
            for (int jj = 0; jj < bag_size; jj++) {
                sampleBag[jj] = sample[index[jj]];
            }
            BootstrapMean mean = new BootstrapMean(sampleBag, numberOfBootstraps, sample.length);
            Variance bootstrapVarianceObj = new Variance();
            double bootstrapVariance = bootstrapVarianceObj.evaluate(mean.getMeans());
            varianceBagMean.increment(bootstrapVariance);
            perbootstrapTime.increment(mean.getMeanTime());
            bootstrapTime += mean.getTimes()[mean.getTimes().length - 1];
            perbagTime.increment(mean.getTimes()[mean.getTimes().length - 1]);
            index = origIndex.clone();
        }
        bootstrapDistanceMean.increment(FastMath.abs(varianceBagMean.getResult() - 
                trueVariance));
        bootstrapTotalTime.increment(bootstrapTime);
    }

}
