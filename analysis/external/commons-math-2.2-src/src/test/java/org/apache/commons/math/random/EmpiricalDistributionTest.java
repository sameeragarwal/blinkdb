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
package org.apache.commons.math.random;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.math.RetryTestCase;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Test cases for the EmpiricalDistribution class
 *
 * @version $Revision: 1003907 $ $Date: 2010-10-03 00:23:34 +0200 (dim. 03 oct. 2010) $
 */

public final class EmpiricalDistributionTest extends RetryTestCase {

    protected EmpiricalDistribution empiricalDistribution = null;
    protected EmpiricalDistribution empiricalDistribution2 = null;
    protected File file = null;
    protected URL url = null;
    protected double[] dataArray = null;

    public EmpiricalDistributionTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws IOException {
        empiricalDistribution = new EmpiricalDistributionImpl(100);
        url = getClass().getResource("testData.txt");

        empiricalDistribution2 = new EmpiricalDistributionImpl(100);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(
                        url.openStream()));
        String str = null;
        ArrayList<Double> list = new ArrayList<Double>();
        while ((str = in.readLine()) != null) {
            list.add(Double.valueOf(str));
        }
        in.close();
        in = null;

        dataArray = new double[list.size()];
        int i = 0;
        for (Double data : list) {
            dataArray[i] = data.doubleValue();
            i++;
        }
    }

    /**
     * Test EmpiricalDistrbution.load() using sample data file.<br>
     * Check that the sampleCount, mu and sigma match data in
     * the sample data file.
     */
    public void testLoad() throws Exception {
        empiricalDistribution.load(url);
        // testData File has 10000 values, with mean ~ 5.0, std dev ~ 1
        // Make sure that loaded distribution matches this
        assertEquals(empiricalDistribution.getSampleStats().getN(),1000,10E-7);
        //TODO: replace with statistical tests
        assertEquals
            (empiricalDistribution.getSampleStats().getMean(),
                5.069831575018909,10E-7);
        assertEquals
          (empiricalDistribution.getSampleStats().getStandardDeviation(),
                1.0173699343977738,10E-7);
    }

    /**
     * Test EmpiricalDistrbution.load(double[]) using data taken from
     * sample data file.<br>
     * Check that the sampleCount, mu and sigma match data in
     * the sample data file.
     */
    public void testDoubleLoad() throws Exception {
        empiricalDistribution2.load(dataArray);
        // testData File has 10000 values, with mean ~ 5.0, std dev ~ 1
        // Make sure that loaded distribution matches this
        assertEquals(empiricalDistribution2.getSampleStats().getN(),1000,10E-7);
        //TODO: replace with statistical tests
        assertEquals
            (empiricalDistribution2.getSampleStats().getMean(),
                5.069831575018909,10E-7);
        assertEquals
          (empiricalDistribution2.getSampleStats().getStandardDeviation(),
                1.0173699343977738,10E-7);

        double[] bounds = ((EmpiricalDistributionImpl) empiricalDistribution2).getGeneratorUpperBounds();
        assertEquals(bounds.length, 100);
        assertEquals(bounds[99], 1.0, 10e-12);

    }

    /**
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    public void testNext() throws Exception {
        tstGen(0.1);
        tstDoubleGen(0.1);
    }

    /**
      * Make sure exception thrown if digest getNext is attempted
      * before loading empiricalDistribution.
     */
    public void testNexFail() {
        try {
            empiricalDistribution.getNextValue();
            empiricalDistribution2.getNextValue();
            fail("Expecting IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    /**
     * Make sure we can handle a grid size that is too fine
     */
    public void testGridTooFine() throws Exception {
        empiricalDistribution = new EmpiricalDistributionImpl(1001);
        tstGen(0.1);
        empiricalDistribution2 = new EmpiricalDistributionImpl(1001);
        tstDoubleGen(0.1);
    }

    /**
     * How about too fat?
     */
    public void testGridTooFat() throws Exception {
        empiricalDistribution = new EmpiricalDistributionImpl(1);
        tstGen(5); // ridiculous tolerance; but ridiculous grid size
                   // really just checking to make sure we do not bomb
        empiricalDistribution2 = new EmpiricalDistributionImpl(1);
        tstDoubleGen(5);
    }

    /**
     * Test bin index overflow problem (BZ 36450)
     */
    public void testBinIndexOverflow() throws Exception {
        double[] x = new double[] {9474.94326071674, 2080107.8865462579};
        new EmpiricalDistributionImpl().load(x);
    }

    public void testSerialization() {
        // Empty
        EmpiricalDistribution dist = new EmpiricalDistributionImpl();
        EmpiricalDistribution dist2 = (EmpiricalDistribution) TestUtils.serializeAndRecover(dist);
        verifySame(dist, dist2);

        // Loaded
        empiricalDistribution2.load(dataArray);
        dist2 = (EmpiricalDistribution) TestUtils.serializeAndRecover(empiricalDistribution2);
        verifySame(empiricalDistribution2, dist2);
    }

    public void testLoadNullDoubleArray() {
        EmpiricalDistribution dist = new EmpiricalDistributionImpl();
        try {
            dist.load((double[]) null);
            fail("load((double[]) null) expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testLoadNullURL() throws Exception {
        EmpiricalDistribution dist = new EmpiricalDistributionImpl();
        try {
            dist.load((URL) null);
            fail("load((URL) null) expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testLoadNullFile() throws Exception {
        EmpiricalDistribution dist = new EmpiricalDistributionImpl();
        try {
            dist.load((File) null);
            fail("load((File) null) expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * MATH-298
     */
    public void testGetBinUpperBounds() {
        double[] testData = {0, 1, 1, 2, 3, 4, 4, 5, 6, 7, 8, 9, 10};
        EmpiricalDistributionImpl dist = new EmpiricalDistributionImpl(5);
        dist.load(testData);
        double[] expectedBinUpperBounds = {2, 4, 6, 8, 10};
        double[] expectedGeneratorUpperBounds = {4d/13d, 7d/13d, 9d/13d, 11d/13d, 1};
        double tol = 10E-12;
        TestUtils.assertEquals(expectedBinUpperBounds, dist.getUpperBounds(), tol);
        TestUtils.assertEquals(expectedGeneratorUpperBounds, dist.getGeneratorUpperBounds(), tol);
    }

    private void verifySame(EmpiricalDistribution d1, EmpiricalDistribution d2) {
        assertEquals(d1.isLoaded(), d2.isLoaded());
        assertEquals(d1.getBinCount(), d2.getBinCount());
        assertEquals(d1.getSampleStats(), d2.getSampleStats());
        if (d1.isLoaded()) {
            for (int i = 0;  i < d1.getUpperBounds().length; i++) {
                assertEquals(d1.getUpperBounds()[i], d2.getUpperBounds()[i], 0);
            }
            assertEquals(d1.getBinStats(), d2.getBinStats());
        }
    }

    private void tstGen(double tolerance)throws Exception {
        empiricalDistribution.load(url);
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            stats.addValue(empiricalDistribution.getNextValue());
        }
        assertEquals("mean", stats.getMean(),5.069831575018909,tolerance);
        assertEquals
         ("std dev", stats.getStandardDeviation(),1.0173699343977738,tolerance);
    }

    private void tstDoubleGen(double tolerance)throws Exception {
        empiricalDistribution2.load(dataArray);
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 1; i < 1000; i++) {
            stats.addValue(empiricalDistribution2.getNextValue());
        }
        assertEquals("mean", stats.getMean(),5.069831575018909,tolerance);
        assertEquals
         ("std dev", stats.getStandardDeviation(),1.0173699343977738,tolerance);
    }
}
