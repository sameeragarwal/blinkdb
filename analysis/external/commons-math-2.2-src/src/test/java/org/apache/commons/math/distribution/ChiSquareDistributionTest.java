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
 * Test cases for ChiSquareDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class ChiSquareDistributionTest extends ContinuousDistributionAbstractTest {

    /**
     * Constructor for ChiSquareDistributionTest.
     * @param name
     */
    public ChiSquareDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ChiSquaredDistribution makeDistribution() {
        return new ChiSquaredDistributionImpl(5.0);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R version 2.9.2
        return new double[] {0.210212602629, 0.554298076728, 0.831211613487, 1.14547622606, 1.61030798696,
                20.5150056524, 15.0862724694, 12.8325019940, 11.0704976935, 9.23635689978};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001, 0.01, 0.025, 0.05, 0.1, 0.999, 0.990, 0.975, 0.950, 0.900};
    }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0, 0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d, 1};
    }

    /** Creates the default inverse cumulative probability density test expected values */
    @Override
    public double[] makeInverseCumulativeTestValues() {
        return new double[] {0, 0.210212602629, 0.554298076728, 0.831211613487, 1.14547622606, 1.61030798696,
                20.5150056524, 15.0862724694, 12.8325019940, 11.0704976935, 9.23635689978,
                Double.POSITIVE_INFINITY};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.0115379817652, 0.0415948507811, 0.0665060119842, 0.0919455953114, 0.121472591024,
                0.000433630076361, 0.00412780610309, 0.00999340341045, 0.0193246438937, 0.0368460089216};
    }

 // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1e-9);
    }

 //---------------------------- Additional test cases -------------------------

    public void testSmallDf() throws Exception {
        setDistribution(new ChiSquaredDistributionImpl(0.1d));
        setTolerance(1E-4);
        // quantiles computed using R version 1.8.1 (linux version)
        setCumulativeTestPoints(new double[] {1.168926E-60, 1.168926E-40, 1.063132E-32,
                1.144775E-26, 1.168926E-20, 5.472917, 2.175255, 1.13438,
                0.5318646, 0.1526342});
        setInverseCumulativeTestValues(getCumulativeTestPoints());
        setInverseCumulativeTestPoints(getCumulativeTestValues());
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
    }

    public void testDfAccessors() {
        ChiSquaredDistribution distribution = (ChiSquaredDistribution) getDistribution();
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

    public void testDensity() {
        double[] x = new double[]{-0.1, 1e-6, 0.5, 1, 2, 5};
        //R 2.5: print(dchisq(x, df=1), digits=10)
        checkDensity(1, x, new double[]{0.00000000000, 398.94208093034, 0.43939128947, 0.24197072452, 0.10377687436, 0.01464498256});
        //R 2.5: print(dchisq(x, df=0.1), digits=10)
        checkDensity(0.1, x, new double[]{0.000000000e+00, 2.486453997e+04, 7.464238732e-02, 3.009077718e-02, 9.447299159e-03, 8.827199396e-04});
        //R 2.5: print(dchisq(x, df=2), digits=10)
        checkDensity(2, x, new double[]{0.00000000000, 0.49999975000, 0.38940039154, 0.30326532986, 0.18393972059, 0.04104249931});
        //R 2.5: print(dchisq(x, df=10), digits=10)
        checkDensity(10, x, new double[]{0.000000000e+00, 1.302082682e-27, 6.337896998e-05, 7.897534632e-04, 7.664155024e-03, 6.680094289e-02});
    }

    private void checkDensity(double df, double[] x, double[] expected) {
        ChiSquaredDistribution d = new ChiSquaredDistributionImpl(df);
        for (int i = 0; i < x.length; i++) {
            assertEquals(expected[i], d.density(x[i]), 1e-5);
        }
    }

    public void testMomonts() {
        final double tol = 1e-9;
        ChiSquaredDistributionImpl dist;
        
        dist = new ChiSquaredDistributionImpl(1500);
        assertEquals(dist.getNumericalMean(), 1500, tol);
        assertEquals(dist.getNumericalVariance(), 3000, tol); 
        
        dist.setDegreesOfFreedom(1.12);
        assertEquals(dist.getNumericalMean(), 1.12, tol);
        assertEquals(dist.getNumericalVariance(), 2.24, tol);
    }

}
