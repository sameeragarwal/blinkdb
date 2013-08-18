/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.stat.descriptive;

import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.apache.commons.math.util.MathUtils;

/**
 * Test cases for the DescriptiveStatistics class.
 *
 * @version $Revision: 1003907 $ $Date: 2007-08-16 15:36:33 -0500 (Thu, 16 Aug
 *          2007) $
 */
public class DescriptiveStatisticsTest extends TestCase {

    public DescriptiveStatisticsTest(String name) {
        super(name);
    }

    protected DescriptiveStatistics createDescriptiveStatistics() {
        return new DescriptiveStatistics();
    }

    public void testSetterInjection() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(3);
        assertEquals(2, stats.getMean(), 1E-10);
        // Now lets try some new math
        stats.setMeanImpl(new deepMean());
        assertEquals(42, stats.getMean(), 1E-10);
    }

    public void testCopy() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(3);
        DescriptiveStatistics copy = new DescriptiveStatistics(stats);
        assertEquals(2, copy.getMean(), 1E-10);
        // Now lets try some new math
        stats.setMeanImpl(new deepMean());
        copy = stats.copy();
        assertEquals(42, copy.getMean(), 1E-10);
    }

    public void testWindowSize() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.setWindowSize(300);
        for (int i = 0; i < 100; ++i) {
            stats.addValue(i + 1);
        }
        int refSum = (100 * 101) / 2;
        assertEquals(refSum / 100.0, stats.getMean(), 1E-10);
        assertEquals(300, stats.getWindowSize());
        try {
            stats.setWindowSize(-3);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        assertEquals(300, stats.getWindowSize());
        stats.setWindowSize(50);
        assertEquals(50, stats.getWindowSize());
        int refSum2 = refSum - (50 * 51) / 2;
        assertEquals(refSum2 / 50.0, stats.getMean(), 1E-10);
    }

    public void testGetValues() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        for (int i = 100; i > 0; --i) {
            stats.addValue(i);
        }
        int refSum = (100 * 101) / 2;
        assertEquals(refSum / 100.0, stats.getMean(), 1E-10);
        double[] v = stats.getValues();
        for (int i = 0; i < v.length; ++i) {
            assertEquals(100.0 - i, v[i], 1.0e-10);
        }
        double[] s = stats.getSortedValues();
        for (int i = 0; i < s.length; ++i) {
            assertEquals(i + 1.0, s[i], 1.0e-10);
        }
        assertEquals(12.0, stats.getElement(88), 1.0e-10);
    }

    public void testToString() {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);
        Locale d = Locale.getDefault();
        Locale.setDefault(Locale.US);
        assertEquals("DescriptiveStatistics:\n" +
                     "n: 3\n" +
                     "min: 1.0\n" +
                     "max: 3.0\n" +
                     "mean: 2.0\n" +
                     "std dev: 1.0\n" +
                     "median: 2.0\n" +
                     "skewness: 0.0\n" +
                     "kurtosis: NaN\n",  stats.toString());
        Locale.setDefault(d);
    }

    public void testShuffledStatistics() {
        // the purpose of this test is only to check the get/set methods
        // we are aware shuffling statistics like this is really not
        // something sensible to do in production ...
        DescriptiveStatistics reference = createDescriptiveStatistics();
        DescriptiveStatistics shuffled  = createDescriptiveStatistics();

        UnivariateStatistic tmp = shuffled.getGeometricMeanImpl();
        shuffled.setGeometricMeanImpl(shuffled.getMeanImpl());
        shuffled.setMeanImpl(shuffled.getKurtosisImpl());
        shuffled.setKurtosisImpl(shuffled.getSkewnessImpl());
        shuffled.setSkewnessImpl(shuffled.getVarianceImpl());
        shuffled.setVarianceImpl(shuffled.getMaxImpl());
        shuffled.setMaxImpl(shuffled.getMinImpl());
        shuffled.setMinImpl(shuffled.getSumImpl());
        shuffled.setSumImpl(shuffled.getSumsqImpl());
        shuffled.setSumsqImpl(tmp);

        for (int i = 100; i > 0; --i) {
            reference.addValue(i);
            shuffled.addValue(i);
        }

        assertEquals(reference.getMean(),          shuffled.getGeometricMean(), 1.0e-10);
        assertEquals(reference.getKurtosis(),      shuffled.getMean(),          1.0e-10);
        assertEquals(reference.getSkewness(),      shuffled.getKurtosis(), 1.0e-10);
        assertEquals(reference.getVariance(),      shuffled.getSkewness(), 1.0e-10);
        assertEquals(reference.getMax(),           shuffled.getVariance(), 1.0e-10);
        assertEquals(reference.getMin(),           shuffled.getMax(), 1.0e-10);
        assertEquals(reference.getSum(),           shuffled.getMin(), 1.0e-10);
        assertEquals(reference.getSumsq(),         shuffled.getSum(), 1.0e-10);
        assertEquals(reference.getGeometricMean(), shuffled.getSumsq(), 1.0e-10);

    }

    public void testPercentileSetter() throws Exception {
        DescriptiveStatistics stats = createDescriptiveStatistics();
        stats.addValue(1);
        stats.addValue(2);
        stats.addValue(3);
        assertEquals(2, stats.getPercentile(50.0), 1E-10);

        // Inject wrapped Percentile impl
        stats.setPercentileImpl(new goodPercentile());
        assertEquals(2, stats.getPercentile(50.0), 1E-10);

        // Try "new math" impl
        stats.setPercentileImpl(new subPercentile());
        assertEquals(10.0, stats.getPercentile(10.0), 1E-10);

        // Try to set bad impl
        try {
            stats.setPercentileImpl(new badPercentile());
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void test20090720() {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(100);
        for (int i = 0; i < 161; i++) {
            descriptiveStatistics.addValue(1.2);
        }
        descriptiveStatistics.clear();
        descriptiveStatistics.addValue(1.2);
        assertEquals(1, descriptiveStatistics.getN());
    }

    public void testRemoval() {

        final DescriptiveStatistics dstat = createDescriptiveStatistics();

        checkremoval(dstat, 1, 6.0, 0.0, Double.NaN);
        checkremoval(dstat, 3, 5.0, 3.0, 4.5);
        checkremoval(dstat, 6, 3.5, 2.5, 3.0);
        checkremoval(dstat, 9, 3.5, 2.5, 3.0);
        checkremoval(dstat, DescriptiveStatistics.INFINITE_WINDOW, 3.5, 2.5, 3.0);

    }

    public void checkremoval(DescriptiveStatistics dstat, int wsize,
                             double mean1, double mean2, double mean3) {

        dstat.setWindowSize(wsize);
        dstat.clear();

        for (int i = 1 ; i <= 6 ; ++i) {
            dstat.addValue(i);
        }

        assertTrue(MathUtils.equalsIncludingNaN(mean1, dstat.getMean()));
        dstat.replaceMostRecentValue(0);
        assertTrue(MathUtils.equalsIncludingNaN(mean2, dstat.getMean()));
        dstat.removeMostRecentValue();
        assertTrue(MathUtils.equalsIncludingNaN(mean3, dstat.getMean()));

    }

    // Test UnivariateStatistics impls for setter injection tests

    /**
     * A new way to compute the mean
     */
    static class deepMean implements UnivariateStatistic {

        public double evaluate(double[] values, int begin, int length) {
            return 42;
        }

        public double evaluate(double[] values) {
            return 42;
        }
        public UnivariateStatistic copy() {
            return new deepMean();
        }
    }

    /**
     * Test percentile implementation - wraps a Percentile
     */
    static class goodPercentile implements UnivariateStatistic {
        private Percentile percentile = new Percentile();
        public void setQuantile(double quantile) {
            percentile.setQuantile(quantile);
        }
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }
        public UnivariateStatistic copy() {
            goodPercentile result = new goodPercentile();
            result.setQuantile(percentile.getQuantile());
            return result;
        }
    }

    /**
     * Test percentile subclass - another "new math" impl
     * Always returns currently set quantile
     */
    static class subPercentile extends Percentile {
        @Override
        public double evaluate(double[] values, int begin, int length) {
            return getQuantile();
        }
        @Override
        public double evaluate(double[] values) {
            return getQuantile();
        }
        private static final long serialVersionUID = 8040701391045914979L;
        @Override
        public Percentile copy() {
            subPercentile result = new subPercentile();
            return result;
        }
    }

    /**
     * "Bad" test percentile implementation - no setQuantile
     */
    static class badPercentile implements UnivariateStatistic {
        private Percentile percentile = new Percentile();
        public double evaluate(double[] values, int begin, int length) {
            return percentile.evaluate(values, begin, length);
        }
        public double evaluate(double[] values) {
            return percentile.evaluate(values);
        }
        public UnivariateStatistic copy() {
            return new badPercentile();
        }
    }

}
