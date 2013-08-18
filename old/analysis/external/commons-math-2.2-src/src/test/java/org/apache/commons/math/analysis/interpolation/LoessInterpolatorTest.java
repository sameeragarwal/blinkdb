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
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test of the LoessInterpolator class.
 */
public class LoessInterpolatorTest {

    @Test
    public void testOnOnePoint() throws MathException {
        double[] xval = {0.5};
        double[] yval = {0.7};
        double[] res = new LoessInterpolator().smooth(xval, yval);
        Assert.assertEquals(1, res.length);
        Assert.assertEquals(0.7, res[0], 0.0);
    }

    @Test
    public void testOnTwoPoints() throws MathException {
        double[] xval = {0.5, 0.6};
        double[] yval = {0.7, 0.8};
        double[] res = new LoessInterpolator().smooth(xval, yval);
        Assert.assertEquals(2, res.length);
        Assert.assertEquals(0.7, res[0], 0.0);
        Assert.assertEquals(0.8, res[1], 0.0);
    }

    @Test
    public void testOnStraightLine() throws MathException {
        double[] xval = {1,2,3,4,5};
        double[] yval = {2,4,6,8,10};
        LoessInterpolator li = new LoessInterpolator(0.6, 2, 1e-12);
        double[] res = li.smooth(xval, yval);
        Assert.assertEquals(5, res.length);
        for(int i = 0; i < 5; ++i) {
            Assert.assertEquals(yval[i], res[i], 1e-8);
        }
    }

    @Test
    public void testOnDistortedSine() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.2;

        generateSineData(xval, yval, xnoise, ynoise);

        LoessInterpolator li = new LoessInterpolator(0.3, 4, 1e-12);

        double[] res = li.smooth(xval, yval);

        // Check that the resulting curve differs from
        // the "real" sine less than the jittered one

        double noisyResidualSum = 0;
        double fitResidualSum = 0;

        for(int i = 0; i < numPoints; ++i) {
            double expected = FastMath.sin(xval[i]);
            double noisy = yval[i];
            double fit = res[i];

            noisyResidualSum += FastMath.pow(noisy - expected, 2);
            fitResidualSum += FastMath.pow(fit - expected, 2);
        }

        Assert.assertTrue(fitResidualSum < noisyResidualSum);
    }

    @Test
    public void testIncreasingBandwidthIncreasesSmoothness() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.1;

        generateSineData(xval, yval, xnoise, ynoise);

        // Check that variance decreases as bandwidth increases

        double[] bandwidths = {0.1, 0.5, 1.0};
        double[] variances = new double[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            double bw = bandwidths[i];

            LoessInterpolator li = new LoessInterpolator(bw, 4, 1e-12);

            double[] res = li.smooth(xval, yval);

            for (int j = 1; j < res.length; ++j) {
                variances[i] += FastMath.pow(res[j] - res[j-1], 2);
            }
        }

        for(int i = 1; i < variances.length; ++i) {
            Assert.assertTrue(variances[i] < variances[i-1]);
        }
    }

    @Test
    public void testIncreasingRobustnessItersIncreasesSmoothnessWithOutliers() throws MathException {
        int numPoints = 100;
        double[] xval = new double[numPoints];
        double[] yval = new double[numPoints];
        double xnoise = 0.1;
        double ynoise = 0.1;

        generateSineData(xval, yval, xnoise, ynoise);

        // Introduce a couple of outliers
        yval[numPoints/3] *= 100;
        yval[2 * numPoints/3] *= -100;

        // Check that variance decreases as the number of robustness
        // iterations increases

        double[] variances = new double[4];
        for (int i = 0; i < 4; i++) {
            LoessInterpolator li = new LoessInterpolator(0.3, i, 1e-12);

            double[] res = li.smooth(xval, yval);

            for (int j = 1; j < res.length; ++j) {
                variances[i] += FastMath.abs(res[j] - res[j-1]);
            }
        }

        for(int i = 1; i < variances.length; ++i) {
            Assert.assertTrue(variances[i] < variances[i-1]);
        }
    }

    @Test(expected=MathException.class)
    public void testUnequalSizeArguments() throws MathException {
        new LoessInterpolator().smooth(new double[] {1,2,3}, new double[] {1,2,3,4});
    }

    @Test(expected=MathException.class)
    public void testEmptyData() throws MathException {
        new LoessInterpolator().smooth(new double[] {}, new double[] {});
    }

    @Test(expected=MathException.class)
    public void testNonStrictlyIncreasing1() throws MathException {
        new LoessInterpolator().smooth(new double[] {4,3,1,2}, new double[] {3,4,5,6});
    }

    @Test(expected=MathException.class)
    public void testNonStrictlyIncreasing2() throws MathException {
        new LoessInterpolator().smooth(new double[] {1,2,2,3}, new double[] {3,4,5,6});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal1() throws MathException {
        new LoessInterpolator().smooth(new double[] {1,2,Double.NaN}, new double[] {3,4,5});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal2() throws MathException {
        new LoessInterpolator().smooth(new double[] {1,2,Double.POSITIVE_INFINITY}, new double[] {3,4,5});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal3() throws MathException {
        new LoessInterpolator().smooth(new double[] {1,2,Double.NEGATIVE_INFINITY}, new double[] {3,4,5});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal4() throws MathException {
        new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.NaN});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal5() throws MathException {
        new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.POSITIVE_INFINITY});
    }

    @Test(expected=MathException.class)
    public void testNotAllFiniteReal6() throws MathException {
        new LoessInterpolator().smooth(new double[] {3,4,5}, new double[] {1,2,Double.NEGATIVE_INFINITY});
    }

    @Test(expected=MathException.class)
    public void testInsufficientBandwidth() throws MathException {
        LoessInterpolator li = new LoessInterpolator(0.1, 3, 1e-12);
        li.smooth(new double[] {1,2,3,4,5,6,7,8,9,10,11,12}, new double[] {1,2,3,4,5,6,7,8,9,10,11,12});
    }

    @Test(expected=MathException.class)
    public void testCompletelyIncorrectBandwidth1() throws MathException {
        new LoessInterpolator(-0.2, 3, 1e-12);
    }

    @Test(expected=MathException.class)
    public void testCompletelyIncorrectBandwidth2() throws MathException {
        new LoessInterpolator(1.1, 3, 1e-12);
    }

    @Test
    public void testMath296withoutWeights() throws MathException {
        double[] xval = {
                0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
                 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0};
        double[] yval = {
                0.47, 0.48, 0.55, 0.56, -0.08, -0.04, -0.07, -0.07,
                -0.56, -0.46, -0.56, -0.52, -3.03, -3.08, -3.09,
                -3.04, 3.54, 3.46, 3.36, 3.35};
        // Output from R, rounded to .001
        double[] yref = {
                0.461, 0.499, 0.541, 0.308, 0.175, -0.042, -0.072,
                -0.196, -0.311, -0.446, -0.557, -1.497, -2.133,
                -3.08, -3.09, -0.621, 0.982, 3.449, 3.389, 3.336
        };
        LoessInterpolator li = new LoessInterpolator(0.3, 4, 1e-12);
        double[] res = li.smooth(xval, yval);
        Assert.assertEquals(xval.length, res.length);
        for(int i = 0; i < res.length; ++i) {
            Assert.assertEquals(yref[i], res[i], 0.02);
        }
    }

    private void generateSineData(double[] xval, double[] yval, double xnoise, double ynoise) {
        double dx = 2 * FastMath.PI / xval.length;
        double x = 0;
        for(int i = 0; i < xval.length; ++i) {
            xval[i] = x;
            yval[i] = FastMath.sin(x) + (2 * FastMath.random() - 1) * ynoise;
            x += dx * (1 + (2 * FastMath.random() - 1) * xnoise);
        }
    }

}
