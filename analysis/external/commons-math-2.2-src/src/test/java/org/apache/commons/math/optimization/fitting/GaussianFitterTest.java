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

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.general.
       LevenbergMarquardtOptimizer;
import org.junit.Test;

/**
 * Tests {@link GaussianFitter}.
 *
 * @since 2.2
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public class GaussianFitterTest {
    /** Good data. */
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
    /** Poor data: right of peak not symmetric with left of peak. */
    protected static final double[][] DATASET2 = new double[][] {
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0},
        {-13.15, 275363.0},
        {-12.65, 258014.0},
        {-12.15, 225000.0},
        {-11.65, 200000.0},
        {-11.15, 190000.0},
        {-10.65, 185000.0},
        {-10.15, 180000.0},
        { -9.65, 179000.0},
        { -9.15, 178000.0},
        { -8.65, 177000.0},
        { -8.15, 176000.0},
        { -7.65, 175000.0},
        { -7.15, 174000.0},
        { -6.65, 173000.0},
        { -6.15, 172000.0},
        { -5.65, 171000.0},
        { -5.15, 170000.0}
    };
    /** Poor data: long tails. */
    protected static final double[][] DATASET3 = new double[][] {
        {-90.15,   1513.0},
        {-80.15,   1514.0},
        {-70.15,   1513.0},
        {-60.15,   1514.0},
        {-50.15,   1513.0},
        {-40.15,   1514.0},
        {-30.15,   1513.0},
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0},
        {-13.15, 275363.0},
        {-12.65, 258014.0},
        {-12.15, 214073.0},
        {-11.65, 182244.0},
        {-11.15, 136419.0},
        {-10.65,  97823.0},
        {-10.15,  58930.0},
        { -9.65,  35404.0},
        { -9.15,  16120.0},
        { -8.65,   9823.0},
        { -8.15,   5064.0},
        { -7.65,   2575.0},
        { -7.15,   1642.0},
        { -6.65,   1101.0},
        { -6.15,    812.0},
        { -5.65,    690.0},
        { -5.15,    565.0},
        {  5.15,    564.0},
        { 15.15,    565.0},
        { 25.15,    564.0},
        { 35.15,    565.0},
        { 45.15,    564.0},
        { 55.15,    565.0},
        { 65.15,    564.0},
        { 75.15,    565.0}
    };
    /** Poor data: right of peak is missing. */
    protected static final double[][] DATASET4 = new double[][] {
        {-20.15,   1523.0},
        {-19.65,   1566.0},
        {-19.15,   1592.0},
        {-18.65,   1927.0},
        {-18.15,   3089.0},
        {-17.65,   6068.0},
        {-17.15,  14239.0},
        {-16.65,  34124.0},
        {-16.15,  64097.0},
        {-15.65, 110352.0},
        {-15.15, 164742.0},
        {-14.65, 209499.0},
        {-14.15, 267274.0},
        {-13.65, 283290.0}
    };
    /** Good data, but few points. */
    protected static final double[][] DATASET5 = new double[][] {
        {4.0254623,  531026.0},
        {4.03128248, 984167.0},
        {4.03839603, 1887233.0},
        {4.04421621, 2687152.0},
        {4.05132976, 3461228.0},
        {4.05326982, 3580526.0},
        {4.05779662, 3439750.0},
        {4.0636168,  2877648.0},
        {4.06943698, 2175960.0},
        {4.07525716, 1447024.0},
        {4.08237071, 717104.0},
        {4.08366408, 620014.0}
    };

    /**
     * Basic.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit01()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(DATASET1, fitter);
        GaussianFunction fitFunction = fitter.fit();
        assertEquals(99200.86969833552, fitFunction.getA(), 1e-4);
        assertEquals(3410515.285208688, fitFunction.getB(), 1e-4);
        assertEquals(4.054928275302832, fitFunction.getC(), 1e-4);
        assertEquals(0.014609868872574, fitFunction.getD(), 1e-4);
    }

    /**
     * Zero points is not enough observed points.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFit02()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        fitter.fit();
    }

    /**
     * Two points is not enough observed points.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFit03()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(new double[][] {
            {4.0254623,  531026.0},
            {4.02804905, 664002.0}},
            fitter);
        fitter.fit();
    }

    /**
     * Poor data: right of peak not symmetric with left of peak.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit04()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(DATASET2, fitter);
        GaussianFunction fitFunction = fitter.fit();
        assertEquals(-256534.689445631, fitFunction.getA(), 1e-4);
        assertEquals(481328.2181530679, fitFunction.getB(), 1e-4);
        assertEquals(-10.5217226891099, fitFunction.getC(), 1e-4);
        assertEquals(-7.64248239366800, fitFunction.getD(), 1e-4);
    }

    /**
     * Poor data: long tails.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit05()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(DATASET3, fitter);
        GaussianFunction fitFunction = fitter.fit();
        assertEquals(491.6310079258938, fitFunction.getA(), 1e-4);
        assertEquals(283508.6800413632, fitFunction.getB(), 1e-4);
        assertEquals(-13.2966857238057, fitFunction.getC(), 1e-4);
        assertEquals(1.725590356962981, fitFunction.getD(), 1e-4);
    }

    /**
     * Poor data: right of peak is missing.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit06()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(DATASET4, fitter);
        GaussianFunction fitFunction = fitter.fit();
        assertEquals(530.3649792355617, fitFunction.getA(), 1e-4);
        assertEquals(284517.0835567514, fitFunction.getB(), 1e-4);
        assertEquals(-13.5355534565105, fitFunction.getC(), 1e-4);
        assertEquals(1.512353018625465, fitFunction.getD(), 1e-4);
    }

    /**
     * Basic with smaller dataset.
     *
     * @throws OptimizationException in the event of a test case error
     * @throws FunctionEvaluationException in the event of a test case error
     */
    @Test
    public void testFit07()
    throws OptimizationException, FunctionEvaluationException {
        GaussianFitter fitter = new GaussianFitter(new LevenbergMarquardtOptimizer());
        addDatasetToGaussianFitter(DATASET5, fitter);
        GaussianFunction fitFunction = fitter.fit();
        assertEquals(176748.1400947575, fitFunction.getA(), 1e-4);
        assertEquals(3361537.018813906, fitFunction.getB(), 1e-4);
        assertEquals(4.054949992747176, fitFunction.getC(), 1e-4);
        assertEquals(0.014192380137002, fitFunction.getD(), 1e-4);
    }

    /**
     * Adds the specified points to specified <code>GaussianFitter</code>
     * instance.
     *
     * @param points data points where first dimension is a point index and
     *        second dimension is an array of length two representing the point
     *        with the first value corresponding to X and the second value
     *        corresponding to Y
     * @param fitter fitter to which the points in <code>points</code> should be
     *        added as observed points
     */
    protected static void addDatasetToGaussianFitter(double[][] points,
                                                     GaussianFitter fitter) {
        for (int i = 0; i < points.length; i++) {
            fitter.addObservedPoint(points[i][0], points[i][1]);
        }
    }
}
