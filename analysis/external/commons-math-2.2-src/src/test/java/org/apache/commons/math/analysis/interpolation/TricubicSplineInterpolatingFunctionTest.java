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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.analysis.TrivariateRealFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for the bicubic function.
 * 
 * @version $Revision: 821626 $ $Date: 2009-10-04 23:57:30 +0200 (Sun, 04 Oct 2009) $ 
 */
public final class TricubicSplineInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};
        double[][][] fval = new double[xval.length][yval.length][zval.length];

        @SuppressWarnings("unused")
        TrivariateRealFunction tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                                             fval, fval, fval, fval,
                                                                             fval, fval, fval, fval);
        
        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            tcf = new TricubicSplineInterpolatingFunction(wxval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, wyval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        double[] wzval = new double[] {-12, -8, -9, -3, 0, 2.5};
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, wzval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        double[][][] wfval = new double[xval.length - 1][yval.length - 1][zval.length];
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          wfval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, wfval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, wfval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, wfval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length - 1][zval.length];
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          wfval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, wfval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, wfval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, wfval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        wfval = new double[xval.length][yval.length][zval.length - 1];
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          wfval, fval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, wfval, fval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, wfval, fval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, wfval,
                                                          fval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          wfval, fval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, wfval, fval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, wfval, fval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                          fval, fval, fval, fval,
                                                          fval, fval, fval, wfval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        Assert.assertNotNull(tcf); // Avoid Findbugs "dead store" warning
    }

    /**
     * Test for a plane.
     * <p>
     *  f(x, y, z) = 2 x - 3 y - 4 z + 5
     * </p>
     */
    @Test
    public void testPlane() throws FunctionEvaluationException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        double[] zval = new double[] {-12, -8, -5.5, -3, 0, 2.5};

        // Function values
        TrivariateRealFunction f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return 2 * x - 3 * y - 4 * z + 5;
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
        // Partial derivatives with respect to x
        double[][][] dFdX = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdX[i][j][k] = 2;
                }
            }
        }
        // Partial derivatives with respect to y
        double[][][] dFdY = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdY[i][j][k] = -3;
                }
            }
        }

        // Partial derivatives with respect to z
        double[][][] dFdZ = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdZ[i][j][k] = -4;
                }
            }
        }
        // Partial cross-derivatives
        double[][][] d2FdXdY = new double[xval.length][yval.length][zval.length];
        double[][][] d2FdXdZ = new double[xval.length][yval.length][zval.length];
        double[][][] d2FdYdZ = new double[xval.length][yval.length][zval.length];
        double[][][] d3FdXdYdZ = new double[xval.length][yval.length][zval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    d2FdXdY[i][j][k] = 0;
                    d2FdXdZ[i][j][k] = 0;
                    d2FdYdZ[i][j][k] = 0;
                    d3FdXdYdZ[i][j][k] = 0;
                }
            }
        }

        TrivariateRealFunction tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                                             fval, dFdX, dFdY, dFdZ,
                                                                             d2FdXdY, d2FdXdZ, d2FdYdZ,
                                                                             d3FdXdYdZ);
        double x, y, z;
        double expected, result;

        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 0.3);

        x = 3.5;
        y = -3.5;
        z = -10;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 0.3);
    }

    /**
     * Sine wave.
     * <p>
     *  f(x, y, z) = a cos [&omega; z - k<sub>y</sub> x - k<sub>y</sub> y]
     * </p>
     * with A = 0.2, &omega; = 0.5, k<sub>x</sub> = 2, k<sub>y</sub> = 1.
     */
    @Test
    public void testWave() throws FunctionEvaluationException {
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
        
        // Partial derivatives with respect to x
        double[][][] dFdX = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction dFdX_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.sin(omega * z - kx * x - ky * y) * kx;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdX[i][j][k] = dFdX_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }
            
        // Partial derivatives with respect to y
        double[][][] dFdY = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction dFdY_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.sin(omega * z - kx * x - ky * y) * ky;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdY[i][j][k] = dFdY_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        // Partial derivatives with respect to z
        double[][][] dFdZ = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction dFdZ_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return -a * FastMath.sin(omega * z - kx * x - ky * y) * omega;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    dFdZ[i][j][k] = dFdZ_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        // Partial second derivatives w.r.t. (x, y)
        double[][][] d2FdXdY = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction d2FdXdY_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return -a * FastMath.cos(omega * z - kx * x - ky * y) * kx * ky;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    d2FdXdY[i][j][k] = d2FdXdY_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        // Partial second derivatives w.r.t. (x, z)
        double[][][] d2FdXdZ = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction d2FdXdZ_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.cos(omega * z - kx * x - ky * y) * kx * omega;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    d2FdXdZ[i][j][k] = d2FdXdZ_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        // Partial second derivatives w.r.t. (y, z)
        double[][][] d2FdYdZ = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction d2FdYdZ_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.cos(omega * z - kx * x - ky * y) * ky * omega;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    d2FdYdZ[i][j][k] = d2FdYdZ_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        // Partial third derivatives
        double[][][] d3FdXdYdZ = new double[xval.length][yval.length][zval.length];
        TrivariateRealFunction d3FdXdYdZ_f = new TrivariateRealFunction() {
                public double value(double x, double y, double z) {
                    return a * FastMath.sin(omega * z - kx * x - ky * y) * kx * ky * omega;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                for (int k = 0; k < zval.length; k++) {
                    d3FdXdYdZ[i][j][k] = d3FdXdYdZ_f.value(xval[i], yval[j], zval[k]);
                }
            }
        }

        TrivariateRealFunction tcf = new TricubicSplineInterpolatingFunction(xval, yval, zval,
                                                                             fval, dFdX, dFdY, dFdZ,
                                                                             d2FdXdY, d2FdXdZ, d2FdYdZ,
                                                                             d3FdXdYdZ);
        double x, y, z;
        double expected, result;
        
        x = 4;
        y = -3;
        z = 0;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-14);

        x = 4.5;
        y = -1.5;
        z = -4.25;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 0.1);

        x = 3.5;
        y = -3.5;
        z = -10;
        expected = f.value(x, y, z);
        result = tcf.value(x, y, z);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 0.1);
    }
}
