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

package org.apache.commons.math.optimization.general;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;

/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files.
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for
 * convenience, it is reproduced below.</p>

 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>

 * @author Argonne National Laboratory. MINPACK project. March 1980 (original fortran minpack tests)
 * @author Burton S. Garbow (original fortran minpack tests)
 * @author Kenneth E. Hillstrom (original fortran minpack tests)
 * @author Jorge J. More (original fortran minpack tests)
 * @author Luc Maisonobe (non-minpack tests and minpack tests Java translation)
 */
public class NonLinearConjugateGradientOptimizerTest
extends TestCase {

    public NonLinearConjugateGradientOptimizerTest(String name) {
        super(name);
    }

    public void testTrivial() throws Exception {
        LinearProblem problem =
            new LinearProblem(new double[][] { { 2 } }, new double[] { 3 });
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0 });
        assertEquals(1.5, optimum.getPoint()[0], 1.0e-10);
        assertEquals(0.0, optimum.getValue(), 1.0e-10);
    }

    public void testColumnsPermutation() throws Exception {

        LinearProblem problem =
            new LinearProblem(new double[][] { { 1.0, -1.0 }, { 0.0, 2.0 }, { 1.0, -2.0 } },
                              new double[] { 4.0, 6.0, 1.0 });

        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0, 0 });
        assertEquals(7.0, optimum.getPoint()[0], 1.0e-10);
        assertEquals(3.0, optimum.getPoint()[1], 1.0e-10);
        assertEquals(0.0, optimum.getValue(), 1.0e-10);

    }

    public void testNoDependency() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 2, 0, 0, 0, 0, 0 },
                { 0, 2, 0, 0, 0, 0 },
                { 0, 0, 2, 0, 0, 0 },
                { 0, 0, 0, 2, 0, 0 },
                { 0, 0, 0, 0, 2, 0 },
                { 0, 0, 0, 0, 0, 2 }
        }, new double[] { 0.0, 1.1, 2.2, 3.3, 4.4, 5.5 });
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0, 0, 0, 0, 0, 0 });
        for (int i = 0; i < problem.target.length; ++i) {
            assertEquals(0.55 * i, optimum.getPoint()[i], 1.0e-10);
        }
    }

    public void testOneSet() throws Exception {

        LinearProblem problem = new LinearProblem(new double[][] {
                {  1,  0, 0 },
                { -1,  1, 0 },
                {  0, -1, 1 }
        }, new double[] { 1, 1, 1});
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0, 0, 0 });
        assertEquals(1.0, optimum.getPoint()[0], 1.0e-10);
        assertEquals(2.0, optimum.getPoint()[1], 1.0e-10);
        assertEquals(3.0, optimum.getPoint()[2], 1.0e-10);

    }

    public void testTwoSets() throws Exception {
        final double epsilon = 1.0e-7;
        LinearProblem problem = new LinearProblem(new double[][] {
                {  2,  1,   0,  4,       0, 0 },
                { -4, -2,   3, -7,       0, 0 },
                {  4,  1,  -2,  8,       0, 0 },
                {  0, -3, -12, -1,       0, 0 },
                {  0,  0,   0,  0, epsilon, 1 },
                {  0,  0,   0,  0,       1, 1 }
        }, new double[] { 2, -9, 2, 2, 1 + epsilon * epsilon, 2});

        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setPreconditioner(new Preconditioner() {
            public double[] precondition(double[] point, double[] r) {
                double[] d = r.clone();
                d[0] /=  72.0;
                d[1] /=  30.0;
                d[2] /= 314.0;
                d[3] /= 260.0;
                d[4] /= 2 * (1 + epsilon * epsilon);
                d[5] /= 4.0;
                return d;
            }
        });
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-13, 1.0e-13));

        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0, 0, 0, 0, 0, 0 });
        assertEquals( 3.0, optimum.getPoint()[0], 1.0e-10);
        assertEquals( 4.0, optimum.getPoint()[1], 1.0e-10);
        assertEquals(-1.0, optimum.getPoint()[2], 1.0e-10);
        assertEquals(-2.0, optimum.getPoint()[3], 1.0e-10);
        assertEquals( 1.0 + epsilon, optimum.getPoint()[4], 1.0e-10);
        assertEquals( 1.0 - epsilon, optimum.getPoint()[5], 1.0e-10);

    }

    public void testNonInversible() throws Exception {

        LinearProblem problem = new LinearProblem(new double[][] {
                {  1, 2, -3 },
                {  2, 1,  3 },
                { -3, 0, -9 }
        }, new double[] { 1, 1, 1 });
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
                optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 0, 0, 0 });
        assertTrue(optimum.getValue() > 0.5);
    }

    public void testIllConditioned() throws Exception {
        LinearProblem problem1 = new LinearProblem(new double[][] {
                { 10.0, 7.0,  8.0,  7.0 },
                {  7.0, 5.0,  6.0,  5.0 },
                {  8.0, 6.0, 10.0,  9.0 },
                {  7.0, 5.0,  9.0, 10.0 }
        }, new double[] { 32, 23, 33, 31 });
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-13, 1.0e-13));
        BrentSolver solver = new BrentSolver();
        solver.setAbsoluteAccuracy(1.0e-15);
        solver.setRelativeAccuracy(1.0e-15);
        optimizer.setLineSearchSolver(solver);
        RealPointValuePair optimum1 =
            optimizer.optimize(problem1, GoalType.MINIMIZE, new double[] { 0, 1, 2, 3 });
        assertEquals(1.0, optimum1.getPoint()[0], 1.0e-5);
        assertEquals(1.0, optimum1.getPoint()[1], 1.0e-5);
        assertEquals(1.0, optimum1.getPoint()[2], 1.0e-5);
        assertEquals(1.0, optimum1.getPoint()[3], 1.0e-5);

        LinearProblem problem2 = new LinearProblem(new double[][] {
                { 10.00, 7.00, 8.10, 7.20 },
                {  7.08, 5.04, 6.00, 5.00 },
                {  8.00, 5.98, 9.89, 9.00 },
                {  6.99, 4.99, 9.00, 9.98 }
        }, new double[] { 32, 23, 33, 31 });
        RealPointValuePair optimum2 =
            optimizer.optimize(problem2, GoalType.MINIMIZE, new double[] { 0, 1, 2, 3 });
        assertEquals(-81.0, optimum2.getPoint()[0], 1.0e-1);
        assertEquals(137.0, optimum2.getPoint()[1], 1.0e-1);
        assertEquals(-34.0, optimum2.getPoint()[2], 1.0e-1);
        assertEquals( 22.0, optimum2.getPoint()[3], 1.0e-1);

    }

    public void testMoreEstimatedParametersSimple() throws Exception {

        LinearProblem problem = new LinearProblem(new double[][] {
                { 3.0, 2.0,  0.0, 0.0 },
                { 0.0, 1.0, -1.0, 1.0 },
                { 2.0, 0.0,  1.0, 0.0 }
        }, new double[] { 7.0, 3.0, 5.0 });

        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 7, 6, 5, 4 });
        assertEquals(0, optimum.getValue(), 1.0e-10);

    }

    public void testMoreEstimatedParametersUnsorted() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                 { 1.0, 1.0,  0.0,  0.0, 0.0,  0.0 },
                 { 0.0, 0.0,  1.0,  1.0, 1.0,  0.0 },
                 { 0.0, 0.0,  0.0,  0.0, 1.0, -1.0 },
                 { 0.0, 0.0, -1.0,  1.0, 0.0,  1.0 },
                 { 0.0, 0.0,  0.0, -1.0, 1.0,  0.0 }
        }, new double[] { 3.0, 12.0, -1.0, 7.0, 1.0 });
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 2, 2, 2, 2, 2, 2 });
        assertEquals(0, optimum.getValue(), 1.0e-10);
    }

    public void testRedundantEquations() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 5.0 });

        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 1, 1 });
        assertEquals(2.0, optimum.getPoint()[0], 1.0e-8);
        assertEquals(1.0, optimum.getPoint()[1], 1.0e-8);

    }

    public void testInconsistentEquations() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 4.0 });

        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-6, 1.0e-6));
        RealPointValuePair optimum =
            optimizer.optimize(problem, GoalType.MINIMIZE, new double[] { 1, 1 });
        assertTrue(optimum.getValue() > 0.1);

    }

    public void testCircleFitting() throws Exception {
        Circle circle = new Circle();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        NonLinearConjugateGradientOptimizer optimizer =
            new NonLinearConjugateGradientOptimizer(ConjugateGradientFormula.POLAK_RIBIERE);
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-30, 1.0e-30));
        BrentSolver solver = new BrentSolver();
        solver.setAbsoluteAccuracy(1.0e-13);
        solver.setRelativeAccuracy(1.0e-15);
        optimizer.setLineSearchSolver(solver);
        RealPointValuePair optimum =
            optimizer.optimize(circle, GoalType.MINIMIZE, new double[] { 98.680, 47.345 });
        Point2D.Double center = new Point2D.Double(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        assertEquals(69.960161753, circle.getRadius(center), 1.0e-8);
        assertEquals(96.075902096, center.x, 1.0e-8);
        assertEquals(48.135167894, center.y, 1.0e-8);
    }

    private static class LinearProblem implements DifferentiableMultivariateRealFunction, Serializable {

        private static final long serialVersionUID = 703247177355019415L;
        final RealMatrix factors;
        final double[] target;
        public LinearProblem(double[][] factors, double[] target) {
            this.factors = new BlockRealMatrix(factors);
            this.target  = target;
        }

        private double[] gradient(double[] point) {
            double[] r = factors.operate(point);
            for (int i = 0; i < r.length; ++i) {
                r[i] -= target[i];
            }
            double[] p = factors.transpose().operate(r);
            for (int i = 0; i < p.length; ++i) {
                p[i] *= 2;
            }
            return p;
        }

        public double value(double[] variables) throws FunctionEvaluationException {
            double[] y = factors.operate(variables);
            double sum = 0;
            for (int i = 0; i < y.length; ++i) {
                double ri = y[i] - target[i];
                sum += ri * ri;
            }
            return sum;
        }

        public MultivariateVectorialFunction gradient() {
            return new MultivariateVectorialFunction() {
                private static final long serialVersionUID = 2621997811350805819L;
                public double[] value(double[] point) {
                    return gradient(point);
                }
            };
        }

        public MultivariateRealFunction partialDerivative(final int k) {
            return new MultivariateRealFunction() {
                private static final long serialVersionUID = -6186178619133562011L;
                public double value(double[] point) {
                    return gradient(point)[k];
                }
            };
        }

    }

    private static class Circle implements DifferentiableMultivariateRealFunction, Serializable {

        private static final long serialVersionUID = -4711170319243817874L;

        private ArrayList<Point2D.Double> points;

        public Circle() {
            points  = new ArrayList<Point2D.Double>();
        }

        public void addPoint(double px, double py) {
            points.add(new Point2D.Double(px, py));
        }

        public double getRadius(Point2D.Double center) {
            double r = 0;
            for (Point2D.Double point : points) {
                r += point.distance(center);
            }
            return r / points.size();
        }

        private double[] gradient(double[] point) {

            // optimal radius
            Point2D.Double center = new Point2D.Double(point[0], point[1]);
            double radius = getRadius(center);

            // gradient of the sum of squared residuals
            double dJdX = 0;
            double dJdY = 0;
            for (Point2D.Double pk : points) {
                double dk = pk.distance(center);
                dJdX += (center.x - pk.x) * (dk - radius) / dk;
                dJdY += (center.y - pk.y) * (dk - radius) / dk;
            }
            dJdX *= 2;
            dJdY *= 2;

            return new double[] { dJdX, dJdY };

        }

        public double value(double[] variables)
                throws IllegalArgumentException, FunctionEvaluationException {

            Point2D.Double center = new Point2D.Double(variables[0], variables[1]);
            double radius = getRadius(center);

            double sum = 0;
            for (Point2D.Double point : points) {
                double di = point.distance(center) - radius;
                sum += di * di;
            }

            return sum;

        }

        public MultivariateVectorialFunction gradient() {
            return new MultivariateVectorialFunction() {
                private static final long serialVersionUID = 3174909643301201710L;
                public double[] value(double[] point) {
                    return gradient(point);
                }
            };
        }

        public MultivariateRealFunction partialDerivative(final int k) {
            return new MultivariateRealFunction() {
                private static final long serialVersionUID = 3073956364104833888L;
                public double value(double[] point) {
                    return gradient(point)[k];
                }
            };
        }

    }

}
