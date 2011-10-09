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
package org.apache.commons.math.stat.descriptive.summary;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;

/**
 * Test cases for the {@link Sum} class.
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */
public class SumTest extends StorelessUnivariateStatisticAbstractTest{

    protected Sum stat;

    /**
     * @param name
     */
    public SumTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.sum;
    }

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    public double expectedWeightedValue() {
        return this.weightedSum;
    }

    public void testSpecialValues() {
        Sum sum = new Sum();
        assertTrue(Double.isNaN(sum.getResult()));
        sum.increment(1);
        assertEquals(1, sum.getResult(), 0);
        sum.increment(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, sum.getResult(), 0);
        sum.increment(Double.NEGATIVE_INFINITY);
        assertTrue(Double.isNaN(sum.getResult()));
        sum.increment(1);
        assertTrue(Double.isNaN(sum.getResult()));
    }

    public void testWeightedSum() {
        Sum sum = new Sum();
        assertEquals(expectedWeightedValue(), sum.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
        assertEquals(expectedValue(), sum.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
    }

}
