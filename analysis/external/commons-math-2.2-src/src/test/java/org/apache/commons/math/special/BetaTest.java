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
package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class BetaTest extends TestCase {
    /**
     * Constructor for BetaTest.
     * @param name
     */
    public BetaTest(String name) {
        super(name);
    }

    private void testRegularizedBeta(double expected, double x, double a,
        double b)
    {
        try {
            double actual = Beta.regularizedBeta(x, a, b);
            TestUtils.assertEquals(expected, actual, 10e-15);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }

    private void testLogBeta(double expected, double a, double b) {
        double actual = Beta.logBeta(a, b);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }

    public void testRegularizedBetaNanPositivePositive() {
        testRegularizedBeta(Double.NaN, Double.NaN, 1.0, 1.0);
    }

    public void testRegularizedBetaPositiveNanPositive() {
        testRegularizedBeta(Double.NaN, 0.5, Double.NaN, 1.0);
    }

    public void testRegularizedBetaPositivePositiveNan() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, Double.NaN);
    }

    public void testRegularizedBetaNegativePositivePositive() {
        testRegularizedBeta(Double.NaN, -0.5, 1.0, 2.0);
    }

    public void testRegularizedBetaPositiveNegativePositive() {
        testRegularizedBeta(Double.NaN, 0.5, -1.0, 2.0);
    }

    public void testRegularizedBetaPositivePositiveNegative() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, -2.0);
    }

    public void testRegularizedBetaZeroPositivePositive() {
        testRegularizedBeta(0.0, 0.0, 1.0, 2.0);
    }

    public void testRegularizedBetaPositiveZeroPositive() {
        testRegularizedBeta(Double.NaN, 0.5, 0.0, 2.0);
    }

    public void testRegularizedBetaPositivePositiveZero() {
        testRegularizedBeta(Double.NaN, 0.5, 1.0, 0.0);
    }

    public void testRegularizedBetaPositivePositivePositive() {
        testRegularizedBeta(0.75, 0.5, 1.0, 2.0);
    }

    public void testLogBetaNanPositive() {
        testLogBeta(Double.NaN, Double.NaN, 2.0);
    }

    public void testLogBetaPositiveNan() {
        testLogBeta(Double.NaN, 1.0, Double.NaN);
    }

    public void testLogBetaNegativePositive() {
        testLogBeta(Double.NaN, -1.0, 2.0);
    }

    public void testLogBetaPositiveNegative() {
        testLogBeta(Double.NaN, 1.0, -2.0);
    }

    public void testLogBetaZeroPositive() {
        testLogBeta(Double.NaN, 0.0, 2.0);
    }

    public void testLogBetaPositiveZero() {
        testLogBeta(Double.NaN, 1.0, 0.0);
    }

    public void testLogBetaPositivePositive() {
        testLogBeta(-0.693147180559945, 1.0, 2.0);
    }
}
