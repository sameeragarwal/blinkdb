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
 * Test cases for TDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class TDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for TDistributionTest.
     * @param name
     */
    public TDistributionTest(String name) {
        super(name);
    }

//-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public TDistribution makeDistribution() {
        return new TDistributionImpl(5.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {-5.89342953136, -3.36492999891, -2.57058183564, -2.01504837333, -1.47588404882,
                5.89342953136, 3.36492999891, 2.57058183564, 2.01504837333, 1.47588404882};
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
        return new double[] {0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005,
                0.000756494565517, 0.0109109752919, 0.0303377878006, 0.0637967988952, 0.128289492005};
    }

    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1E-9);
    }

    //---------------------------- Additional test cases -------------------------
    /**
     * @see <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=27243">
     *      Bug report that prompted this unit test.</a>
     */
    public void testCumulativeProbabilityAgaintStackOverflow() throws Exception {
        TDistributionImpl td = new TDistributionImpl(5.);
        td.cumulativeProbability(.1);
        td.cumulativeProbability(.01);
    }

    public void testSmallDf() throws Exception {
        setDistribution(new TDistributionImpl(1d));
        // quantiles computed using R version 2.9.2
        setCumulativeTestPoints(new double[] {-318.308838986, -31.8205159538, -12.7062047362,
                -6.31375151468, -3.07768353718, 318.308838986, 31.8205159538, 12.7062047362,
                 6.31375151468, 3.07768353718});
        setDensityTestValues(new double[] {3.14158231817e-06, 0.000314055924703, 0.00195946145194,
                0.00778959736375, 0.0303958893917, 3.14158231817e-06, 0.000314055924703,
                0.00195946145194, 0.00778959736375, 0.0303958893917});
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
        verifyDensities();
    }

    public void testInverseCumulativeProbabilityExtremes() throws Exception {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(
                new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    public void testDfAccessors() {
        TDistribution distribution = (TDistribution) getDistribution();
        assertEquals(5d, distribution.getDegreesOfFreedom(), Double.MIN_VALUE);
        distribution.setDegreesOfFreedom(4d);
        assertEquals(4d, distribution.getDegreesOfFreedom(), Double.MIN_VALUE);
        try {
            distribution.setDegreesOfFreedom(0d);
            fail("Expecting IllegalArgumentException for df = 0");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testMomonts() {
        final double tol = 1e-9;
        TDistributionImpl dist;
        
        dist = new TDistributionImpl(1);
        assertTrue(Double.isNaN(dist.getNumericalMean()));
        assertTrue(Double.isNaN(dist.getNumericalVariance())); 
        
        dist.setDegreesOfFreedom(1.5);
        assertEquals(dist.getNumericalMean(), 0, tol);
        assertTrue(Double.isInfinite(dist.getNumericalVariance()));
        
        dist.setDegreesOfFreedom(5);
        assertEquals(dist.getNumericalMean(), 0, tol);
        assertEquals(dist.getNumericalVariance(), 5d / (5d - 2d), tol);        
    }
}
