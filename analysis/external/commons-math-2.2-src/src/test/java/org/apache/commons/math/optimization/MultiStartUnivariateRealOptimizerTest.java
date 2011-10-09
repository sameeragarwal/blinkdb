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

package org.apache.commons.math.optimization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class MultiStartUnivariateRealOptimizerTest {

    @Test
    public void testSinMin() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealOptimizer underlying = new BrentOptimizer();
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(44428400075l);
        MultiStartUnivariateRealOptimizer minimizer =
            new MultiStartUnivariateRealOptimizer(underlying, 10, g);
        minimizer.optimize(f, GoalType.MINIMIZE, -100.0, 100.0);
        double[] optima = minimizer.getOptima();
        double[] optimaValues = minimizer.getOptimaValues();
        for (int i = 1; i < optima.length; ++i) {
            double d = (optima[i] - optima[i-1]) / (2 * FastMath.PI);
            assertTrue (FastMath.abs(d - FastMath.rint(d)) < 1.0e-8);
            assertEquals(-1.0, f.value(optima[i]), 1.0e-10);
            assertEquals(f.value(optima[i]), optimaValues[i], 1.0e-10);
        }
        assertTrue(minimizer.getEvaluations() > 150);
        assertTrue(minimizer.getEvaluations() < 250);
    }

    @Test
    public void testQuinticMin() throws MathException {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealOptimizer underlying = new BrentOptimizer();
        underlying.setRelativeAccuracy(1e-15);
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(4312000053L);
        MultiStartUnivariateRealOptimizer minimizer =
            new MultiStartUnivariateRealOptimizer(underlying, 5, g);
        minimizer.setAbsoluteAccuracy(10 * minimizer.getAbsoluteAccuracy());
        minimizer.setRelativeAccuracy(10 * minimizer.getRelativeAccuracy());

        try {
            minimizer.getOptima();
            fail("an exception should have been thrown");
        } catch (IllegalStateException ise) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }
        try {
            minimizer.getOptimaValues();
            fail("an exception should have been thrown");
        } catch (IllegalStateException ise) {
            // expected
        } catch (Exception e) {
            fail("wrong exception caught");
        }

        double result = minimizer.optimize(f, GoalType.MINIMIZE, -0.3, -0.2);
        assertEquals(-0.2719561270319131, result, 1.0e-13);
        assertEquals(-0.2719561270319131, minimizer.getResult(), 1.0e-13);
        assertEquals(-0.04433426954946637, minimizer.getFunctionValue(), 1.0e-13);

        double[] optima = minimizer.getOptima();
        double[] optimaValues = minimizer.getOptimaValues();
        for (int i = 0; i < optima.length; ++i) {
            assertEquals(f.value(optima[i]), optimaValues[i], 1.0e-10);
        }
        assertTrue(minimizer.getEvaluations()    >= 120);
        assertTrue(minimizer.getEvaluations()    <= 170);
        assertTrue(minimizer.getIterationCount() >= 120);
        assertTrue(minimizer.getIterationCount() <= 170);
    }
}
