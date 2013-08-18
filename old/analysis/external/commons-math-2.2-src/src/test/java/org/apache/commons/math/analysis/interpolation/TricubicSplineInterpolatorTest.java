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
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.analysis.TrivariateRealFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for the tricubic interpolator.
 * 
 * @version $Revision$ $Date$ 
 */
public final class TricubicSplineInterpolatorTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() throws MathException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};
        double[][][] fval = new double[xval.length][yval.length][zval.length];

        TrivariateRealGridInterpolator interpolator = new TricubicSplineInterpolator();
        
        @SuppressWarnings("unused")
        TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
        
        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            p = interpolator.interpolate(wxval, yval, zval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        double[] wyval = new double[] {-4, -3, -1, -1};
        try {
            p = interpolator.interpolate(xval, wyval, zval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        double[] wzval = new double[] {-12, -8, -5.5, -3, -4, 2.5};
        try {
            p = interpolator.interpolate(xval, yval, wzval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        double[][][] wfval = new double[xval.length][yval.length + 1][zval.length];
        try {
            p = interpolator.interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length - 1][yval.length][zval.length];
        try {
            p = interpolator.interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length][zval.length - 1];
        try {
            p = interpolator.interpolate(xval, yval, zval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
    }

    /**
     * Test of interpolator for a plane.
     * <p>
     * f(x, y, z) = 2 x - 3 y - z + 5
     */
    @Test
    public void testPlane() throws MathException {
        TrivariateRealFunction f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return 2 * x - 3 * y - z + 5;
                }
            };

        TrivariateRealGridInterpolator interpolator = new TricubicSplineInterpolator();

        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};
        double[][][] fval = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
        double x, y, z;
        double expected, result;
        
        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("On sample point", expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("half-way between sample points (middle of the patch)", expected, result, 0.3);

        x = 3.5;
        y = -3.5;
        z = -10;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("half-way between sample points (border of the patch)", expected, result, 0.3);
    }

    /**
     * Test of interpolator for a sine wave.
     * <p>
     * <p>
     *  f(x, y, z) = a cos [&omega; z - k<sub>y</sub> x - k<sub>y</sub> y]
     * </p>
     * with A = 0.2, &omega; = 0.5, k<sub>x</sub> = 2, k<sub>y</sub> = 1.
     */
    @Test
    public void testWave() throws MathException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 4};

        final double a = 0.2;
        final double omega = 0.5;
        final double kx = 2;
        final double ky = 1;

        // Function values
        TrivariateRealFunction f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.cos(omega * z - kx * x - ky * y);
                }
            };
        
        double[][][] fval = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    fval[i][j][k] = f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        TrivariateRealGridInterpolator interpolator = new TricubicSplineInterpolator();

        TrivariateRealFunction p = interpolator.interpolate(xval, yval, zval, fval);
        double x, y, z;
        double expected, result;
        
        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-12);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 0.1);

        x = 3.5;
        y = -3.5;
        z = -10;
        expected = f.value(x, y, z);
        result = p.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 0.1);
    }
}
