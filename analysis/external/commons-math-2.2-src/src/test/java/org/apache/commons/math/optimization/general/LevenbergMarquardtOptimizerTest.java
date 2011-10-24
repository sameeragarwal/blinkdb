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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.util.FastMath;

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
public class LevenbergMarquardtOptimizerTest
  extends TestCase {

    public LevenbergMarquardtOptimizerTest(String name) {
        super(name);
    }

    public void testTrivial() throws Exception {
        LinearProblem problem =
            new LinearProblem(new double[][] { { 2 } }, new double[] { 3 });
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1 }, new double[] { 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        try {
            optimizer.guessParametersErrors();
            fail("an exception should have been thrown");
        } catch (OptimizationException ee) {
            // expected behavior
        }
        assertEquals(1.5, optimum.getPoint()[0], 1.0e-10);
        assertEquals(3.0, optimum.getValue()[0], 1.0e-10);
    }

    public void testQRColumnsPermutation() throws Exception {

        LinearProblem problem =
            new LinearProblem(new double[][] { { 1.0, -1.0 }, { 0.0, 2.0 }, { 1.0, -2.0 } },
                              new double[] { 4.0, 6.0, 1.0 });

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 }, new double[] { 0, 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(7.0, optimum.getPoint()[0], 1.0e-10);
        assertEquals(3.0, optimum.getPoint()[1], 1.0e-10);
        assertEquals(4.0, optimum.getValue()[0], 1.0e-10);
        assertEquals(6.0, optimum.getValue()[1], 1.0e-10);
        assertEquals(1.0, optimum.getValue()[2], 1.0e-10);

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
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1, 1, 1, 1 },
                               new double[] { 0, 0, 0, 0, 0, 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
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
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 }, new double[] { 0, 0, 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(1.0, optimum.getPoint()[0], 1.0e-10);
        assertEquals(2.0, optimum.getPoint()[1], 1.0e-10);
        assertEquals(3.0, optimum.getPoint()[2], 1.0e-10);

    }

    public void testTwoSets() throws Exception {
        double epsilon = 1.0e-7;
        LinearProblem problem = new LinearProblem(new double[][] {
                {  2,  1,   0,  4,       0, 0 },
                { -4, -2,   3, -7,       0, 0 },
                {  4,  1,  -2,  8,       0, 0 },
                {  0, -3, -12, -1,       0, 0 },
                {  0,  0,   0,  0, epsilon, 1 },
                {  0,  0,   0,  0,       1, 1 }
        }, new double[] { 2, -9, 2, 2, 1 + epsilon * epsilon, 2});

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1, 1, 1, 1 },
                               new double[] { 0, 0, 0, 0, 0, 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
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

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 }, new double[] { 0, 0, 0 });
        assertTrue(FastMath.sqrt(problem.target.length) * optimizer.getRMS() > 0.6);
        try {
            optimizer.getCovariances();
            fail("an exception should have been thrown");
        } catch (OptimizationException ee) {
            // expected behavior
        }

    }

    public void testIllConditioned() throws Exception {
        LinearProblem problem1 = new LinearProblem(new double[][] {
                { 10.0, 7.0,  8.0,  7.0 },
                {  7.0, 5.0,  6.0,  5.0 },
                {  8.0, 6.0, 10.0,  9.0 },
                {  7.0, 5.0,  9.0, 10.0 }
        }, new double[] { 32, 23, 33, 31 });
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum1 =
            optimizer.optimize(problem1, problem1.target, new double[] { 1, 1, 1, 1 },
                               new double[] { 0, 1, 2, 3 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(1.0, optimum1.getPoint()[0], 1.0e-10);
        assertEquals(1.0, optimum1.getPoint()[1], 1.0e-10);
        assertEquals(1.0, optimum1.getPoint()[2], 1.0e-10);
        assertEquals(1.0, optimum1.getPoint()[3], 1.0e-10);

        LinearProblem problem2 = new LinearProblem(new double[][] {
                { 10.00, 7.00, 8.10, 7.20 },
                {  7.08, 5.04, 6.00, 5.00 },
                {  8.00, 5.98, 9.89, 9.00 },
                {  6.99, 4.99, 9.00, 9.98 }
        }, new double[] { 32, 23, 33, 31 });
        VectorialPointValuePair optimum2 =
            optimizer.optimize(problem2, problem2.target, new double[] { 1, 1, 1, 1 },
                               new double[] { 0, 1, 2, 3 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(-81.0, optimum2.getPoint()[0], 1.0e-8);
        assertEquals(137.0, optimum2.getPoint()[1], 1.0e-8);
        assertEquals(-34.0, optimum2.getPoint()[2], 1.0e-8);
        assertEquals( 22.0, optimum2.getPoint()[3], 1.0e-8);

    }

    public void testMoreEstimatedParametersSimple() throws Exception {

        LinearProblem problem = new LinearProblem(new double[][] {
                { 3.0, 2.0,  0.0, 0.0 },
                { 0.0, 1.0, -1.0, 1.0 },
                { 2.0, 0.0,  1.0, 0.0 }
        }, new double[] { 7.0, 3.0, 5.0 });

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 },
                new double[] { 7, 6, 5, 4 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);

    }

    public void testMoreEstimatedParametersUnsorted() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0, 1.0,  0.0,  0.0, 0.0,  0.0 },
                { 0.0, 0.0,  1.0,  1.0, 1.0,  0.0 },
                { 0.0, 0.0,  0.0,  0.0, 1.0, -1.0 },
                { 0.0, 0.0, -1.0,  1.0, 0.0,  1.0 },
                { 0.0, 0.0,  0.0, -1.0, 1.0,  0.0 }
       }, new double[] { 3.0, 12.0, -1.0, 7.0, 1.0 });

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1, 1, 1 },
                               new double[] { 2, 2, 2, 2, 2, 2 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(3.0, optimum.getPointRef()[2], 1.0e-10);
        assertEquals(4.0, optimum.getPointRef()[3], 1.0e-10);
        assertEquals(5.0, optimum.getPointRef()[4], 1.0e-10);
        assertEquals(6.0, optimum.getPointRef()[5], 1.0e-10);

    }

    public void testRedundantEquations() throws Exception {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 5.0 });

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 },
                               new double[] { 1, 1 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(2.0, optimum.getPointRef()[0], 1.0e-10);
        assertEquals(1.0, optimum.getPointRef()[1], 1.0e-10);

    }

    public void testInconsistentEquations() throws FunctionEvaluationException, OptimizationException {
        LinearProblem problem = new LinearProblem(new double[][] {
                { 1.0,  1.0 },
                { 1.0, -1.0 },
                { 1.0,  3.0 }
        }, new double[] { 3.0, 1.0, 4.0 });

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        optimizer.optimize(problem, problem.target, new double[] { 1, 1, 1 }, new double[] { 1, 1 });
        assertTrue(optimizer.getRMS() > 0.1);

    }

    public void testInconsistentSizes() throws FunctionEvaluationException, OptimizationException {
        LinearProblem problem =
            new LinearProblem(new double[][] { { 1, 0 }, { 0, 1 } }, new double[] { -1, 1 });
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        VectorialPointValuePair optimum =
            optimizer.optimize(problem, problem.target, new double[] { 1, 1 }, new double[] { 0, 0 });
        assertEquals(0, optimizer.getRMS(), 1.0e-10);
        assertEquals(-1, optimum.getPoint()[0], 1.0e-10);
        assertEquals(+1, optimum.getPoint()[1], 1.0e-10);

        try {
            optimizer.optimize(problem, problem.target,
                               new double[] { 1 },
                               new double[] { 0, 0 });
            fail("an exception should have been thrown");
        } catch (OptimizationException oe) {
            // expected behavior
        }

        try {
            optimizer.optimize(problem, new double[] { 1 },
                               new double[] { 1 },
                               new double[] { 0, 0 });
            fail("an exception should have been thrown");
        } catch (FunctionEvaluationException oe) {
            // expected behavior
        }

    }

    public void testControlParameters() {
        Circle circle = new Circle();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        checkEstimate(circle, 0.1, 10, 1.0e-14, 1.0e-16, 1.0e-10, false);
        checkEstimate(circle, 0.1, 10, 1.0e-15, 1.0e-17, 1.0e-10, true);
        checkEstimate(circle, 0.1,  5, 1.0e-15, 1.0e-16, 1.0e-10, true);
        circle.addPoint(300, -300);
        checkEstimate(circle, 0.1, 20, 1.0e-18, 1.0e-16, 1.0e-10, true);
    }

    private void checkEstimate(DifferentiableMultivariateVectorialFunction problem,
                               double initialStepBoundFactor, int maxCostEval,
                               double costRelativeTolerance, double parRelativeTolerance,
                               double orthoTolerance, boolean shouldFail) {
        try {
            LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
            optimizer.setInitialStepBoundFactor(initialStepBoundFactor);
            optimizer.setMaxIterations(maxCostEval);
            optimizer.setCostRelativeTolerance(costRelativeTolerance);
            optimizer.setParRelativeTolerance(parRelativeTolerance);
            optimizer.setOrthoTolerance(orthoTolerance);
            optimizer.optimize(problem, new double[] { 0, 0, 0, 0, 0 }, new double[] { 1, 1, 1, 1, 1 },
                               new double[] { 98.680, 47.345 });
            assertTrue(! shouldFail);
        } catch (OptimizationException ee) {
            assertTrue(shouldFail);
        } catch (FunctionEvaluationException ee) {
            assertTrue(shouldFail);
        }
    }

    public void testCircleFitting() throws Exception {
        Circle circle = new Circle();
        circle.addPoint( 30.0,  68.0);
        circle.addPoint( 50.0,  -6.0);
        circle.addPoint(110.0, -20.0);
        circle.addPoint( 35.0,  15.0);
        circle.addPoint( 45.0,  97.0);
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair optimum =
            optimizer.optimize(circle, new double[] { 0, 0, 0, 0, 0 }, new double[] { 1, 1, 1, 1, 1 },
                               new double[] { 98.680, 47.345 });
        assertTrue(optimizer.getEvaluations() < 10);
        assertTrue(optimizer.getJacobianEvaluations() < 10);
        double rms = optimizer.getRMS();
        assertEquals(1.768262623567235,  FastMath.sqrt(circle.getN()) * rms,  1.0e-10);
        Point2D.Double center = new Point2D.Double(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        assertEquals(69.96016176931406, circle.getRadius(center), 1.0e-10);
        assertEquals(96.07590211815305, center.x,      1.0e-10);
        assertEquals(48.13516790438953, center.y,      1.0e-10);
        double[][] cov = optimizer.getCovariances();
        assertEquals(1.839, cov[0][0], 0.001);
        assertEquals(0.731, cov[0][1], 0.001);
        assertEquals(cov[0][1], cov[1][0], 1.0e-14);
        assertEquals(0.786, cov[1][1], 0.001);
        double[] errors = optimizer.guessParametersErrors();
        assertEquals(1.384, errors[0], 0.001);
        assertEquals(0.905, errors[1], 0.001);

        // add perfect measurements and check errors are reduced
        double  r = circle.getRadius(center);
        for (double d= 0; d < 2 * FastMath.PI; d += 0.01) {
            circle.addPoint(center.x + r * FastMath.cos(d), center.y + r * FastMath.sin(d));
        }
        double[] target = new double[circle.getN()];
        Arrays.fill(target, 0.0);
        double[] weights = new double[circle.getN()];
        Arrays.fill(weights, 2.0);
        optimizer.optimize(circle, target, weights, new double[] { 98.680, 47.345 });
        cov = optimizer.getCovariances();
        assertEquals(0.0016, cov[0][0], 0.001);
        assertEquals(3.2e-7, cov[0][1], 1.0e-9);
        assertEquals(cov[0][1], cov[1][0], 1.0e-14);
        assertEquals(0.0016, cov[1][1], 0.001);
        errors = optimizer.guessParametersErrors();
        assertEquals(0.004, errors[0], 0.001);
        assertEquals(0.004, errors[1], 0.001);

    }

    public void testCircleFittingBadInit() throws FunctionEvaluationException, OptimizationException {
        Circle circle = new Circle();
        double[][] points = new double[][] {
                {-0.312967,  0.072366}, {-0.339248,  0.132965}, {-0.379780,  0.202724},
                {-0.390426,  0.260487}, {-0.361212,  0.328325}, {-0.346039,  0.392619},
                {-0.280579,  0.444306}, {-0.216035,  0.470009}, {-0.149127,  0.493832},
                {-0.075133,  0.483271}, {-0.007759,  0.452680}, { 0.060071,  0.410235},
                { 0.103037,  0.341076}, { 0.118438,  0.273884}, { 0.131293,  0.192201},
                { 0.115869,  0.129797}, { 0.072223,  0.058396}, { 0.022884,  0.000718},
                {-0.053355, -0.020405}, {-0.123584, -0.032451}, {-0.216248, -0.032862},
                {-0.278592, -0.005008}, {-0.337655,  0.056658}, {-0.385899,  0.112526},
                {-0.405517,  0.186957}, {-0.415374,  0.262071}, {-0.387482,  0.343398},
                {-0.347322,  0.397943}, {-0.287623,  0.458425}, {-0.223502,  0.475513},
                {-0.135352,  0.478186}, {-0.061221,  0.483371}, { 0.003711,  0.422737},
                { 0.065054,  0.375830}, { 0.108108,  0.297099}, { 0.123882,  0.222850},
                { 0.117729,  0.134382}, { 0.085195,  0.056820}, { 0.029800, -0.019138},
                {-0.027520, -0.072374}, {-0.102268, -0.091555}, {-0.200299, -0.106578},
                {-0.292731, -0.091473}, {-0.356288, -0.051108}, {-0.420561,  0.014926},
                {-0.471036,  0.074716}, {-0.488638,  0.182508}, {-0.485990,  0.254068},
                {-0.463943,  0.338438}, {-0.406453,  0.404704}, {-0.334287,  0.466119},
                {-0.254244,  0.503188}, {-0.161548,  0.495769}, {-0.075733,  0.495560},
                { 0.001375,  0.434937}, { 0.082787,  0.385806}, { 0.115490,  0.323807},
                { 0.141089,  0.223450}, { 0.138693,  0.131703}, { 0.126415,  0.049174},
                { 0.066518, -0.010217}, {-0.005184, -0.070647}, {-0.080985, -0.103635},
                {-0.177377, -0.116887}, {-0.260628, -0.100258}, {-0.335756, -0.056251},
                {-0.405195, -0.000895}, {-0.444937,  0.085456}, {-0.484357,  0.175597},
                {-0.472453,  0.248681}, {-0.438580,  0.347463}, {-0.402304,  0.422428},
                {-0.326777,  0.479438}, {-0.247797,  0.505581}, {-0.152676,  0.519380},
                {-0.071754,  0.516264}, { 0.015942,  0.472802}, { 0.076608,  0.419077},
                { 0.127673,  0.330264}, { 0.159951,  0.262150}, { 0.153530,  0.172681},
                { 0.140653,  0.089229}, { 0.078666,  0.024981}, { 0.023807, -0.037022},
                {-0.048837, -0.077056}, {-0.127729, -0.075338}, {-0.221271, -0.067526}
        };
        double[] target = new double[points.length];
        Arrays.fill(target, 0.0);
        double[] weights = new double[points.length];
        Arrays.fill(weights, 2.0);
        for (int i = 0; i < points.length; ++i) {
            circle.addPoint(points[i][0], points[i][1]);
        }
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        optimizer.setConvergenceChecker(new SimpleVectorialValueChecker(1.0e-8, 1.0e-8));
        VectorialPointValuePair optimum =
            optimizer.optimize(circle, target, weights, new double[] { -12, -12 });
        Point2D.Double center = new Point2D.Double(optimum.getPointRef()[0], optimum.getPointRef()[1]);
        assertTrue(optimizer.getEvaluations() < 25);
        assertTrue(optimizer.getJacobianEvaluations() < 20);
        assertEquals( 0.043, optimizer.getRMS(), 1.0e-3);
        assertEquals( 0.292235,  circle.getRadius(center), 1.0e-6);
        assertEquals(-0.151738,  center.x,      1.0e-6);
        assertEquals( 0.2075001, center.y,      1.0e-6);
    }

    public void testMath199() throws FunctionEvaluationException {
        try {
            QuadraticProblem problem = new QuadraticProblem();
            problem.addPoint (0, -3.182591015485607);
            problem.addPoint (1, -2.5581184967730577);
            problem.addPoint (2, -2.1488478161387325);
            problem.addPoint (3, -1.9122489313410047);
            problem.addPoint (4, 1.7785661310051026);
            LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
            optimizer.setQRRankingThreshold(0);
            optimizer.optimize(problem,
                               new double[] { 0, 0, 0, 0, 0 },
                               new double[] { 0.0, 4.4e-323, 1.0, 4.4e-323, 0.0 },
                               new double[] { 0, 0, 0 });
            fail("an exception should have been thrown");
        } catch (OptimizationException ee) {
            // expected behavior
        }

    }

    private static class LinearProblem implements DifferentiableMultivariateVectorialFunction, Serializable {

        private static final long serialVersionUID = 703247177355019415L;
        final RealMatrix factors;
        final double[] target;
        public LinearProblem(double[][] factors, double[] target) {
            this.factors = new BlockRealMatrix(factors);
            this.target  = target;
        }

        public double[] value(double[] variables) {
            return factors.operate(variables);
        }

        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                private static final long serialVersionUID = 556396458721526234L;
                public double[][] value(double[] point) {
                    return factors.getData();
                }
            };
        }

    }

    private static class Circle implements DifferentiableMultivariateVectorialFunction, Serializable {

        private static final long serialVersionUID = -4711170319243817874L;

        private ArrayList<Point2D.Double> points;

        public Circle() {
            points  = new ArrayList<Point2D.Double>();
        }

        public void addPoint(double px, double py) {
            points.add(new Point2D.Double(px, py));
        }

        public int getN() {
            return points.size();
        }

        public double getRadius(Point2D.Double center) {
            double r = 0;
            for (Point2D.Double point : points) {
                r += point.distance(center);
            }
            return r / points.size();
        }

        private double[][] jacobian(double[] point) {

            int n = points.size();
            Point2D.Double center = new Point2D.Double(point[0], point[1]);

            // gradient of the optimal radius
            double dRdX = 0;
            double dRdY = 0;
            for (Point2D.Double pk : points) {
                double dk = pk.distance(center);
                dRdX += (center.x - pk.x) / dk;
                dRdY += (center.y - pk.y) / dk;
            }
            dRdX /= n;
            dRdY /= n;

            // jacobian of the radius residuals
            double[][] jacobian = new double[n][2];
            for (int i = 0; i < n; ++i) {
                Point2D.Double pi = points.get(i);
                double di   = pi.distance(center);
                jacobian[i][0] = (center.x - pi.x) / di - dRdX;
                jacobian[i][1] = (center.y - pi.y) / di - dRdY;
            }

            return jacobian;

        }

        public double[] value(double[] variables)
        throws FunctionEvaluationException, IllegalArgumentException {

            Point2D.Double center = new Point2D.Double(variables[0], variables[1]);
            double radius = getRadius(center);

            double[] residuals = new double[points.size()];
            for (int i = 0; i < residuals.length; ++i) {
                residuals[i] = points.get(i).distance(center) - radius;
            }

            return residuals;

        }

        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                private static final long serialVersionUID = -4340046230875165095L;
                public double[][] value(double[] point) {
                    return jacobian(point);
                }
            };
        }

    }

    private static class QuadraticProblem implements DifferentiableMultivariateVectorialFunction, Serializable {

        private static final long serialVersionUID = 7072187082052755854L;
        private List<Double> x;
        private List<Double> y;

        public QuadraticProblem() {
            x = new ArrayList<Double>();
            y = new ArrayList<Double>();
        }

        public void addPoint(double x, double y) {
            this.x.add(x);
            this.y.add(y);
        }

        private double[][] jacobian(double[] variables) {
            double[][] jacobian = new double[x.size()][3];
            for (int i = 0; i < jacobian.length; ++i) {
                jacobian[i][0] = x.get(i) * x.get(i);
                jacobian[i][1] = x.get(i);
                jacobian[i][2] = 1.0;
            }
            return jacobian;
        }

        public double[] value(double[] variables) {
            double[] values = new double[x.size()];
            for (int i = 0; i < values.length; ++i) {
                values[i] = (variables[0] * x.get(i) + variables[1]) * x.get(i) + variables[2];
            }
            return values;
        }

        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                private static final long serialVersionUID = -8673650298627399464L;
                public double[][] value(double[] point) {
                    return jacobian(point);
                }
            };
        }

    }

}
