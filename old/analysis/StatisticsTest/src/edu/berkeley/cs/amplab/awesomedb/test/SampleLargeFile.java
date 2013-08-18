package edu.berkeley.cs.amplab.awesomedb.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.BinomialDistributionImpl;

public class SampleLargeFile {
    public static void main(String args[]) throws NumberFormatException, IOException, MathException
    {
        if (args.length != 1) {
            System.out.println("Must provide filename and sampling rate");
        }
        BufferedReader file = new BufferedReader(new FileReader(args[0]));
        double baseRate = 0.0001953125;
        double[] rates = new double[9];
        FileWriter[] files = new FileWriter[9];
        BinomialDistributionImpl[] distributions = new BinomialDistributionImpl[9];
        for (int i = 0; i < 9; i++) {
            rates[i] = baseRate;
            baseRate *= 2.0;
            files[i] = new FileWriter(String.format("Samples_rates%1$s_p.dat", rates[i]));
            distributions[i] = new BinomialDistributionImpl(1, rates[i]);
        }
        String line = null;
        long lines = 0;
        while ((line = file.readLine()) != null) {
            lines++;
            if (lines % 10000 == 0) {
                System.out.println("Progressing");
            }
            for (int i = 0; i < files.length; i++) {
                if (distributions[i].sample() == 1) {
                    files[i].write(String.format("%1$s\n", line));
                }
            }
        }
    }
}
