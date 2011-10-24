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

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.StatUtils;

import junit.framework.TestCase;

public class SemiVarianceTest extends TestCase {

    public void testInsufficientData() {
        double[] nothing = null;
        SemiVariance sv = new SemiVariance();
        try {
            sv.evaluate(nothing);
            fail("null is not a valid data array.");
        } catch (IllegalArgumentException iae) {
        }

        try {
            sv.setVarianceDirection(SemiVariance.UPSIDE_VARIANCE);
            sv.evaluate(nothing);
            fail("null is not a valid data array.");
        } catch (IllegalArgumentException iae) {
        }
        nothing = new double[] {};
        assertTrue(Double.isNaN(sv.evaluate(nothing)));
    }

    public void testSingleDown() {
        SemiVariance sv = new SemiVariance();
        double[] values = { 50.0d };
        double singletest = sv.evaluate(values);
        assertEquals(0.0d, singletest, 0);
    }

    public void testSingleUp() {
        SemiVariance sv = new SemiVariance(SemiVariance.UPSIDE_VARIANCE);
        double[] values = { 50.0d };
        double singletest = sv.evaluate(values);
        assertEquals(0.0d, singletest, 0);
    }

    public void testSample() {
        final double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        final int length = values.length;
        final double mean = StatUtils.mean(values); // 6.333...
        final SemiVariance sv = new SemiVariance();  // Default bias correction is true
        final double downsideSemiVariance = sv.evaluate(values); // Downside is the default
        assertEquals(TestUtils.sumSquareDev(new double[] {-2d, 2d, 4d, -2d, 3d, 5d}, mean) / (length - 1),
                downsideSemiVariance, 1E-14);

        sv.setVarianceDirection(SemiVariance.UPSIDE_VARIANCE);
        final double upsideSemiVariance = sv.evaluate(values);
        assertEquals(TestUtils.sumSquareDev(new double[] {22d, 11d, 14d}, mean) / (length - 1),
                upsideSemiVariance, 1E-14);

        // Verify that upper + lower semivariance against the mean sum to variance
        assertEquals(StatUtils.variance(values), downsideSemiVariance + upsideSemiVariance, 10e-12);
    }

    public void testPopulation() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        SemiVariance sv = new SemiVariance(false);

        double singletest = sv.evaluate(values);
        assertEquals(19.556d, singletest, 0.01d);

        sv.setVarianceDirection(SemiVariance.UPSIDE_VARIANCE);
        singletest = sv.evaluate(values);
        assertEquals(36.222d, singletest, 0.01d);
    }

    public void testNonMeanCutoffs() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        SemiVariance sv = new SemiVariance(false); // Turn off bias correction - use df = length

        double singletest = sv.evaluate(values, 1.0d, SemiVariance.DOWNSIDE_VARIANCE, false, 0, values.length);
        assertEquals(TestUtils.sumSquareDev(new double[] { -2d, -2d }, 1.0d) / values.length,
                singletest, 0.01d);

        singletest = sv.evaluate(values, 3.0d, SemiVariance.UPSIDE_VARIANCE, false, 0, values.length);
        assertEquals(TestUtils.sumSquareDev(new double[] { 4d, 22d, 11d, 14d, 5d }, 3.0d) / values.length, singletest,
                0.01d);
    }

    /**
     * Check that the lower + upper semivariance against the mean sum to the
     * variance.
     */
    public void testVarianceDecompMeanCutoff() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        double variance = StatUtils.variance(values);
        SemiVariance sv = new SemiVariance(true); // Bias corrected
        sv.setVarianceDirection(SemiVariance.DOWNSIDE_VARIANCE);
        final double lower = sv.evaluate(values);
        sv.setVarianceDirection(SemiVariance.UPSIDE_VARIANCE);
        final double upper = sv.evaluate(values);
        assertEquals(variance, lower + upper, 10e-12);
    }

    /**
     * Check that upper and lower semivariances against a cutoff sum to the sum
     * of squared deviations of the full set of values against the cutoff
     * divided by df = length - 1 (assuming bias-corrected).
     */
    public void testVarianceDecompNonMeanCutoff() {
        double[] values = { -2.0d, 2.0d, 4.0d, -2.0d, 22.0d, 11.0d, 3.0d, 14.0d, 5.0d };
        double target = 0;
        double totalSumOfSquares = TestUtils.sumSquareDev(values, target);
        SemiVariance sv = new SemiVariance(true); // Bias corrected
        sv.setVarianceDirection(SemiVariance.DOWNSIDE_VARIANCE);
        double lower = sv.evaluate(values, target);
        sv.setVarianceDirection(SemiVariance.UPSIDE_VARIANCE);
        double upper = sv.evaluate(values, target);
        assertEquals(totalSumOfSquares / (values.length - 1), lower + upper, 10e-12);
    }

    public void testNoVariance() {
        final double[] values = {100d, 100d, 100d, 100d};
        SemiVariance sv = new SemiVariance();
        assertEquals(0, sv.evaluate(values), 10E-12);
        assertEquals(0, sv.evaluate(values, 100d), 10E-12);
        assertEquals(0, sv.evaluate(values, 100d, SemiVariance.UPSIDE_VARIANCE, false, 0, values.length), 10E-12);
    }
}
