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
import org.apache.commons.math.MathException;
import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for the bicubic function.
 * 
 * @version $Revision: 821626 $ $Date: 2009-10-04 23:57:30 +0200 (Sun, 04 Oct 2009) $ 
 */
public final class BicubicSplineInterpolatingFunctionTest {
    /**
     * Test preconditions.
     */
    @Test
    public void testPreconditions() throws MathException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2.5};
        double[][] zval = new double[xval.length][yval.length];

        @SuppressWarnings("unused")
        BivariateRealFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           zval, zval, zval);
        
        double[] wxval = new double[] {3, 2, 5, 6.5};
        try {
            bcf = new BicubicSplineInterpolatingFunction(wxval, yval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        double[] wyval = new double[] {-4, -1, -1, 2.5};
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, wyval, zval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        double[][] wzval = new double[xval.length][yval.length - 1];
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }

        wzval = new double[xval.length - 1][yval.length];
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, wzval, zval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, wzval, zval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, wzval, zval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        try {
            bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval, zval, zval, wzval);
            Assert.fail("an exception should have been thrown");
        } catch (DimensionMismatchException e) {
            // Expected
        }
        Assert.assertNotNull(bcf); // Avoid Findbugs "dead store" warning
    }

    /**
     * Test for a plane.
     * <p>
     * z = 2 x - 3 y + 5
     */
    @Test
    public void testPlane() throws MathException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        // Function values
        BivariateRealFunction f = new BivariateRealFunction() {
                public double value(double x, double y) {
                    return 2 * x - 3 * y + 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = 2;
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = -3;
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 0;
            }
        }

        BivariateRealFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           dZdX, dZdY, dZdXdY);
        double x, y;
        double expected, result;

        x = 4;
        y = -3;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 0.3);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 0.3);
    }

    /**
     * Test for a paraboloid.
     * <p>
     * z = 2 x<sup>2</sup> - 3 y<sup>2</sup> + 4 x y - 5
     */
    @Test
    public void testParaboloid() throws MathException {
        double[] xval = new double[] {3, 4, 5, 6.5};
        double[] yval = new double[] {-4, -3, -1, 2, 2.5};
        // Function values
        BivariateRealFunction f = new BivariateRealFunction() {
                public double value(double x, double y) {
                    return 2 * x * x - 3 * y * y + 4 * x * y - 5;
                }
            };
        double[][] zval = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                zval[i][j] = f.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to x
        double[][] dZdX = new double[xval.length][yval.length];
        BivariateRealFunction dfdX = new BivariateRealFunction() {
                public double value(double x, double y) {
                    return 4 * (x + y);
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdX[i][j] = dfdX.value(xval[i], yval[j]);
            }
        }
        // Partial derivatives with respect to y
        double[][] dZdY = new double[xval.length][yval.length];
        BivariateRealFunction dfdY = new BivariateRealFunction() {
                public double value(double x, double y) {
                    return 4 * x - 6 * y;
                }
            };
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdY[i][j] = dfdY.value(xval[i], yval[j]);
            }
        }
        // Partial cross-derivatives
        double[][] dZdXdY = new double[xval.length][yval.length];
        for (int i = 0; i < xval.length; i++) {
            for (int j = 0; j < yval.length; j++) {
                dZdXdY[i][j] = 4;
            }
        }

        BivariateRealFunction bcf = new BicubicSplineInterpolatingFunction(xval, yval, zval,
                                                                           dZdX, dZdY, dZdXdY);
        double x, y;
        double expected, result;
        
        x = 4;
        y = -3;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("On sample point",
                            expected, result, 1e-15);

        x = 4.5;
        y = -1.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (middle of the patch)",
                            expected, result, 2);

        x = 3.5;
        y = -3.5;
        expected = f.value(x, y);
        result = bcf.value(x, y);
        Assert.assertEquals("Half-way between sample points (border of the patch)",
                            expected, result, 2);
    }

    /**
     * Test for partial derivatives of {@link BicubicSplineFunction}.
     * <p>
     * f(x, y) = &Sigma;<sub>i</sub>&Sigma;<sub>j</sub> (i+1) (j+2) x<sup>i</sup> y<sup>j</sup>
     */
    @Test
    public void testSplinePartialDerivatives() throws FunctionEvaluationException {
        final int N = 4;
        final double[] coeff = new double[16];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                coeff[i + N * j] = (i + 1) * (j + 2);
            }
        }

        final BicubicSplineFunction f = new BicubicSplineFunction(coeff);
        BivariateRealFunction derivative;
        final double x = 0.435;
        final double y = 0.776;
        final double tol = 1e-13;

        derivative = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double yFactor = 2 + 3 * y + 4 * y2 + 5 * y3;
                    return yFactor * (2 + 6 * x + 12 * x2);
                }
            };
        Assert.assertEquals("dFdX", derivative.value(x, y),
                            f.partialDerivativeX().value(x, y), tol);
        
        derivative = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double y2 = y * y;
                    final double xFactor = 1 + 2 * x + 3 * x2 + 4 * x3;
                    return xFactor * (3 + 8 * y + 15 * y2);
                }
            };
        Assert.assertEquals("dFdY", derivative.value(x, y),
                            f.partialDerivativeY().value(x, y), tol);

        derivative = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double y2 = y * y;
                    final double y3 = y2 * y;
                    final double yFactor = 2 + 3 * y + 4 * y2 + 5 * y3;
                    return yFactor * (6 + 24 * x);
                }
            };
        Assert.assertEquals("d2FdX2", derivative.value(x, y),
                            f.partialDerivativeXX().value(x, y), tol);

        derivative = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double xFactor = 1 + 2 * x + 3 * x2 + 4 * x3;
                    return xFactor * (8 + 30 * y);
                }
            };
        Assert.assertEquals("d2FdY2", derivative.value(x, y),
                            f.partialDerivativeYY().value(x, y), tol);

        derivative = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;
                    final double yFactor = 3 + 8 * y + 15 * y2;
                    return yFactor * (2 + 6 * x + 12 * x2);
                }
            };
        Assert.assertEquals("d2FdXdY", derivative.value(x, y),
                            f.partialDerivativeXY().value(x, y), tol);
    }

    /**
     * Test that the partial derivatives computed from a
     * {@link BicubicSplineInterpolatingFunction} match the input data.
     * <p>
     * f(x, y) = 5
     *           - 3 x + 2 y
     *           - x y + 2 x<sup>2</sup> - 3 y<sup>2</sup>
     *           + 4 x<sup>2</sup> y - x y<sup>2</sup> - 3 x<sup>3</sup> + y<sup>3</sup>
     */
    @Test
    public void testMatchingPartialDerivatives() throws MathException {
        final int sz = 21;
        double[] val = new double[sz];
        // Coordinate values
        final double delta = 1d / (sz - 1);
        for (int i = 0; i < sz; i++) {
            val[i] = i * delta;
        }
        // Function values
        BivariateRealFunction f = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double x3 = x2 * x;
                    final double y2 = y * y;
                    final double y3 = y2 * y;

                    return 5
                        - 3 * x + 2 * y
                        - x * y + 2 * x2 - 3 * y2
                        + 4 * x2 * y - x * y2 - 3 * x3 + y3;
                }
            };
        double[][] fval = new double[sz][sz];
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                fval[i][j] = f.value(val[i], val[j]);
            }
        }
        // Partial derivatives with respect to x
        double[][] dFdX = new double[sz][sz];
        BivariateRealFunction dfdX = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;                    
                    return - 3 - y + 4 * x + 8 * x * y - y2 - 9 * x2;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                dFdX[i][j] = dfdX.value(val[i], val[j]);
            }
        }
        // Partial derivatives with respect to y
        double[][] dFdY = new double[sz][sz];
        BivariateRealFunction dfdY = new BivariateRealFunction() {
                public double value(double x, double y) {
                    final double x2 = x * x;
                    final double y2 = y * y;                    
                    return 2 - x - 6 * y + 4 * x2 - 2 * x * y + 3 * y2;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                dFdY[i][j] = dfdY.value(val[i], val[j]);
            }
        }
        // Partial cross-derivatives
        double[][] d2FdXdY = new double[sz][sz];
        BivariateRealFunction d2fdXdY = new BivariateRealFunction() {
                public double value(double x, double y) {
                    return -1 + 8 * x - 2 * y;
                }
            };
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz; j++) {
                d2FdXdY[i][j] = d2fdXdY.value(val[i], val[j]);
            }
        }

        BicubicSplineInterpolatingFunction bcf
            = new BicubicSplineInterpolatingFunction(val, val, fval, dFdX, dFdY, d2FdXdY);

        double x, y;
        double expected, result;

        final double tol = 1e-12;
        for (int i = 0; i < sz; i++) {
            x = val[i];
            for (int j = 0; j < sz; j++) {
                y = val[j];
                
                expected = dfdX.value(x, y);
                result = bcf.partialDerivativeX(x, y);
                Assert.assertEquals(x + " " + y + " dFdX", expected, result, tol);

                expected = dfdY.value(x, y);
                result = bcf.partialDerivativeY(x, y);
                Assert.assertEquals(x + " " + y + " dFdY", expected, result, tol);
                
                expected = d2fdXdY.value(x, y);
                result = bcf.partialDerivativeXY(x, y);
                Assert.assertEquals(x + " " + y + " d2FdXdY", expected, result, tol);
            }
        }
    }
}
