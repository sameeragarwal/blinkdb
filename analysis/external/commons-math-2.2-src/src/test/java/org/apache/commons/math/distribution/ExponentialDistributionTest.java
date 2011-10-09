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

import org.apache.commons.math.util.FastMath;

/**
 * Test cases for ExponentialDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class ExponentialDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for ExponentialDistributionTest.
     * @param name
     */
    public ExponentialDistributionTest(String name) {
        super(name);
    }

    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1E-9);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ExponentialDistribution makeDistribution() {
        return new ExponentialDistributionImpl(5.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {0.00500250166792, 0.0502516792675, 0.126589039921, 0.256466471938,
                0.526802578289, 34.5387763949, 23.0258509299, 18.4443972706, 14.9786613678, 11.5129254650};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001, 0.01, 0.025, 0.05, 0.1, 0.999,
                0.990, 0.975, 0.950, 0.900};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.1998, 0.198, 0.195, 0.19, 0.18, 0.000200000000000,
                0.00200000000002, 0.00499999999997, 0.00999999999994, 0.0199999999999};
    }

    //------------ Additional tests -------------------------------------------

    public void testCumulativeProbabilityExtremes() throws Exception {
        setCumulativeTestPoints(new double[] {-2, 0});
        setCumulativeTestValues(new double[] {0, 0});
        verifyCumulativeProbabilities();
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
         setInverseCumulativeTestPoints(new double[] {0, 1});
         setInverseCumulativeTestValues(new double[] {0, Double.POSITIVE_INFINITY});
         verifyInverseCumulativeProbabilities();
    }

    public void testCumulativeProbability2() throws Exception {
        double actual = getDistribution().cumulativeProbability(0.25, 0.75);
        assertEquals(0.0905214, actual, 10e-4);
    }

    public void testDensity() {
        ExponentialDistribution d1 = new ExponentialDistributionImpl(1);
        assertEquals(0.0, d1.density(-1e-9));
        assertEquals(1.0, d1.density(0.0));
        assertEquals(0.0, d1.density(1000.0));
        assertEquals(FastMath.exp(-1), d1.density(1.0));
        assertEquals(FastMath.exp(-2), d1.density(2.0));

        ExponentialDistribution d2 = new ExponentialDistributionImpl(3);
        assertEquals(1/3.0, d2.density(0.0));
        // computed using  print(dexp(1, rate=1/3), digits=10) in R 2.5
        assertEquals(0.2388437702, d2.density(1.0), 1e-8);

        // computed using  print(dexp(2, rate=1/3), digits=10) in R 2.5
        assertEquals(0.1711390397, d2.density(2.0), 1e-8);
    }

    public void testMeanAccessors() {
        ExponentialDistribution distribution = (ExponentialDistribution) getDistribution();
        assertEquals(5d, distribution.getMean(), Double.MIN_VALUE);
        distribution.setMean(2d);
        assertEquals(2d, distribution.getMean(), Double.MIN_VALUE);
        try {
            distribution.setMean(0);
            fail("Expecting IllegalArgumentException for 0 mean");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }


    public void testMomonts() {
        final double tol = 1e-9;
        ExponentialDistributionImpl dist;
        
        dist = new ExponentialDistributionImpl(11d);
        assertEquals(dist.getNumericalMean(), 11d, tol);
        assertEquals(dist.getNumericalVariance(), 11d * 11d, tol);
        
        dist.setMean(10.5d);
        assertEquals(dist.getNumericalMean(), 10.5d, tol);
        assertEquals(dist.getNumericalVariance(), 10.5d * 10.5d, tol);
    }
}
