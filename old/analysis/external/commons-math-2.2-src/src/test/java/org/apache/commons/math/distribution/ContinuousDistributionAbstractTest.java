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

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;

/**
 * Abstract base class for {@link ContinuousDistribution} tests.
 * <p>
 * To create a concrete test class for a continuous distribution
 * implementation, first implement makeDistribution() to return a distribution
 * instance to use in tests. Then implement each of the test data generation
 * methods below.  In each case, the test points and test values arrays
 * returned represent parallel arrays of inputs and expected values for the
 * distribution returned by makeDistribution().  Default implementations
 * are provided for the makeInverseXxx methods that just invert the mapping
 * defined by the arrays returned by the makeCumulativeXxx methods.
 * <p>
 * makeCumulativeTestPoints() -- arguments used to test cumulative probabilities
 * makeCumulativeTestValues() -- expected cumulative probabilites
 * makeDensityTestValues() -- expected density values at cumulativeTestPoints
 * makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf
 * makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 * To implement additional test cases with different distribution instances and
 * test data, use the setXxx methods for the instance data in test cases and
 * call the verifyXxx methods to verify results.
 * <p>
 * Error tolerance can be overriden by implementing getTolerance().
 * <p>
 * Test data should be validated against reference tables or other packages
 * where possible, and the source of the reference data and/or validation
 * should be documented in the test cases.  A framework for validating
 * distribution data against R is included in the /src/test/R source tree.
 * <p>
 * See {@link NormalDistributionTest} and {@link ChiSquareDistributionTest}
 * for examples.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public abstract class ContinuousDistributionAbstractTest extends TestCase {

//-------------------- Private test instance data -------------------------
    /**  Distribution instance used to perform tests */
    private ContinuousDistribution distribution;

    /** Tolerance used in comparing expected and returned values */
    private double tolerance = 1E-4;

    /** Arguments used to test cumulative probability density calculations */
    private double[] cumulativeTestPoints;

    /** Values used to test cumulative probability density calculations */
    private double[] cumulativeTestValues;

    /** Arguments used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestPoints;

    /** Values used to test inverse cumulative probability density calculations */
    private double[] inverseCumulativeTestValues;

    /** Values used to test density calculations */
    private double[] densityTestValues;

    //-------------------------------------------------------------------------

    /**
     * Constructor for ContinuousDistributionAbstractTest.
     * @param name
     */
    public ContinuousDistributionAbstractTest(String name) {
        super(name);
    }

    //-------------------- Abstract methods -----------------------------------

    /** Creates the default continuous distribution instance to use in tests. */
    public abstract ContinuousDistribution makeDistribution();

    /** Creates the default cumulative probability test input values */
    public abstract double[] makeCumulativeTestPoints();

    /** Creates the default cumulative probability test expected values */
    public abstract double[] makeCumulativeTestValues();

    /** Creates the default density test expected values */
    public abstract double[] makeDensityTestValues();

    //---- Default implementations of inverse test data generation methods ----

    /** Creates the default inverse cumulative probability test input values */
    public double[] makeInverseCumulativeTestPoints() {
        return makeCumulativeTestValues();
    }

    /** Creates the default inverse cumulative probability density test expected values */
    public double[] makeInverseCumulativeTestValues() {
        return makeCumulativeTestPoints();
    }

    //-------------------- Setup / tear down ----------------------------------

    /**
     * Setup sets all test instance data to default values
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        distribution = makeDistribution();
        cumulativeTestPoints = makeCumulativeTestPoints();
        cumulativeTestValues = makeCumulativeTestValues();
        inverseCumulativeTestPoints = makeInverseCumulativeTestPoints();
        inverseCumulativeTestValues = makeInverseCumulativeTestValues();
        densityTestValues = makeDensityTestValues();
    }

    /**
     * Cleans up test instance data
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        distribution = null;
        cumulativeTestPoints = null;
        cumulativeTestValues = null;
        inverseCumulativeTestPoints = null;
        inverseCumulativeTestValues = null;
        densityTestValues = null;
    }

    //-------------------- Verification methods -------------------------------

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyCumulativeProbabilities() throws Exception {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect cumulative probability value returned for "
                + cumulativeTestPoints[i], cumulativeTestValues[i],
                distribution.cumulativeProbability(cumulativeTestPoints[i]),
                getTolerance());
        }
    }

    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using current test instance data
     */
    protected void verifyInverseCumulativeProbabilities() throws Exception {
        for (int i = 0; i < inverseCumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect inverse cumulative probability value returned for "
                + inverseCumulativeTestPoints[i], inverseCumulativeTestValues[i],
                 distribution.inverseCumulativeProbability(inverseCumulativeTestPoints[i]),
                 getTolerance());
        }
    }

    /**
     * Verifies that density calculations match expected values
     */
    protected void verifyDensities() throws Exception {
        for (int i = 0; i < cumulativeTestPoints.length; i++) {
            TestUtils.assertEquals("Incorrect probability density value returned for "
                + cumulativeTestPoints[i], densityTestValues[i],
                 //TODO: remove cast when density(double) is added to ContinuousDistribution
                 ((AbstractContinuousDistribution) distribution).density(cumulativeTestPoints[i]),
                 getTolerance());
        }
    }

    //------------------------ Default test cases -----------------------------

    /**
     * Verifies that cumulative probability density calculations match expected values
     * using default test instance data
     */
    public void testCumulativeProbabilities() throws Exception {
        verifyCumulativeProbabilities();
    }

    /**
     * Verifies that inverse cumulative probability density calculations match expected values
     * using default test instance data
     */
    public void testInverseCumulativeProbabilities() throws Exception {
        verifyInverseCumulativeProbabilities();
    }

    /**
     * Verifies that density calculations return expected values
     * for default test instance data
     */
    public void testDensities() throws Exception {
        verifyDensities();
    }

    /**
     * Verifies that probability computations are consistent
     */
    public void testConsistency() throws Exception {
        for (int i=1; i < cumulativeTestPoints.length; i++) {

            // check that cdf(x, x) = 0
            TestUtils.assertEquals(0d,
               distribution.cumulativeProbability
                 (cumulativeTestPoints[i], cumulativeTestPoints[i]), tolerance);

            // check that P(a < X < b) = P(X < b) - P(X < a)
            double upper = FastMath.max(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double lower = FastMath.min(cumulativeTestPoints[i], cumulativeTestPoints[i -1]);
            double diff = distribution.cumulativeProbability(upper) -
                distribution.cumulativeProbability(lower);
            double direct = distribution.cumulativeProbability(lower, upper);
            TestUtils.assertEquals("Inconsistent cumulative probabilities for ("
                    + lower + "," + upper + ")", diff, direct, tolerance);
        }
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
        AbstractContinuousDistribution dist = (AbstractContinuousDistribution) makeDistribution();
        final int sampleSize = 1000;
        double[] sample = dist.sample(sampleSize);
        double[] quartiles = TestUtils.getDistributionQuartiles(dist);
        double[] expected = {250, 250, 250, 250};
        long[] counts = new long[4];
        dist.reseedRandomGenerator(1000);  // Use fixed seed
        for (int i = 0; i < sampleSize; i++) {
            TestUtils.updateCounts(sample[i], counts, quartiles);
        }
        TestUtils.assertChiSquareAccept(expected, counts, 0.001);
    }

    //------------------ Getters / Setters for test instance data -----------
    /**
     * @return Returns the cumulativeTestPoints.
     */
    protected double[] getCumulativeTestPoints() {
        return cumulativeTestPoints;
    }

    /**
     * @param cumulativeTestPoints The cumulativeTestPoints to set.
     */
    protected void setCumulativeTestPoints(double[] cumulativeTestPoints) {
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

    protected double[] getDensityTestValues() {
        return densityTestValues;
    }

    protected void setDensityTestValues(double[] densityTestValues) {
        this.densityTestValues = densityTestValues;
    }

    /**
     * @return Returns the distribution.
     */
    protected ContinuousDistribution getDistribution() {
        return distribution;
    }

    /**
     * @param distribution The distribution to set.
     */
    protected void setDistribution(AbstractContinuousDistribution distribution) {
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
    protected double[] getInverseCumulativeTestValues() {
        return inverseCumulativeTestValues;
    }

    /**
     * @param inverseCumulativeTestValues The inverseCumulativeTestValues to set.
     */
    protected void setInverseCumulativeTestValues(double[] inverseCumulativeTestValues) {
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
