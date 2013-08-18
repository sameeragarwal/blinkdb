package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;

public class CreateGlobalPopulation {
    public static void main(String[] args) throws MathException, IOException {
        if (args.length < 1) {
            System.err.println("Must specify filename");
            System.exit(1);
        }
        final double Alpha = 9; // shape
        final double Beta = 0.8; //scale;
        GammaDistributionImpl normalDistribution = new GammaDistributionImpl(Alpha, Beta);
        System.out.println("Start");
        FileWriter file = new FileWriter(args[0]);
        final long SIZE = (10L * 1024L * 1024L * 1024L) / 4;
        System.out.println(String.format("Size %1$s", SIZE));
        for (long i = 0; i < SIZE; i++) {
            if (i % 100000 == 0) {
                System.out.println(String.format("Looping %1$s", i));
            }
            file.write(String.format("%1$s\n",normalDistribution.sample()));
            file.flush();
        }
    }

}
