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

/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */
public class GeometricMeanTest extends StorelessUnivariateStatisticAbstractTest{

    protected GeometricMean stat;

    /**
     * @param name
     */
    public GeometricMeanTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new GeometricMean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.geoMean;
    }

    public void testSpecialValues() {
        GeometricMean mean = new GeometricMean();
        // empty
        assertTrue(Double.isNaN(mean.getResult()));

        // finite data
        mean.increment(1d);
        assertFalse(Double.isNaN(mean.getResult()));

        // add 0 -- makes log sum blow to minus infinity, should make 0
        mean.increment(0d);
        assertEquals(0d, mean.getResult(), 0);

        // add positive infinity - note the minus infinity above
        mean.increment(Double.POSITIVE_INFINITY);
        assertTrue(Double.isNaN(mean.getResult()));

        // clear
        mean.clear();
        assertTrue(Double.isNaN(mean.getResult()));

        // positive infinity by itself
        mean.increment(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, mean.getResult(), 0);

        // negative value -- should make NaN
        mean.increment(-2d);
        assertTrue(Double.isNaN(mean.getResult()));
    }

}
