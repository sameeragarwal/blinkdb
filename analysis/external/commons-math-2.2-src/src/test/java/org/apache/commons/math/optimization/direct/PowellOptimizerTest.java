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
package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.SumSincFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link PowellOptimizer}.
 */
public class PowellOptimizerTest {

    @Test
    public void testSumSinc() throws MathException {
        final DifferentiableMultivariateRealFunction func = new SumSincFunction(-1);

        int dim = 2;
        final double[] minPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            minPoint[i] = 0;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i];
        }
        // doTest(func, minPoint, init,  GoalType.MINIMIZE, 1e-5, 1e-9, 1e-7);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i] + 3;
        }
        doTest(func, minPoint, init,  GoalType.MINIMIZE, 1e-5, 1e-9, 1e-7);
    }

    @Test
    public void testQuadratic() throws MathException {
        final DifferentiableMultivariateRealFunction func = new DifferentiableMultivariateRealFunction() {
                public double value(double[] x) {
                    final double a = x[0] - 1;
                    final double b = x[1] - 1;
                    return a * a + b * b + 1;
                }
                public MultivariateRealFunction partialDerivative(int k) {
                    return null; // Not used
                }
                public MultivariateVectorialFunction gradient() {
                    return null; // Not used
                }
            };

        int dim = 2;
        final double[] minPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            minPoint[i] = 1;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i];
        }
        doTest(func, minPoint, init,  GoalType.MINIMIZE, 1e-5, 1e-9, 1e-8);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = minPoint[i] - 20;
        }
        doTest(func, minPoint, init, GoalType.MINIMIZE, 1e-5, 1e-9, 1e-8);
    }

    @Test
    public void testMaximizeQuadratic() throws MathException {
        final DifferentiableMultivariateRealFunction func = new DifferentiableMultivariateRealFunction() {
                public double value(double[] x) {
                    final double a = x[0] - 1;
                    final double b = x[1] - 1;
                    return -a * a - b * b + 1;
                }
                public MultivariateRealFunction partialDerivative(int k) {
                    return null;  // Not used
                }
                public MultivariateVectorialFunction gradient() {
                    return null;  // Not used
                }
            };

        int dim = 2;
        final double[] maxPoint = new double[dim];
        for (int i = 0; i < dim; i++) {
            maxPoint[i] = 1;
        }

        double[] init = new double[dim];

        // Initial is minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = maxPoint[i];
        }
        doTest(func, maxPoint, init,  GoalType.MAXIMIZE, 1e-5, 1e-9, 1e-8);

        // Initial is far from minimum.
        for (int i = 0; i < dim; i++) {
            init[i] = maxPoint[i] - 20;
        }
        doTest(func, maxPoint, init, GoalType.MAXIMIZE, 1e-5, 1e-9, 1e-8);
    }

    /**
     * @param func Function to optimize.
     * @param optimum Expected optimum.
     * @param init Starting point.
     * @param goal Minimization or maximization.
     * @param xTol Tolerance (relative error on the objective function) for
     * "Brent" line search algorithm used by "Powell".
     * @param fTol Tolerance (relative error on the objective function) for
     * "Powell" algorithm.
     * @param pointTol Tolerance for checking that the optimum is correct.
     */
    private void doTest(DifferentiableMultivariateRealFunction func,
                        double[] optimum,
                        double[] init,
                        GoalType goal,
                        double xTol,
                        double fTol,
                        double pointTol)
        throws MathException {
        final PowellOptimizer optim = new PowellOptimizer(xTol);
        optim.setConvergenceChecker(new SimpleScalarValueChecker(fTol, -1));

        final RealPointValuePair result = optim.optimize(func, goal, init);
        final double[] found = result.getPoint();

        for (int i = 0, dim = optimum.length; i < dim; i++) {
            Assert.assertEquals(optimum[i], found[i], pointTol);
        }
    }
}
