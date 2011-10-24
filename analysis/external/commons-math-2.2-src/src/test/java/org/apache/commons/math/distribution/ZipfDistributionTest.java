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
 * Test cases for {@link ZipfDistribution}.
 * Extends IntegerDistributionAbstractTest.  See class javadoc for
 * IntegerDistributionAbstractTest for details.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class ZipfDistributionTest extends IntegerDistributionAbstractTest {
    public ZipfDistributionTest(String name) {
        super(name);
    }

    //-------------- Implementations for abstract methods -----------------------

    /** Creates the default discrete distribution instance to use in tests. */
    @Override
    public IntegerDistribution makeDistribution() {
        return new ZipfDistributionImpl(10, 1);
    }

    /** Creates the default probability density test input values */
    @Override
    public int[] makeDensityTestPoints() {
        return new int[] {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0d, 0d, 0.3414d, 0.1707d, 0.1138d, 0.0854d, 0.0683d,
                0.0569d, 0.0488d, 0.0427d, 0.0379d, 0.0341d, 0d};
    }

    /** Creates the default cumulative probability density test input values */
    @Override
    public int[] makeCumulativeTestPoints() {
        return makeDensityTestPoints();
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0d, 0.0000d, 0.3414d, 0.5121d, 0.6259d, 0.7113d,
                0.7796d, 0.8365d, 0.8852d, 0.9279d, 0.9659d, 1d, 1d};
        }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0, 0.001d, 0.010d, 0.025d, 0.050d, 0.3414d, 0.3415d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d, 1};
        }

    /** Creates the default inverse cumulative probability density test expected values */
    @Override
    public int[] makeInverseCumulativeTestValues() {
        return new int[] {0, 0, 0, 0, 0, 0, 1, 9, 9, 9, 8, 7, 10};
    }

    public void testMomonts() {
        final double tol = 1e-9;
        ZipfDistributionImpl dist;
        
        dist = new ZipfDistributionImpl(2, 0.5);
        assertEquals(dist.getNumericalMean(), FastMath.sqrt(2), tol);
        assertEquals(dist.getNumericalVariance(), 0.24264068711928521, tol); 
    }
}
