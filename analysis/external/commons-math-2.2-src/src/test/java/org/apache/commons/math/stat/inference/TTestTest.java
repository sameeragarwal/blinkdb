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
package org.apache.commons.math.stat.inference;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
/**
 * Test cases for the TTestImpl class.
 *
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */

public class TTestTest extends TestCase {

    protected TTest testStatistic = new TTestImpl();

    private double[] tooShortObs = { 1.0 };
    private double[] emptyObs = {};
    private SummaryStatistics emptyStats = new SummaryStatistics();
   SummaryStatistics tooShortStats = null;

    public TTestTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        tooShortStats = new SummaryStatistics();
        tooShortStats.addValue(0d);
    }

    public void testOneSampleT() throws Exception {
        double[] observed =
            {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0, 94.0, 101.0,  88.0, 98.0, 94.0, 101.0, 92.0, 95.0 };
        double mu = 100.0;
        SummaryStatistics sampleStats = null;
        sampleStats = new SummaryStatistics();
        for (int i = 0; i < observed.length; i++) {
            sampleStats.addValue(observed[i]);
        }

        // Target comparison values computed using R version 1.8.1 (Linux version)
        assertEquals("t statistic",  -2.81976445346,
                testStatistic.t(mu, observed), 10E-10);
        assertEquals("t statistic",  -2.81976445346,
                testStatistic.t(mu, sampleStats), 10E-10);
        assertEquals("p value", 0.0136390585873,
                testStatistic.tTest(mu, observed), 10E-10);
        assertEquals("p value", 0.0136390585873,
                testStatistic.tTest(mu, sampleStats), 10E-10);

        try {
            testStatistic.t(mu, (double[]) null);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(mu, (SummaryStatistics) null);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(mu, emptyObs);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(mu, emptyStats);
            fail("arguments too short, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(mu, tooShortObs);
            fail("insufficient data to compute t statistic, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.tTest(mu, tooShortObs);
            fail("insufficient data to perform t test, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }

        try {
            testStatistic.t(mu, tooShortStats);
            fail("insufficient data to compute t statistic, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            testStatistic.tTest(mu, tooShortStats);
            fail("insufficient data to perform t test, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testOneSampleTTest() throws Exception {
        double[] oneSidedP =
            {2d, 0d, 6d, 6d, 3d, 3d, 2d, 3d, -6d, 6d, 6d, 6d, 3d, 0d, 1d, 1d, 0d, 2d, 3d, 3d };
        SummaryStatistics oneSidedPStats = new SummaryStatistics();
        for (int i = 0; i < oneSidedP.length; i++) {
            oneSidedPStats.addValue(oneSidedP[i]);
        }
        // Target comparison values computed using R version 1.8.1 (Linux version)
        assertEquals("one sample t stat", 3.86485535541,
                testStatistic.t(0d, oneSidedP), 10E-10);
        assertEquals("one sample t stat", 3.86485535541,
                testStatistic.t(0d, oneSidedPStats),1E-10);
        assertEquals("one sample p value", 0.000521637019637,
                testStatistic.tTest(0d, oneSidedP) / 2d, 10E-10);
        assertEquals("one sample p value", 0.000521637019637,
                testStatistic.tTest(0d, oneSidedPStats) / 2d, 10E-5);
        assertTrue("one sample t-test reject", testStatistic.tTest(0d, oneSidedP, 0.01));
        assertTrue("one sample t-test reject", testStatistic.tTest(0d, oneSidedPStats, 0.01));
        assertTrue("one sample t-test accept", !testStatistic.tTest(0d, oneSidedP, 0.0001));
        assertTrue("one sample t-test accept", !testStatistic.tTest(0d, oneSidedPStats, 0.0001));

        try {
            testStatistic.tTest(0d, oneSidedP, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.tTest(0d, oneSidedPStats, 95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

    }

    public void testTwoSampleTHeterscedastic() throws Exception {
        double[] sample1 = { 7d, -4d, 18d, 17d, -3d, -5d, 1d, 10d, 11d, -2d };
        double[] sample2 = { -1d, 12d, -1d, -3d, 3d, -5d, 5d, 2d, -11d, -1d, -3d };
        SummaryStatistics sampleStats1 = new SummaryStatistics();
        for (int i = 0; i < sample1.length; i++) {
            sampleStats1.addValue(sample1[i]);
        }
        SummaryStatistics sampleStats2 = new SummaryStatistics();
        for (int i = 0; i < sample2.length; i++) {
            sampleStats2.addValue(sample2[i]);
        }

        // Target comparison values computed using R version 1.8.1 (Linux version)
        assertEquals("two sample heteroscedastic t stat", 1.60371728768,
                testStatistic.t(sample1, sample2), 1E-10);
        assertEquals("two sample heteroscedastic t stat", 1.60371728768,
                testStatistic.t(sampleStats1, sampleStats2), 1E-10);
        assertEquals("two sample heteroscedastic p value", 0.128839369622,
                testStatistic.tTest(sample1, sample2), 1E-10);
        assertEquals("two sample heteroscedastic p value", 0.128839369622,
                testStatistic.tTest(sampleStats1, sampleStats2), 1E-10);
        assertTrue("two sample heteroscedastic t-test reject",
                testStatistic.tTest(sample1, sample2, 0.2));
        assertTrue("two sample heteroscedastic t-test reject",
                testStatistic.tTest(sampleStats1, sampleStats2, 0.2));
        assertTrue("two sample heteroscedastic t-test accept",
                !testStatistic.tTest(sample1, sample2, 0.1));
        assertTrue("two sample heteroscedastic t-test accept",
                !testStatistic.tTest(sampleStats1, sampleStats2, 0.1));

        try {
            testStatistic.tTest(sample1, sample2, .95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.tTest(sampleStats1, sampleStats2, .95);
            fail("alpha out of range, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.tTest(sample1, tooShortObs, .01);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.tTest(sampleStats1, tooShortStats, .01);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.tTest(sample1, tooShortObs);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }

        try {
            testStatistic.tTest(sampleStats1, tooShortStats);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(sample1, tooShortObs);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            testStatistic.t(sampleStats1, tooShortStats);
            fail("insufficient data, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
           // expected
        }
    }
    public void testTwoSampleTHomoscedastic() throws Exception {
        double[] sample1 ={2, 4, 6, 8, 10, 97};
        double[] sample2 = {4, 6, 8, 10, 16};
        SummaryStatistics sampleStats1 = new SummaryStatistics();
        for (int i = 0; i < sample1.length; i++) {
            sampleStats1.addValue(sample1[i]);
        }
        SummaryStatistics sampleStats2 = new SummaryStatistics();
        for (int i = 0; i < sample2.length; i++) {
            sampleStats2.addValue(sample2[i]);
        }

        // Target comparison values computed using R version 1.8.1 (Linux version)
        assertEquals("two sample homoscedastic t stat", 0.73096310086,
              testStatistic.homoscedasticT(sample1, sample2), 10E-11);
        assertEquals("two sample homoscedastic p value", 0.4833963785,
                testStatistic.homoscedasticTTest(sampleStats1, sampleStats2), 1E-10);
        assertTrue("two sample homoscedastic t-test reject",
                testStatistic.homoscedasticTTest(sample1, sample2, 0.49));
        assertTrue("two sample homoscedastic t-test accept",
                !testStatistic.homoscedasticTTest(sample1, sample2, 0.48));
    }

    public void testSmallSamples() throws Exception {
        double[] sample1 = {1d, 3d};
        double[] sample2 = {4d, 5d};

        // Target values computed using R, version 1.8.1 (linux version)
        assertEquals(-2.2360679775, testStatistic.t(sample1, sample2),
                1E-10);
        assertEquals(0.198727388935, testStatistic.tTest(sample1, sample2),
                1E-10);
    }

    public void testPaired() throws Exception {
        double[] sample1 = {1d, 3d, 5d, 7d};
        double[] sample2 = {0d, 6d, 11d, 2d};
        double[] sample3 = {5d, 7d, 8d, 10d};

        // Target values computed using R, version 1.8.1 (linux version)
        assertEquals(-0.3133, testStatistic.pairedT(sample1, sample2), 1E-4);
        assertEquals(0.774544295819, testStatistic.pairedTTest(sample1, sample2), 1E-10);
        assertEquals(0.001208, testStatistic.pairedTTest(sample1, sample3), 1E-6);
        assertFalse(testStatistic.pairedTTest(sample1, sample3, .001));
        assertTrue(testStatistic.pairedTTest(sample1, sample3, .002));
    }
}
