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
package org.apache.commons.math.optimization.univariate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.UnivariateRealOptimizer;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

/**
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (Sat, 05 Sep 2009) $
 */
public final class BrentOptimizerTest {

    @Test
    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        minimizer.setMaxEvaluations(200);
        assertEquals(200, minimizer.getMaxEvaluations());
        try {
            minimizer.getResult();
            fail("an exception should have been thrown");
        } catch (NoDataException ise) {
            // expected
        }
        assertEquals(3 * FastMath.PI / 2, minimizer.optimize(f, GoalType.MINIMIZE, 4, 5), 10 * minimizer.getRelativeAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
        assertEquals(3 * FastMath.PI / 2, minimizer.optimize(f, GoalType.MINIMIZE, 1, 5), 10 * minimizer.getRelativeAccuracy());
        assertTrue(minimizer.getIterationCount() <= 50);
        assertTrue(minimizer.getEvaluations()    <= 100);
        assertTrue(minimizer.getEvaluations()    >=  15);
        minimizer.setMaxEvaluations(10);
        try {
            minimizer.optimize(f, GoalType.MINIMIZE, 4, 5);
            fail("an exception should have been thrown");
        } catch (FunctionEvaluationException mue) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }

    @Test
    public void testQuinticMin() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        assertEquals(-0.27195613, minimizer.optimize(f, GoalType.MINIMIZE, -0.3, -0.2), 1.0e-8);
        assertEquals( 0.82221643, minimizer.optimize(f, GoalType.MINIMIZE,  0.3,  0.9), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);

        // search in a large interval
        assertEquals(-0.27195613, minimizer.optimize(f, GoalType.MINIMIZE, -1.0, 0.2), 1.0e-8);
        assertTrue(minimizer.getIterationCount() <= 50);
    }

    @Test
    public void testQuinticMinStatistics() throws MathException {
        // The function has local minima at -0.27195613 and 0.82221643.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        minimizer.setRelativeAccuracy(1e-10);
        minimizer.setAbsoluteAccuracy(1e-11);

        final DescriptiveStatistics[] stat = new DescriptiveStatistics[3];
        for (int i = 0; i < stat.length; i++) {
            stat[i] = new DescriptiveStatistics();
        }

        final double min = -0.75;
        final double max = 0.25;
        final int nSamples = 200;
        final double delta = (max - min) / nSamples;
        for (int i = 0; i < nSamples; i++) {
            final double start = min + i * delta;
            stat[0].addValue(minimizer.optimize(f, GoalType.MINIMIZE, min, max, start));
            stat[1].addValue(minimizer.getIterationCount());
            stat[2].addValue(minimizer.getEvaluations());
        }

        final double meanOptValue = stat[0].getMean();
        final double medianIter = stat[1].getPercentile(50);
        final double medianEval = stat[2].getPercentile(50);
        assertTrue(meanOptValue > -0.27195612812 && meanOptValue < -0.27195612811);
        assertEquals(medianIter, 17, FastMath.ulp(1d));
        assertEquals(medianEval, 18, FastMath.ulp(1d));
    }

    @Test
    public void testQuinticMax() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has a local maximum at 0.27195613.
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer minimizer = new BrentOptimizer();
        assertEquals(0.27195613, minimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3), 1.0e-8);
        minimizer.setMaximalIterationCount(5);
        try {
            minimizer.optimize(f, GoalType.MAXIMIZE, 0.2, 0.3);
            fail("an exception should have been thrown");
        } catch (MaxIterationsExceededException miee) {
            // expected
        }
    }

    @Test
    public void testMinEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer solver = new BrentOptimizer();

        solver.setRelativeAccuracy(1e-8);

        // endpoint is minimum
        double result = solver.optimize(f, GoalType.MINIMIZE, 3 * FastMath.PI / 2, 5);
        assertEquals(3 * FastMath.PI / 2, result, 10 * solver.getRelativeAccuracy());

        result = solver.optimize(f, GoalType.MINIMIZE, 4, 3 * FastMath.PI / 2);
        assertEquals(3 * FastMath.PI / 2, result, 10 * solver.getRelativeAccuracy());
    }
}
