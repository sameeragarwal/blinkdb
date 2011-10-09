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
package org.apache.commons.math.stat.descriptive.moment;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.util.FastMath;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public class StandardDeviationTest extends StorelessUnivariateStatisticAbstractTest{

    protected StandardDeviation stat;

    /**
     * @param name
     */
    public StandardDeviationTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new StandardDeviation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.std;
    }

    /**
     * Make sure Double.NaN is returned iff n = 0
     *
     */
    public void testNaN() {
        StandardDeviation std = new StandardDeviation();
        assertTrue(Double.isNaN(std.getResult()));
        std.increment(1d);
        assertEquals(0d, std.getResult(), 0);
    }

    /**
     * Test population version of variance
     */
    public void testPopulation() {
        double[] values = {-1.0d, 3.1d, 4.0d, -2.1d, 22d, 11.7d, 3d, 14d};
        double sigma = populationStandardDeviation(values);
        SecondMoment m = new SecondMoment();
        m.evaluate(values);  // side effect is to add values
        StandardDeviation s1 = new StandardDeviation();
        s1.setBiasCorrected(false);
        assertEquals(sigma, s1.evaluate(values), 1E-14);
        s1.incrementAll(values);
        assertEquals(sigma, s1.getResult(), 1E-14);
        s1 = new StandardDeviation(false, m);
        assertEquals(sigma, s1.getResult(), 1E-14);
        s1 = new StandardDeviation(false);
        assertEquals(sigma, s1.evaluate(values), 1E-14);
        s1.incrementAll(values);
        assertEquals(sigma, s1.getResult(), 1E-14);
    }

    /**
     * Definitional formula for population standard deviation
     */
    protected double populationStandardDeviation(double[] v) {
        double mean = new Mean().evaluate(v);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
            sum += (v[i] - mean) * (v[i] - mean);
        }
        return FastMath.sqrt(sum / v.length);
    }

}
