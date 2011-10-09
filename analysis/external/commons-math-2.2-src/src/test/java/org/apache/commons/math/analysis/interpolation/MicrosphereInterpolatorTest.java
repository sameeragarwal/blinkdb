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
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testcase for the "microsphere projection" interpolator.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public final class MicrosphereInterpolatorTest {
    /**
     * Test of interpolator for a plane.
     * <p>
     * y = 2 x<sub>1</sub> - 3 x<sub>2</sub> + 5
     */
    @Test
    public void testLinearFunction2D() throws MathException {
        MultivariateRealFunction f = new MultivariateRealFunction() {
                public double value(double[] x) {
                    if (x.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    return 2 * x[0] - 3 * x[1] + 5;
                }
            };

        MultivariateRealInterpolator interpolator = new MicrosphereInterpolator();

        // Interpolating points in [-1, 1][-1, 1] by steps of 1.
        final int n = 9;
        final int dim = 2;
        double[][] x = new double[n][dim];
        double[] y = new double[n];
        int index = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                x[index][0] = i;
                x[index][1] = j;
                y[index] = f.value(x[index]);
                ++index;
            }
        }

        MultivariateRealFunction p = interpolator.interpolate(x, y);

        double[] c = new double[dim];
        double expected, result;

        c[0] = 0;
        c[1] = 0;
        expected = f.value(c);
        result = p.value(c);
        Assert.assertEquals("On sample point", expected, result, FastMath.ulp(1d));

        c[0] = 0 + 1e-5;
        c[1] = 1 - 1e-5;
        expected = f.value(c);
        result = p.value(c);
        Assert.assertEquals("1e-5 away from sample point", expected, result, 1e-4);
    }

    /**
     * Test of interpolator for a quadratic function.
     * <p>
     * y = 2 x<sub>1</sub><sup>2</sup> - 3 x<sub>2</sub><sup>2</sup>
     *     + 4 x<sub>1</sub> x<sub>2</sub> - 5
     */
    @Test
    public void testParaboloid2D() throws MathException {
        MultivariateRealFunction f = new MultivariateRealFunction() {
                public double value(double[] x) {
                    if (x.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    return 2 * x[0] * x[0] - 3 * x[1] * x[1] + 4 * x[0] * x[1] - 5;
                }
            };

        MultivariateRealInterpolator interpolator = new MicrosphereInterpolator();

        // Interpolating points in [-10, 10][-10, 10] by steps of 2.
        final int n = 121;
        final int dim = 2;
        double[][] x = new double[n][dim];
        double[] y = new double[n];
        int index = 0;
        for (int i = -10; i <= 10; i += 2) {
            for (int j = -10; j <= 10; j += 2) {
                x[index][0] = i;
                x[index][1] = j;
                y[index] = f.value(x[index]);
                ++index;
            }
        }

        MultivariateRealFunction p = interpolator.interpolate(x, y);

        double[] c = new double[dim];
        double expected, result;

        c[0] = 0;
        c[1] = 0;
        expected = f.value(c);
        result = p.value(c);
        Assert.assertEquals("On sample point", expected, result, FastMath.ulp(1d));

        c[0] = 2 + 1e-5;
        c[1] = 2 - 1e-5;
        expected = f.value(c);
        result = p.value(c);
        Assert.assertEquals("1e-5 away from sample point", expected, result, 1e-3);
    }
}
