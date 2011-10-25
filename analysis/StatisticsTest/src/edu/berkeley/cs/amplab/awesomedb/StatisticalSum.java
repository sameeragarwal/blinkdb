package edu.berkeley.cs.amplab.awesomedb;
public class StatisticalSum {
    public static double Sum(double[] samples, double samplingRate) {
        double sum = 0;
        for (double elt : samples) {
            sum += elt;
        }
        return sum * (1.0d / samplingRate);
    }
}
