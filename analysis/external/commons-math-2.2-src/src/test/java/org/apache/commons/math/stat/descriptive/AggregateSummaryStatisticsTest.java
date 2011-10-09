/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.stat.descriptive;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;


/**
 * Test cases for {@link AggregateSummaryStatistics}
 *
 */
public class AggregateSummaryStatisticsTest extends TestCase {

    /**
     * Tests the standard aggregation behavior
     */
    public void testAggregation() {
        AggregateSummaryStatistics aggregate = new AggregateSummaryStatistics();
        SummaryStatistics setOneStats = aggregate.createContributingStatistics();
        SummaryStatistics setTwoStats = aggregate.createContributingStatistics();

        assertNotNull("The set one contributing stats are null", setOneStats);
        assertNotNull("The set two contributing stats are null", setTwoStats);
        assertNotSame("Contributing stats objects are the same", setOneStats, setTwoStats);

        setOneStats.addValue(2);
        setOneStats.addValue(3);
        setOneStats.addValue(5);
        setOneStats.addValue(7);
        setOneStats.addValue(11);
        assertEquals("Wrong number of set one values", 5, setOneStats.getN());
        assertEquals("Wrong sum of set one values", 28.0, setOneStats.getSum());

        setTwoStats.addValue(2);
        setTwoStats.addValue(4);
        setTwoStats.addValue(8);
        assertEquals("Wrong number of set two values", 3, setTwoStats.getN());
        assertEquals("Wrong sum of set two values", 14.0, setTwoStats.getSum());

        assertEquals("Wrong number of aggregate values", 8, aggregate.getN());
        assertEquals("Wrong aggregate sum", 42.0, aggregate.getSum());
    }

    /**
     * Verify that aggregating over a partition gives the same results
     * as direct computation.
     *
     *  1) Randomly generate a dataset of 10-100 values
     *     from [-100, 100]
     *  2) Divide the dataset it into 2-5 partitions
     *  3) Create an AggregateSummaryStatistic and ContributingStatistics
     *     for each partition
     *  4) Compare results from the AggregateSummaryStatistic with values
     *     returned by a single SummaryStatistics instance that is provided
     *     the full dataset
     */
    public void testAggregationConsistency() throws Exception {

        // Generate a random sample and random partition
        double[] totalSample = generateSample();
        double[][] subSamples = generatePartition(totalSample);
        int nSamples = subSamples.length;

        // Create aggregator and total stats for comparison
        AggregateSummaryStatistics aggregate = new AggregateSummaryStatistics();
        SummaryStatistics totalStats = new SummaryStatistics();

        // Create array of component stats
        SummaryStatistics componentStats[] = new SummaryStatistics[nSamples];

        for (int i = 0; i < nSamples; i++) {

            // Make componentStats[i] a contributing statistic to aggregate
            componentStats[i] = aggregate.createContributingStatistics();

            // Add values from subsample
            for (int j = 0; j < subSamples[i].length; j++) {
                componentStats[i].addValue(subSamples[i][j]);
            }
        }

        // Compute totalStats directly
        for (int i = 0; i < totalSample.length; i++) {
            totalStats.addValue(totalSample[i]);
        }

        /*
         * Compare statistics in totalStats with aggregate.
         * Note that guaranteed success of this comparison depends on the
         * fact that <aggregate> gets values in exactly the same order
         * as <totalStats>.
         *
         */
        assertEquals(totalStats.getSummary(), aggregate.getSummary());

    }

    /**
     * Test aggregate function by randomly generating a dataset of 10-100 values
     * from [-100, 100], dividing it into 2-5 partitions, computing stats for each
     * partition and comparing the result of aggregate(...) applied to the collection
     * of per-partition SummaryStatistics with a single SummaryStatistics computed
     * over the full sample.
     *
     * @throws Exception
     */
    public void testAggregate() throws Exception {

        // Generate a random sample and random partition
        double[] totalSample = generateSample();
        double[][] subSamples = generatePartition(totalSample);
        int nSamples = subSamples.length;

        // Compute combined stats directly
        SummaryStatistics totalStats = new SummaryStatistics();
        for (int i = 0; i < totalSample.length; i++) {
            totalStats.addValue(totalSample[i]);
        }

        // Now compute subsample stats individually and aggregate
        SummaryStatistics[] subSampleStats = new SummaryStatistics[nSamples];
        for (int i = 0; i < nSamples; i++) {
            subSampleStats[i] = new SummaryStatistics();
        }
        Collection<SummaryStatistics> aggregate = new ArrayList<SummaryStatistics>();
        for (int i = 0; i < nSamples; i++) {
            for (int j = 0; j < subSamples[i].length; j++) {
                subSampleStats[i].addValue(subSamples[i][j]);
            }
            aggregate.add(subSampleStats[i]);
        }

        // Compare values
        StatisticalSummary aggregatedStats = AggregateSummaryStatistics.aggregate(aggregate);
        assertEquals(totalStats.getSummary(), aggregatedStats, 10E-12);
    }


    public void testAggregateDegenerate() throws Exception {
        double[] totalSample = {1, 2, 3, 4, 5};
        double[][] subSamples = {{1}, {2}, {3}, {4}, {5}};

        // Compute combined stats directly
        SummaryStatistics totalStats = new SummaryStatistics();
        for (int i = 0; i < totalSample.length; i++) {
            totalStats.addValue(totalSample[i]);
        }

        // Now compute subsample stats individually and aggregate
        SummaryStatistics[] subSampleStats = new SummaryStatistics[5];
        for (int i = 0; i < 5; i++) {
            subSampleStats[i] = new SummaryStatistics();
        }
        Collection<SummaryStatistics> aggregate = new ArrayList<SummaryStatistics>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < subSamples[i].length; j++) {
                subSampleStats[i].addValue(subSamples[i][j]);
            }
            aggregate.add(subSampleStats[i]);
        }

        // Compare values
        StatisticalSummaryValues aggregatedStats = AggregateSummaryStatistics.aggregate(aggregate);
        assertEquals(totalStats.getSummary(), aggregatedStats, 10E-12);
    }

    public void testAggregateSpecialValues() throws Exception {
        double[] totalSample = {Double.POSITIVE_INFINITY, 2, 3, Double.NaN, 5};
        double[][] subSamples = {{Double.POSITIVE_INFINITY, 2}, {3}, {Double.NaN}, {5}};

        // Compute combined stats directly
        SummaryStatistics totalStats = new SummaryStatistics();
        for (int i = 0; i < totalSample.length; i++) {
            totalStats.addValue(totalSample[i]);
        }

        // Now compute subsample stats individually and aggregate
        SummaryStatistics[] subSampleStats = new SummaryStatistics[5];
        for (int i = 0; i < 4; i++) {
            subSampleStats[i] = new SummaryStatistics();
        }
        Collection<SummaryStatistics> aggregate = new ArrayList<SummaryStatistics>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < subSamples[i].length; j++) {
                subSampleStats[i].addValue(subSamples[i][j]);
            }
            aggregate.add(subSampleStats[i]);
        }

        // Compare values
        StatisticalSummaryValues aggregatedStats = AggregateSummaryStatistics.aggregate(aggregate);
        assertEquals(totalStats.getSummary(), aggregatedStats, 10E-12);

    }

    /**
     * Verifies that a StatisticalSummary and a StatisticalSummaryValues are equal up
     * to delta, with NaNs, infinities returned in the same spots. For max, min, n, values
     * have to agree exactly, delta is used only for sum, mean, variance, std dev.
     */
    protected static void assertEquals(StatisticalSummary expected, StatisticalSummary observed, double delta) {
        TestUtils.assertEquals(expected.getMax(), observed.getMax(), 0);
        TestUtils.assertEquals(expected.getMin(), observed.getMin(), 0);
        assertEquals(expected.getN(), observed.getN());
        TestUtils.assertEquals(expected.getSum(), observed.getSum(), delta);
        TestUtils.assertEquals(expected.getMean(), observed.getMean(), delta);
        TestUtils.assertEquals(expected.getStandardDeviation(), observed.getStandardDeviation(), delta);
        TestUtils.assertEquals(expected.getVariance(), observed.getVariance(), delta);
    }


    /**
     * Generates a random sample of double values.
     * Sample size is random, between 10 and 100 and values are
     * uniformly distributed over [-100, 100].
     *
     * @return array of random double values
     */
    private double[] generateSample() {
        final RandomData randomData = new RandomDataImpl();
        final int sampleSize = randomData.nextInt(10,100);
        double[] out = new double[sampleSize];
        for (int i = 0; i < out.length; i++) {
            out[i] = randomData.nextUniform(-100, 100);
        }
        return out;
    }

    /**
     * Generates a partition of <sample> into up to 5 sequentially selected
     * subsamples with randomly selected partition points.
     *
     * @param sample array to partition
     * @return rectangular array with rows = subsamples
     */
    private double[][] generatePartition(double[] sample) {
        final int length = sample.length;
        final double[][] out = new double[5][];
        final RandomData randomData = new RandomDataImpl();
        int cur = 0;
        int offset = 0;
        int sampleCount = 0;
        for (int i = 0; i < 5; i++) {
            if (cur == length || offset == length) {
                break;
            }
            final int next = (i == 4 || cur == length - 1) ? length - 1 : randomData.nextInt(cur, length - 1);
            final int subLength = next - cur + 1;
            out[i] = new double[subLength];
            System.arraycopy(sample, offset, out[i], 0, subLength);
            cur = next + 1;
            sampleCount++;
            offset += subLength;
        }
        if (sampleCount < 5) {
            double[][] out2 = new double[sampleCount][];
            for (int j = 0; j < sampleCount; j++) {
                final int curSize = out[j].length;
                out2[j] = new double[curSize];
                System.arraycopy(out[j], 0, out2[j], 0, curSize);
            }
            return out2;
        } else {
            return out;
        }
    }

}
