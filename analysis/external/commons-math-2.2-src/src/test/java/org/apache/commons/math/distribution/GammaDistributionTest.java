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

/**
 * Test cases for GammaDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class GammaDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for GammaDistributionTest.
     * @param name
     */
    public GammaDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public GammaDistribution makeDistribution() {
        return new GammaDistributionImpl(4d, 2d);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {0.857104827257, 1.64649737269, 2.17973074725, 2.7326367935, 3.48953912565,
                26.1244815584, 20.0902350297, 17.5345461395, 15.5073130559, 13.3615661365};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.00427280075546, 0.0204117166709, 0.0362756163658, 0.0542113174239, 0.0773195272491,
                0.000394468852816, 0.00366559696761, 0.00874649473311, 0.0166712508128, 0.0311798227954};
    }

    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1e-9);
    }

    //---------------------------- Additional test cases -------------------------
    public void testParameterAccessors() {
        GammaDistribution distribution = (GammaDistribution) getDistribution();
        assertEquals(4d, distribution.getAlpha(), 0);
        distribution.setAlpha(3d);
        assertEquals(3d, distribution.getAlpha(), 0);
        assertEquals(2d, distribution.getBeta(), 0);
        distribution.setBeta(4d);
        assertEquals(4d, distribution.getBeta(), 0);
        try {
            distribution.setAlpha(0d);
            fail("Expecting IllegalArgumentException for alpha = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            distribution.setBeta(0d);
            fail("Expecting IllegalArgumentException for beta = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testProbabilities() throws Exception {
        testProbability(-1.000, 4.0, 2.0, .0000);
        testProbability(15.501, 4.0, 2.0, .9499);
        testProbability(0.504, 4.0, 1.0, .0018);
        testProbability(10.011, 1.0, 2.0, .9933);
        testProbability(5.000, 2.0, 2.0, .7127);
    }

    public void testValues() throws Exception {
        testValue(15.501, 4.0, 2.0, .9499);
        testValue(0.504, 4.0, 1.0, .0018);
        testValue(10.011, 1.0, 2.0, .9933);
        testValue(5.000, 2.0, 2.0, .7127);
    }

    private void testProbability(double x, double a, double b, double expected) throws Exception {
        GammaDistribution distribution = new GammaDistributionImpl( a, b );
        double actual = distribution.cumulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }

    private void testValue(double expected, double a, double b, double p) throws Exception {
        GammaDistribution distribution = new GammaDistributionImpl( a, b );
        double actual = distribution.inverseCumulativeProbability(p);
        assertEquals("critical value for " + p, expected, actual, 10e-4);
    }

    public void testDensity() {
        double[] x = new double[]{-0.1, 1e-6, 0.5, 1, 2, 5};
        // R2.5: print(dgamma(x, shape=1, rate=1), digits=10)
        checkDensity(1, 1, x, new double[]{0.000000000000, 0.999999000001, 0.606530659713, 0.367879441171, 0.135335283237, 0.006737946999});
        // R2.5: print(dgamma(x, shape=2, rate=1), digits=10)
        checkDensity(2, 1, x, new double[]{0.000000000000, 0.000000999999, 0.303265329856, 0.367879441171, 0.270670566473, 0.033689734995});
        // R2.5: print(dgamma(x, shape=4, rate=1), digits=10)
        checkDensity(4, 1, x, new double[]{0.000000000e+00, 1.666665000e-19, 1.263605541e-02, 6.131324020e-02, 1.804470443e-01, 1.403738958e-01});
        // R2.5: print(dgamma(x, shape=4, rate=10), digits=10)
        checkDensity(4, 10, x, new double[]{0.000000000e+00, 1.666650000e-15, 1.403738958e+00, 7.566654960e-02, 2.748204830e-05, 4.018228850e-17});
        // R2.5: print(dgamma(x, shape=.1, rate=10), digits=10)
        checkDensity(0.1, 10, x, new double[]{0.000000000e+00, 3.323953832e+04, 1.663849010e-03, 6.007786726e-06, 1.461647647e-10, 5.996008322e-24});
        // R2.5: print(dgamma(x, shape=.1, rate=20), digits=10)
        checkDensity(0.1, 20, x, new double[]{0.000000000e+00, 3.562489883e+04, 1.201557345e-05, 2.923295295e-10, 3.228910843e-19, 1.239484589e-45});
        // R2.5: print(dgamma(x, shape=.1, rate=4), digits=10)
        checkDensity(0.1, 4, x, new double[]{0.000000000e+00, 3.032938388e+04, 3.049322494e-02, 2.211502311e-03, 2.170613371e-05, 5.846590589e-11});
        // R2.5: print(dgamma(x, shape=.1, rate=1), digits=10)
        checkDensity(0.1, 1, x, new double[]{0.000000000e+00, 2.640334143e+04, 1.189704437e-01, 3.866916944e-02, 7.623306235e-03, 1.663849010e-04});
    }

    private void checkDensity(double alpha, double rate, double[] x, double[] expected) {
        GammaDistribution d = new GammaDistributionImpl(alpha, 1 / rate);
        for (int i = 0; i < x.length; i++) {
            assertEquals(expected[i], d.density(x[i]), 1e-5);
        }
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(new double[] {0, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testMomonts() {
        final double tol = 1e-9;
        GammaDistributionImpl dist;
        
        dist = new GammaDistributionImpl(1, 2);
        assertEquals(dist.getNumericalMean(), 2, tol);
        assertEquals(dist.getNumericalVariance(), 4, tol); 
        
        dist.setAlpha(1.1);
        dist.setBeta(4.2);        
        assertEquals(dist.getNumericalMean(), 1.1d * 4.2d, tol);
        assertEquals(dist.getNumericalVariance(), 1.1d * 4.2d * 4.2d, tol);
    }
}
