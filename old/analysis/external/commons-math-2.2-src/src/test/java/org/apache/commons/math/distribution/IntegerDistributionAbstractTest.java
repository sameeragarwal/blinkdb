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
package org.apache.commons.math.distribution;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * Abstract base class for {@link IntegerDistribution} tests.
 * <p>
 * To create a concrete test class for an integer distribution implementation,
 *  implement makeDistribution() to return a distribution instance to use in
 *  tests and each of the test data generation methods below.  In each case, the
 *  test points and test values arrays returned represent parallel arrays of
 *  inputs and expected values for the distribution returned by makeDistribution().
 *  <p>
 *  makeDensityTestPoints() -- arguments used to test probability density calculation
 *  makeDensityTestValues() -- expected probability densities
 *  makeCumulativeTestPoints() -- arguments used to test cumulative probabilities
 *  makeCumulativeTestValues() -- expected cumulative probabilites
 *  makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf evaluation
 *  makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 *  To implement additional test cases with different distribution instances and test data,
 *  use the setXxx methods for the instance data in test cases and call the verifyXxx methods
 *  to verify results.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public abstract class IntegerDistributionAbstractTest extends TestCase {

//-------------------- Private test instance data -------------------------
    /** Discrete distribution instance used to perform tests */
    private IntegerDistribution distribution;

    /** Tolerance used in comparing expected and returned values */
    private double tolerance = 1E-4;

    /** Arguments used to test probability density calculations */
    private int[] densityTestPoints;

    /** Values used to test probability density calculations */
    private double[] densityTestValues;

    /** Arguments used to test cumulative probability density calculations */
    private int[] cumulativeTestPoints;

    /** Values used to test cumulative probability density calculations */
    private double[] cumulativeTestValues;

    /** Arguments used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestPoints;

    /** Values used to test inverse cumulative probability density calculations */
    private int[] inverseCumulativeTestValues;

    //-------------------------------------------------------------------------

    /**
     * Constructor for IntegerDistributionAbstractTest.
     * @param name
     */
    public IntegerDistributionAbstractTest(String name) {
        super(name);
    }

    //-------------------- Abstract methods -----------------------------------

    /** Creates the default discrete distribution instance to use in tests. */
    public abstract IntegerDistribution makeDistribution();

    /** Creates the default probability density test input values */
    public abstract int[] makeDensityTestPoints();

    /** Creates the default probability density test expected values */
    public abstract double[] makeDensityTestValues();

    /** Creates the default cumulative probability density test input values */
    public abstract int[] makeCumulativeTestPoints();

    /** Creates the default cumulative probability density test expected values */
    public abstract double[] makeCumulativeTestValues();

    /** Creates the default inverse cumulative probability test input values */
    public abstract double[] makeInverseCumulativeTestPoints();

    /** Creates the default inverse cumulative probability density test expected values */
    public abstract int[] makeInverseCumulativeTestValues();

    //-------------------- Setup / tear down ----------------------------------

    /**
     * Setup sets all test instance data to default values
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        distribution = makeDistribution();
        densityTestPoints = makeDensityTestPoints();
        densityTestValues = makeDensityTestValues();
        cumulativeTestPoints = makeCumulativeTestPoints();
        cumulativeTestValues = makeCumulativeTestValues();
        inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
        inverseCumulativeTestValues = makeInverseCumulativeTestValues();
    }

    /**
     * Cleans up test instance data
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        distribution = null;
        densityTestPoints = null;
        densityTestValues = null;
        cumulativeTestPoints = null;
        cumulativeTestValues = null;
        inverseCumulativeTestPoints = null;
        inverseCumulativeTestValues = null;
    }

    //-------------------- Verification methods -------------------------------

    /**
     * Verifies that probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyDensities() throws Exception {
        for (int i = 0; i < densityTestPoints.length; i++) {
            assertEquals("Incorrect density value returned for " + densityTestPoints[i],
                    densityTestValues[i],
                    distribution.probability(densityTestPoints[i]), tolerance);
        }
    }

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyCumulativeProbabilities() throws Exception {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            assertEquals("Incorrect cumulative probability value returned for " + cumulativeTestPoints[i],
                    cumulativeTestValues[i],
                    distribution.cumulativeProbability(cumulativeTestPoints[i]), tolerance);
        }
    }


    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyInverseCumulativeProbabilities() throws Exception {
        for (int i = 0; i < inverseCumulativeTestPoints.length; i++) {
            assertEquals("Incorrect inverse cumulative probability value returned for "
                    + inverseCumulativeTestPoints[i], inverseCumulativeTestValues[i],
                    distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]));
        }
    }

    //------------------------ Default test cases -----------------------------

    /**
     * Verifies that probability density calculations match expected values
     * using default test instance data
     */
    public void testDensities() throws Exception {
        verifyDensities();
    }

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using default test instance data
     */
    public void testCumulativeProbabilities() throws Exception {
        verifyCumulativeProbabilities();
    }

    /**
     * Verifies that floating point arguments are correctly handled by
     * cumulativeProbablility(-,-)
     * JIRA: MATH-184
     */
    public void testFloatingPointArguments() throws Exception {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            double arg = cumulativeTestPoints[i];
            assertEquals(
                    "Incorrect cumulative probability value returned for " +
                    cumulativeTestPoints[i],
                    cumulativeTestValues[i],
                    distribution.cumulativeProbability(arg), tolerance);
            if (i < cumulativeTestPoints.length - 1) {
                double arg2 = cumulativeTestPoints[i + 1];
                assertEquals("Inconsistent probability for discrete range " +
                        "[ " + arg + "," + arg2 + " ]",
                   distribution.cumulativeProbability(
                           cumulativeTestPoints[i],
                           cumulativeTestPoints[i + 1]),
                   distribution.cumulativeProbability(arg, arg2), tolerance);
                arg = arg - FastMath.random();
                arg2 = arg2 + FastMath.random();
                assertEquals("Inconsistent probability for discrete range " +
                        "[ " + arg + "," + arg2 + " ]",
                   distribution.cumulativeProbability(
                           cumulativeTestPoints[i],
                           cumulativeTestPoints[i + 1]),
                   distribution.cumulativeProbability(arg, arg2), tolerance);
            }
        }
        int one = 1;
        int ten = 10;
        int two = 2;
        double oned = one;
        double twod = two;
        double tend = ten;
        assertEquals(distribution.cumulativeProbability(one, two),
                distribution.cumulativeProbability(oned, twod), tolerance);
        assertEquals(distribution.cumulativeProbability(one, two),
                distribution.cumulativeProbability(oned - tolerance,
                        twod + 0.9), tolerance);
        assertEquals(distribution.cumulativeProbability(two, ten),
                distribution.cumulativeProbability(twod, tend), tolerance);
        assertEquals(distribution.cumulativeProbability(two, ten),
                distribution.cumulativeProbability(twod - tolerance,
                        tend + 0.9), tolerance);
    }

    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using default test instance data
     */
    public void testInverseCumulativeProbabilities() throws Exception {
        verifyInverseCumulativeProbabilities();
    }

    /**
     * Verifies that illegal arguments are correctly handled
     */
    public void testIllegalArguments() throws Exception {
        try {
            distribution.cumulativeProbability(1, 0);
            fail("Expecting IllegalArgumentException for bad cumulativeProbability interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.inverseCumulativeProbability(-1);
            fail("Expecting IllegalArgumentException for p = -1");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.inverseCumulativeProbability(2);
            fail("Expecting IllegalArgumentException for p = 2");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    /**
     * Test sampling
     */
    public void testSampling() throws Exception {
        int[] densityPoints = makeDensityTestPoints();
        double[] densityValues = makeDensityTestValues();
        int sampleSize = 1000;
        int length = TestUtils.eliminateZeroMassPoints(densityPoints, densityValues);
        AbstractIntegerDistribution distribution = (AbstractIntegerDistribution) makeDistribution();
        double[] expectedCounts = new double[length];
        long[] observedCounts = new long[length];
        for (int i = 0; i < length; i++) {
            expectedCounts[i] = sampleSize * densityValues[i];
        }
        distribution.reseedRandomGenerator(1000); // Use fixed seed
        int[] sample = distribution.sample(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
          for (int j = 0; j < length; j++) {
              if (sample[i] == densityPoints[j]) {
                  observedCounts[j]++;
              }
          }
        }
        TestUtils.assertChiSquareAccept(densityPoints, expectedCounts, observedCounts, .001);
    }

    //------------------ Getters / Setters for test instance data -----------
    /**
     * @return Returns the cumulativeTestPoints.
     */
    protected int[] getCumulativeTestPoints() {
        return cumulativeTestPoints;
    }

    /**
     * @param cumulativeTestPoints The cumulativeTestPoints to set.
     */
    protected void setCumulativeTestPoints(int[] cumulativeTestPoints) {
        this.cumulativeTestPoints = cumulativeTestPoints;
    }

    /**
     * @return Returns the cumulativeTestValues.
     */
    protected double[] getCumulativeTestValues() {
        return cumulativeTestValues;
    }

    /**
     * @param cumulativeTestValues The cumulativeTestValues to set.
     */
    protected void setCumulativeTestValues(double[] cumulativeTestValues) {
        this.cumulativeTestValues = cumulativeTestValues;
    }

    /**
     * @return Returns the densityTestPoints.
     */
    protected int[] getDensityTestPoints() {
        return densityTestPoints;
    }

    /**
     * @param densityTestPoints The densityTestPoints to set.
     */
    protected void setDensityTestPoints(int[] densityTestPoints) {
        this.densityTestPoints = densityTestPoints;
    }

    /**
     * @return Returns the densityTestValues.
     */
    protected double[] getDensityTestValues() {
        return densityTestValues;
    }

    /**
     * @param densityTestValues The densityTestValues to set.
     */
    protected void setDensityTestValues(double[] densityTestValues) {
        this.densityTestValues = densityTestValues;
    }

    /**
     * @return Returns the distribution.
     */
    protected IntegerDistribution getDistribution() {
        return distribution;
    }

    /**
     * @param distribution The distribution to set.
     */
    protected void setDistribution(IntegerDistribution distribution) {
        this.distribution = distribution;
    }

    /**
     * @return Returns the inverseCumulativeTestPoints.
     */
    protected double[] getInverseCumulativeTestPoints() {
        return inverseCumulativeTestPoints;
    }

    /**
     * @param inverseCumulativeTestPoints The inverseCumulativeTestPoints to set.
     */
    protected void setInverseCumulativeTestPoints(double[] inverseCumulativeTestPoints) {
        this.inverseCumulativeTestPoints = inverseCumulativeTestPoints;
    }

    /**
     * @return Returns the inverseCumulativeTestValues.
     */
    protected int[] getInverseCumulativeTestValues() {
        return inverseCumulativeTestValues;
    }

    /**
     * @param inverseCumulativeTestValues The inverseCumulativeTestValues to set.
     */
    protected void setInverseCumulativeTestValues(int[] inverseCumulativeTestValues) {
        this.inverseCumulativeTestValues = inverseCumulativeTestValues;
    }

    /**
     * @return Returns the tolerance.
     */
    protected double getTolerance() {
        return tolerance;
    }

    /**
     * @param tolerance The tolerance to set.
     */
    protected void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

}
