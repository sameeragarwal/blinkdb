package edu.berkeley.cs.amplab.awesomedb.test;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.AbstractContinuousDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.util.FastMath;
//import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.BootstrapMean;
import edu.berkeley.cs.amplab.awesomedb.BootstrapSample;
import edu.berkeley.cs.amplab.awesomedb.EuclideanDoublePoint;
import edu.berkeley.cs.amplab.awesomedb.StatisticalMean;

import org.apache.commons.math.stat.clustering.Cluster;
import org.apache.commons.math.stat.clustering.Clusterable;
import org.apache.commons.math.stat.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math.stat.descriptive.moment.FirstMoment;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.stat.descriptive.moment.ThirdMoment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class OperatorTest {
    public static void CalculateMoments(
            List<Cluster<EuclideanDoublePoint>> kmeans, FirstMoment first,
            SecondMoment second, ThirdMoment third) {
        for (int i = 0; i < kmeans.size(); i++) {
            double point = kmeans.get(i).getCenter().getPoint()[0];
            first.increment(point);
            second.increment(point);
            third.increment(point);
        }
    }

    public static void main(String args[]) throws MathException {
        MersenneTwister rng = new MersenneTwister();
        final long TESTS = 500;
        final long SMOOTHING = 1000;
        double mean = rng.nextDouble() * 100;
        double stdev = rng.nextDouble() * 1000;
        NormalDistributionImpl normalDistribution = new NormalDistributionImpl(
                mean, stdev);
        AbstractContinuousDistribution distribution = normalDistribution;
        KMeansPlusPlusClusterer<EuclideanDoublePoint> clusterer = new KMeansPlusPlusClusterer<EuclideanDoublePoint>(
                new Random(),
                KMeansPlusPlusClusterer.EmptyClusterStrategy.LARGEST_VARIANCE);
        EuclideanDoublePoint[] points = new EuclideanDoublePoint[1000000];
        double[] population = distribution.sample(1000000);
        System.out.println("Calculated population");
        for (int i = 0; i < points.length; i++) {
            points[i] = new EuclideanDoublePoint(new double[] { population[i] });
        }
        List<Cluster<EuclideanDoublePoint>> kmeans = clusterer.cluster(
                Arrays.asList(points), 10, 1000);
        FirstMoment first = new FirstMoment();
        SecondMoment second = new SecondMoment();
        ThirdMoment third = new ThirdMoment();
        CalculateMoments(kmeans, first, second, third);
        double moment1 = first.getResult();
        double moment2 = second.getResult();
        double moment3 = third.getResult();
        System.out.println("Calculated population data");
        /*FileWriter sampleWriter = new FileWriter(String.format(
                "stat_mean_%1$s_%2$s.dat", mean, stdev));
        sampleWriter.write("Samples,Time\n");
        //System.out.println("Trial   Mean   StDev   Mean   Error");
        for (int i = 0; i < TESTS; i++) {
            int sampleSize = (i + 1) * 10;
            Mean errorMean = new Mean();
            for (int j = 0; j < SMOOTHING; j++) {

                double[] sample = distribution.sample(sampleSize);
                //long startTime = System.nanoTime();
                double calculatedMean = StatisticalMean.Mean(sample);
                //long stopTime = System.nanoTime();
                double error = FastMath.abs(calculatedMean - mean);
                errorMean.increment(error);
            }
            sampleWriter.write(String.format("%1$s,%2$s\n", sampleSize,
                    errorMean.getResult()));
            System.out.println(String.format("Done sampling for %1$s", sampleSize));
            //§double error = 
        }*/

        try {
            FileWriter moment1Writer = new FileWriter(String.format(
                    "stat_kmean_m1_%1$s_%2$s.dat", mean, stdev));
            FileWriter moment2Writer = new FileWriter(String.format(
                    "stat_kmean_m2_%1$s_%2$s.dat", mean, stdev));
            FileWriter moment3Writer = new FileWriter(String.format(
                    "stat_kmean_m3_%1$s_%2$s.dat", mean, stdev));
            moment1Writer.write("Samples,Error\n");
            moment2Writer.write("Samples,Error\n");
            moment3Writer.write("Samples,Error\n");
            //System.out.println("Trial   Mean   StDev   Mean   Error");
            /*for (int i = 0; i < TESTS; i++) {
                int sampleSize = (i + 1) * 10;
                Mean errorMean1 = new Mean();
                Mean errorMean2 = new Mean();
                Mean errorMean3 = new Mean();
                for (int j = 0; j < SMOOTHING; j++) {
                    double[] samples = distribution.sample(sampleSize);
                    EuclideanDoublePoint[] samplePoints = new EuclideanDoublePoint[sampleSize];
                    for (int ii = 0; ii < sampleSize; ii++) {
                        samplePoints[ii] = points[((int) Math.round(Math.abs(samples[ii])
                                * points.length)) % points.length];
                    }
                    kmeans = clusterer.cluster(Arrays.asList(samplePoints), 10,
                            1000);
                    first = new FirstMoment();
                    second = new SecondMoment();
                    third = new ThirdMoment();
                    CalculateMoments(kmeans, first, second, third);
                    //long startTime = System.nanoTime();
                    //long stopTime = System.nanoTime();                    
                    errorMean1.increment(FastMath.abs(first.getResult()
                            - moment1));
                    errorMean2.increment(FastMath.abs(second.getResult()
                            - moment2));
                    errorMean3.increment(FastMath.abs(third.getResult()
                            - moment3));
                }
                moment1Writer.write(String.format("%1$s,%2$s\n", sampleSize,
                        errorMean1.getResult()));
                moment2Writer.write(String.format("%1$s,%2$s\n", sampleSize,
                        errorMean2.getResult()));
                moment3Writer.write(String.format("%1$s,%2$s\n", sampleSize,
                        errorMean3.getResult()));
                moment1Writer.flush();
                moment2Writer.flush();
                moment3Writer.flush();
                System.out.println(String.format("Done sampling for %1$s",
                        sampleSize));
                //§double error = 
            }*/

            for (int i = 149; i < TESTS; i++) {
                int sampleSize = (i + 1) * 10;
                FileWriter bootstrapWriter1 = new FileWriter(String.format(
                        "boot_kmean_m1_%1$s_%2$s_%3$s.dat", mean, stdev,
                        sampleSize));
                FileWriter bootstrapWriter2 = new FileWriter(String.format(
                        "boot_kmean_m2_%1$s_%2$s_%3$s.dat", mean, stdev,
                        sampleSize));
                FileWriter bootstrapWriter3 = new FileWriter(String.format(
                        "boot_kmean_m3_%1$s_%2$s_%3$s.dat", mean, stdev,
                        sampleSize));
                bootstrapWriter1.write("Bootstraps,Error\n");
                bootstrapWriter2.write("Bootstraps,Error\n");
                bootstrapWriter3.write("Bootstraps,Error\n");
                for (int numbootstraps = sampleSize / 10; numbootstraps < Math
                        .min(sampleSize * 5, sampleSize * sampleSize); numbootstraps++) {
                    Mean errorMean1 = new Mean();
                    Mean errorMean2 = new Mean();
                    Mean errorMean3 = new Mean();
                    for (int j = 0; j < SMOOTHING; j++) {
                        //double[] sample = distribution.sample(sampleSize);
                        double[] sample = new double[sampleSize];
                        for (int ii = 0; ii < sampleSize; ii++) {
                            sample[ii] = (double)rng.nextInt(points.length);
                        }
                        //long startTime = System.nanoTime();
                        Mean estimate1 = new Mean();
                        Mean estimate2 = new Mean();
                        Mean estimate3 = new Mean();
                        for (int k = 0; k < numbootstraps; k++) {
                            double[] bootstrapped = BootstrapSample
                                    .GenerateSampleWithReplacement(sample);
                            EuclideanDoublePoint[] samplePoints = new EuclideanDoublePoint[sampleSize];
                            for (int ii = 0; ii < sampleSize; ii++) {
                                samplePoints[ii] = points[(int) Math.round(bootstrapped[ii])];
                            }
                            kmeans = clusterer.cluster(
                                    Arrays.asList(samplePoints), 10, 1000);
                            first = new FirstMoment();
                            second = new SecondMoment();
                            third = new ThirdMoment();
                            CalculateMoments(kmeans, first, second, third);
                            estimate1.increment(first.getResult());
                            estimate2.increment(second.getResult());
                            estimate3.increment(third.getResult());
                        }
                        errorMean1.increment(FastMath.abs(estimate1.getResult() - moment1));
                        errorMean2.increment(FastMath.abs(estimate2.getResult() - moment2));
                        errorMean3.increment(FastMath.abs(estimate3.getResult() - moment3));
                    }
                    bootstrapWriter1.write(String.format("%1$s,%2$s\n", numbootstraps,
                            errorMean1.getResult()));
                    bootstrapWriter2.write(String.format("%1$s,%2$s\n", numbootstraps,
                            errorMean2.getResult()));
                    bootstrapWriter3.write(String.format("%1$s,%2$s\n", numbootstraps,
                            errorMean3.getResult()));
                    bootstrapWriter1.flush();
                    bootstrapWriter2.flush();
                    bootstrapWriter3.flush();

                }
                bootstrapWriter1.close();
                bootstrapWriter2.close();
                bootstrapWriter3.close();
            }
            /*for (int i = 149; i < TESTS; i++) {
                int sampleSize = (i + 1) * 10;
                FileWriter bootstrapWriter = new FileWriter(String.format(
                        "boot_mean_%1$s_%2$s_%3$s.dat", mean, stdev, sampleSize));
                bootstrapWriter.write("Bootstraps,Error\n");
                for (int numbootstraps = sampleSize / 10; 
                        numbootstraps < Math.min(sampleSize * 50, sampleSize * sampleSize);
                        numbootstraps++) {
                    Mean errorMean = new Mean();
                    for (int j = 0; j < SMOOTHING; j++) {
                        double[] sample = distribution.sample(sampleSize);
                        //long startTime = System.nanoTime();
                        BootstrapMean bmean = new BootstrapMean(sample, numbootstraps);
                        double calculatedMean = bmean.Mean();
                        //long stopTime = System.nanoTime();
                        double error = FastMath.abs(calculatedMean - mean);
                        errorMean.increment(error);
                    }
                    bootstrapWriter.write(String.format("%1$s,%2$s\n", numbootstraps,
                        errorMean.getResult()));
                    System.out.println(String.format("Done bootstraping for %1$s %2$s", sampleSize, numbootstraps));
                }
                bootstrapWriter.flush();
                bootstrapWriter.close();
            }*/
        } catch (IOException e) {

        }
        /*for (int i = 0; i < TESTS; i++) {
            
            
            double[] sample = distribution.sample(1000);
            double calcMean = StatisticalMean.Mean(sample);
            double calcError = StatisticalMean.StandardError(sample);
            BootstrapMean bootstrap_mean = new BootstrapMean(sample, 100000);
            System.out.println(String.format(
                    "%1$s    %2$s    %3$s    %4$s    %5$s    %6$s    %7$s", 
                    i, 
                    mean, 
                    stdev, 
                    calcMean, 
                    calcError, 
                    bootstrap_mean.Mean(), 
                    bootstrap_mean.StDev()));
        }*/
    }
}
