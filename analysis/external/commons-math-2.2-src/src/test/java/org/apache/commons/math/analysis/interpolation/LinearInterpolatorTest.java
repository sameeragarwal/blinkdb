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
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.NonMonotonousSequenceException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the LinearInterpolator.
 */
public class LinearInterpolatorTest {

    /** error tolerance for spline interpolator value at knot points */
    protected double knotTolerance = 1E-12;

    /** error tolerance for interpolating polynomial coefficients */
    protected double coefficientTolerance = 1E-6;

    /** error tolerance for interpolated values */
    protected double interpolationTolerance = 1E-12;

    @Test
    public void testInterpolateLinearDegenerateTwoSegment()
        throws Exception {
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 1.0 };
        UnivariateRealInterpolator i = new LinearInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);

        // Check interpolation
        Assert.assertEquals(0.0,f.value(0.0), interpolationTolerance);
        Assert.assertEquals(0.4,f.value(0.4), interpolationTolerance);
        Assert.assertEquals(1.0,f.value(1.0), interpolationTolerance);
    }

    @Test
    public void testInterpolateLinearDegenerateThreeSegment()
        throws Exception {
        double x[] = { 0.0, 0.5, 1.0, 1.5 };
        double y[] = { 0.0, 0.5, 1.0, 1.5 };
        UnivariateRealInterpolator i = new LinearInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], 1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[2], 1d};
        TestUtils.assertEquals(polynomials[2].getCoefficients(), target, coefficientTolerance);

        // Check interpolation
        Assert.assertEquals(0,f.value(0), interpolationTolerance);
        Assert.assertEquals(1.4,f.value(1.4), interpolationTolerance);
        Assert.assertEquals(1.5,f.value(1.5), interpolationTolerance);
    }

    @Test
    public void testInterpolateLinear() throws Exception {
        double x[] = { 0.0, 0.5, 1.0 };
        double y[] = { 0.0, 0.5, 0.0 };
        UnivariateRealInterpolator i = new LinearInterpolator();
        UnivariateRealFunction f = i.interpolate(x, y);
        verifyInterpolation(f, x, y);

        // Verify coefficients using analytical values
        PolynomialFunction polynomials[] = ((PolynomialSplineFunction) f).getPolynomials();
        double target[] = {y[0], 1d};
        TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
        target = new double[]{y[1], -1d};
        TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
    }

    @Test
    public void testIllegalArguments() throws MathException {
        // Data set arrays of different size.
        UnivariateRealInterpolator i = new LinearInterpolator();
        try {
            double xval[] = { 0.0, 1.0 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect data set array with different sizes.");
        } catch (DimensionMismatchException iae) {
            // Expected.
        }
        // X values not sorted.
        try {
            double xval[] = { 0.0, 1.0, 0.5 };
            double yval[] = { 0.0, 1.0, 2.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect unsorted arguments.");
        } catch (NonMonotonousSequenceException iae) {
            // Expected.
        }
        // Not enough data to interpolate.
        try {
            double xval[] = { 0.0 };
            double yval[] = { 0.0 };
            i.interpolate(xval, yval);
            Assert.fail("Failed to detect unsorted arguments.");
        } catch (NumberIsTooSmallException iae) {
            // Expected.
        }
    }

    /**
     * verifies that f(x[i]) = y[i] for i = 0..n-1 where n is common length.
     */
    protected void verifyInterpolation(UnivariateRealFunction f, double x[], double y[])
        throws Exception{
        for (int i = 0; i < x.length; i++) {
            Assert.assertEquals(f.value(x[i]), y[i], knotTolerance);
        }
    }

}
