package edu.berkeley.cs.amplab.awesomedb;

import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.util.FastMath;

/**
 * @author apanda
 * K-Means clustering algorithm for 1D double data, use KM++ to initialize. This should save us a 
 * bunch of the boxing/unboxing cost from the apache version
 */
public class KMeansClustering {
    static MersenneTwister rng = new MersenneTwister();
    public static double[] KMeansPlusPlusInitialization(double[] points, int k) {
        int initialPoint = rng.nextInt(points.length);
        double[] centers = new double[k];
        centers[0] = points[initialPoint];
        double[] distances = calculateMinDistances(points, centers[0]);
        double[] cdf = new double[points.length];
        for (int i = 1; i < k; i++) {
            double sum = 0;
            for (int j = 0; j < distances.length; j++) {
                sum += FastMath.pow(distances[j], 2.0);
                cdf[j] = sum;
            }
            double pointOnCurve = rng.nextDouble() * sum;
            int newCenter = 0;
            for (int j = 0; j < cdf.length; j++) {
                if (cdf[j] >= pointOnCurve) {
                    newCenter = j;
                    break;
                }
            }
            centers[i] = points[newCenter];
            distances = updateMinDistances(points, distances, centers[i]);
        }
        return centers;
    }
    
    public static double[] kmeanClustering(double[] points, int k, int nitter, double delta) {
        double[] centers = KMeansPlusPlusInitialization(points, k);
//        System.out.println("Initial centers");
//        for (double center : centers) {
//            System.out.println(center);
//        }
//        System.out.println("Done initial centers");
        int[] clusters = new int[points.length];
        int[] clusterLengths = new int[k];
        double[] clusterDistance = new double[points.length];
        double[][] clusteredPoints = new double[k][];
        int[] clusteredPointIndex = new int[k];
        for (int itter = 0; itter < nitter; itter++) {
            // Cluster points
            for (int i = 0; i < clusterLengths.length; i++) {
                clusterLengths[i] = 0;
            }
            for (int i = 0; i < points.length; i++) {
                clusters[i] = 0;
                clusterDistance[i] = FastMath.abs(points[i] - centers[0]);
            }
            clusterLengths[0] = points.length;
            for (int j = 1; j < centers.length; j++) {
                for (int i = 0; i < points.length; i++) {
                    double distance = FastMath.abs(points[i] - centers[j]);
                    if (distance < clusterDistance[i]) {
                        clusterLengths[clusters[i]]--;
                        clusterLengths[j]++;
                        clusterDistance[i] = distance;
                        clusters[i] = j;
                    }
                }
            }
            boolean foundNoPointCluster = false;
            //Account for no point clusters
            for (int i = 0; i < clusterLengths.length; i++) {
                if (clusterLengths[i] == 0) {
                    // Oops, no points clustered here, take max distance
                    double max = clusterDistance[0];
                    int maxIndex = 0;
                    for (int j = 0; j < clusterDistance.length; j++) {
                        if (clusterDistance[j] > max) {
                            max = clusterDistance[j];
                            maxIndex = j;
                        }
                    }
                    clusterDistance[maxIndex] = 0.0;
                    clusterLengths[clusters[maxIndex]]--;
                    clusterLengths[i]++;
                    clusters[maxIndex] = i;
                    centers[i] = points[maxIndex];
                    foundNoPointCluster = true;
                }
                else {
                    clusteredPoints[i] = new double[clusterLengths[i]];
                    clusteredPointIndex[i] = 0;
                }
            }
            
            if (foundNoPointCluster) {
                continue;
            }
            
            for (int i = 0; i < points.length; i++) {
                clusteredPoints[clusters[i]][clusteredPointIndex[clusters[i]]]= points[i];
                clusteredPointIndex[clusters[i]]++;
            }
            
            double maxDelta = -1.0;
            for (int i = 0; i < centers.length; i++) {
//                System.out.println("Cluster " + i);
//                for (double point : clusteredPoints[i]) {
//                    System.out.println(point);
//                }
//                System.out.println("Done cluster" + i);
                double newCenter = StatisticalMean.Mean(clusteredPoints[i]);
                maxDelta = FastMath.max(maxDelta, FastMath.abs(newCenter - centers[i]));
                centers[i] = newCenter;
            }
//            System.out.println("New centers");
//            for (double center : centers) {
//                System.out.println(center);
//            }
//            System.out.println("Done new centers");
            if (maxDelta < delta){
                return centers;
            }
        }
        return centers;
    }
    
    public static double[] calculateMinDistances(double[] points, double center) {
        double[] distance = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            distance[i] = FastMath.abs(points[i] - center);
        }
        return distance;
    }
    
    public static double[] updateMinDistances(
                            double[] points, 
                            double[] distances, 
                            double newCenter) {
        for (int i = 0; i < points.length; i++) {
            distances[i] = FastMath.min(distances[i], FastMath.abs(points[i] - newCenter));
        }
        return distances;
    }
    
    public static void main(String[] args) {
        double[] array1 = {1.0, 1.0, 1.0, 1.0, 1.0};
        double[] test1 = kmeanClustering(array1, 1, 1000, 0.0002);
        System.out.println(String.format("Test 1 %1$s", test1[0]));
        double[] array2 = {1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.1, 1.99, 2.0001};
        double[] test2 = kmeanClustering(array2, 2, 1000, 1.0e-5d);
        System.out.println(String.format("Test 2 %1$s %2$s", test2[0], test2[1]));
    }
}
