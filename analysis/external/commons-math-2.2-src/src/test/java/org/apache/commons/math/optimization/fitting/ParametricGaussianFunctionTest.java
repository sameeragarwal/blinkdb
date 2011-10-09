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

package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.CurveFitter;
import org.apache.commons.math.optimization.general.
       LevenbergMarquardtOptimizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ParametricGaussianFunction}.
 *
 * @since 2.2
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public class ParametricGaussianFunctionTest {
    /** Dataset 1 used by some test cases. */
    protected static final double[][] DATASET1 = new double[][] {
        {4.0254623,  531026.0},
        {4.02804905, 664002.0},
        {4.02934242, 787079.0},
        {4.03128248, 984167.0},
        {4.03386923, 1294546.0},
        {4.03580929, 1560230.0},
        {4.03839603, 1887233.0},
        {4.0396894,  2113240.0},
        {4.04162946, 2375211.0},
        {4.04421621, 2687152.0},
        {4.04550958, 2862644.0},
        {4.04744964, 3078898.0},
        {4.05003639, 3327238.0},
        {4.05132976, 3461228.0},
        {4.05326982, 3580526.0},
        {4.05585657, 3576946.0},
        {4.05779662, 3439750.0},
        {4.06038337, 3220296.0},
        {4.06167674, 3070073.0},
        {4.0636168,  2877648.0},
        {4.06620355, 2595848.0},
        {4.06749692, 2390157.0},
        {4.06943698, 2175960.0},
        {4.07202373, 1895104.0},
        {4.0733171,  1687576.0},
        {4.07525716, 1447024.0},
        {4.0778439,  1130879.0},
        {4.07978396, 904900.0},
        {4.08237071, 717104.0},
        {4.08366408, 620014.0}
    };

    /**
     * Using not-so-good initial parameters.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit01()
    throws OptimizationException, FunctionEvaluationException {
        CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer());
        addDatasetToCurveFitter(DATASET1, fitter);
        double[] parameters = fitter.fit(new ParametricGaussianFunction(),
                                         new double[] {8.64753e3, 3.483323e6, 4.06322, 1.946857e-2});
        assertEquals(99200.94715858076, parameters[0], 1e-4);
        assertEquals(3410515.221897707, parameters[1], 1e-4);
        assertEquals(4.054928275257894, parameters[2], 1e-4);
        assertEquals(0.014609868499860, parameters[3], 1e-4);
    }

    /**
     * Using eye-balled guesses for initial parameters.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit02()
    throws OptimizationException, FunctionEvaluationException {
        CurveFitter fitter = new CurveFitter(new LevenbergMarquardtOptimizer());
        addDatasetToCurveFitter(DATASET1, fitter);
        double[] parameters = fitter.fit(new ParametricGaussianFunction(),
                                         new double[] {500000.0, 3500000.0, 4.055, 0.025479654});
        assertEquals(99200.81836264656, parameters[0], 1e-4);
        assertEquals(3410515.327151986, parameters[1], 1e-4);
        assertEquals(4.054928275377392, parameters[2], 1e-4);
        assertEquals(0.014609869119806, parameters[3], 1e-4);
    }

    /**
     * The parameters array is null.
     *
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test(expected=IllegalArgumentException.class)
    public void testValue01() throws FunctionEvaluationException {
        ParametricGaussianFunction f = new ParametricGaussianFunction();
        f.value(0.0, null);
    }

    /**
     * The parameters array length is not 4.
     *
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test(expected=IllegalArgumentException.class)
    public void testValue02() throws FunctionEvaluationException {
        ParametricGaussianFunction f = new ParametricGaussianFunction();
        f.value(0.0, new double[] {0.0, 1.0});
    }

    /**
     * The parameters d is 0.
     *
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test(expected=ZeroException.class)
    public void testValue03() throws FunctionEvaluationException {
        ParametricGaussianFunction f = new ParametricGaussianFunction();
        f.value(0.0, new double[] {0.0, 1.0, 1.0, 0.0});
    }

    /**
     * Adds the specified points to specified <code>CurveFitter</code> instance.
     *
     * @param points data points where first dimension is a point index and
     *        second dimension is an array of length two representing the point
     *        with the first value corresponding to X and the second value
     *        corresponding to Y
     * @param fitter fitter to which the points in <code>points</code> should be
     *        added as observed points
     */
    protected static void addDatasetToCurveFitter(double[][] points,
                                                  CurveFitter fitter) {
        for (int i = 0; i < points.length; i++) {
            fitter.addObservedPoint(points[i][0], points[i][1]);
        }
    }
}
