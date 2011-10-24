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
package org.apache.commons.math.stat.descriptive.rank;

import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */
public class PercentileTest extends UnivariateStatisticAbstractTest{

    protected Percentile stat;

    /**
     * @param name
     */
    public PercentileTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Percentile(95.0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.percentile95;
    }

    public void testHighPercentile(){
        double[] d = new double[]{1, 2, 3};
        Percentile p = new Percentile(75);
        assertEquals(3.0, p.evaluate(d), 1.0e-5);
    }

    public void testPercentile() {
        double[] d = new double[] {1, 3, 2, 4};
        Percentile p = new Percentile(30);
        assertEquals(1.5, p.evaluate(d), 1.0e-5);
        p.setQuantile(25);
        assertEquals(1.25, p.evaluate(d), 1.0e-5);
        p.setQuantile(75);
        assertEquals(3.75, p.evaluate(d), 1.0e-5);
        p.setQuantile(50);
        assertEquals(2.5, p.evaluate(d), 1.0e-5);

        // invalid percentiles
        try {
            p.evaluate(d, 0, d.length, -1.0);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
        try {
            p.evaluate(d, 0, d.length, 101.0);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }
    }

    public void testNISTExample() {
        double[] d = new double[] {95.1772, 95.1567, 95.1937, 95.1959,
                95.1442, 95.0610,  95.1591, 95.1195, 95.1772, 95.0925, 95.1990, 95.1682
        };
        Percentile p = new Percentile(90);
        assertEquals(95.1981, p.evaluate(d), 1.0e-4);
        assertEquals(95.1990, p.evaluate(d,0,d.length, 100d), 0);
    }

    public void test5() {
        Percentile percentile = new Percentile(5);
        assertEquals(this.percentile5, percentile.evaluate(testArray), getTolerance());
    }

    public void testNullEmpty() {
        Percentile percentile = new Percentile(50);
        double[] nullArray = null;
        double[] emptyArray = new double[] {};
        try {
            percentile.evaluate(nullArray);
            fail("Expecting IllegalArgumentException for null array");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        assertTrue(Double.isNaN(percentile.evaluate(emptyArray)));
    }

    public void testSingleton() {
        Percentile percentile = new Percentile(50);
        double[] singletonArray = new double[] {1d};
        assertEquals(1d, percentile.evaluate(singletonArray), 0);
        assertEquals(1d, percentile.evaluate(singletonArray, 0, 1), 0);
        assertEquals(1d, percentile.evaluate(singletonArray, 0, 1, 5), 0);
        assertEquals(1d, percentile.evaluate(singletonArray, 0, 1, 100), 0);
        assertTrue(Double.isNaN(percentile.evaluate(singletonArray, 0, 0)));
    }

    public void testSpecialValues() {
        Percentile percentile = new Percentile(50);
        double[] specialValues = new double[] {0d, 1d, 2d, 3d, 4d,  Double.NaN};
        assertEquals(2.5d, percentile.evaluate(specialValues), 0);
        specialValues =  new double[] {Double.NEGATIVE_INFINITY, 1d, 2d, 3d,
                Double.NaN, Double.POSITIVE_INFINITY};
        assertEquals(2.5d, percentile.evaluate(specialValues), 0);
        specialValues = new double[] {1d, 1d, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY};
        assertTrue(Double.isInfinite(percentile.evaluate(specialValues)));
        specialValues = new double[] {1d, 1d, Double.NaN,
                Double.NaN};
        assertTrue(Double.isNaN(percentile.evaluate(specialValues)));
        specialValues = new double[] {1d, 1d, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY};
        // Interpolation results in NEGATIVE_INFINITY + POSITIVE_INFINITY
        assertTrue(Double.isNaN(percentile.evaluate(specialValues)));
    }

    public void testSetQuantile() {
        Percentile percentile = new Percentile(10);
        percentile.setQuantile(100); // OK
        assertEquals(100, percentile.getQuantile(), 0);
        try {
            percentile.setQuantile(0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new Percentile(0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

}
