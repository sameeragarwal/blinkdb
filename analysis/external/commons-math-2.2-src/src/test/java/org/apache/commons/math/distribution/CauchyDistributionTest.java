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
 * Test cases for CauchyDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class CauchyDistributionTest extends ContinuousDistributionAbstractTest  {

    /**
     * Constructor for CauchyDistributionTest.
     * @param arg0
     */
    public CauchyDistributionTest(String arg0) {
        super(arg0);
    }

    // --------------------- Override tolerance  --------------
    protected double defaultTolerance = NormalDistributionImpl.DEFAULT_INVERSE_ABSOLUTE_ACCURACY;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(defaultTolerance);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public CauchyDistribution makeDistribution() {
        return new CauchyDistributionImpl(1.2, 2.1);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R 2.9.2
        return new double[] {-667.24856187, -65.6230835029, -25.4830299460, -12.0588781808,
                -5.26313542807, 669.64856187, 68.0230835029, 27.8830299460, 14.4588781808, 7.66313542807};
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
        return new double[] {1.49599158008e-06, 0.000149550440335, 0.000933076881878, 0.00370933207799, 0.0144742330437,
                1.49599158008e-06, 0.000149550440335, 0.000933076881878, 0.00370933207799, 0.0144742330437};
    }

    //---------------------------- Additional test cases -------------------------

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0.0, 1.0});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testMedian() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        double expected = FastMath.random();
        distribution.setMedian(expected);
        assertEquals(expected, distribution.getMedian(), 0.0);
    }

    public void testScale() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        double expected = FastMath.random();
        distribution.setScale(expected);
        assertEquals(expected, distribution.getScale(), 0.0);
    }

    public void testSetScale() {
        CauchyDistribution distribution = (CauchyDistribution) getDistribution();
        try {
            distribution.setScale(0.0);
            fail("Can not have 0.0 scale.");
        } catch (IllegalArgumentException ex) {
            // success
        }

        try {
            distribution.setScale(-1.0);
            fail("Can not have negative scale.");
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testMomonts() {
        CauchyDistributionImpl dist;
        
        dist = new CauchyDistributionImpl(10.2, 0.15);
        assertTrue(Double.isNaN(dist.getNumericalMean()));
        assertTrue(Double.isNaN(dist.getNumericalVariance())); 
        
        dist.setMedian(23.12);
        dist.setScale(2.12);
        assertTrue(Double.isNaN(dist.getNumericalMean()));
        assertTrue(Double.isNaN(dist.getNumericalVariance()));
    }
}
