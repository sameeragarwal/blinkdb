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

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class MultiDirectionalTest {

  @Test
  public void testFunctionEvaluationException() throws OptimizationException, FunctionEvaluationException, IllegalArgumentException {
      MultivariateRealFunction wrong =
          new MultivariateRealFunction() {
            private static final long serialVersionUID = 4751314470965489371L;
            public double value(double[] x) throws FunctionEvaluationException {
                if (x[0] < 0) {
                    throw new FunctionEvaluationException(x,LocalizedFormats.SIMPLE_MESSAGE, "oops");
                } else if (x[0] > 1) {
                    throw (new FunctionEvaluationException(new RuntimeException("oops"), x));
                } else {
                    return x[0] * (1 - x[0]);
                }
            }
      };
      try {
          MultiDirectional optimizer = new MultiDirectional(0.9, 1.9);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { -1.0 });
          Assert.fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          Assert.assertNull(ce.getCause());
      }
      try {
          MultiDirectional optimizer = new MultiDirectional(0.9, 1.9);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { +2.0 });
          Assert.fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          Assert.assertNotNull(ce.getCause());
      }
  }

  @Test
  public void testMinimizeMaximize()
      throws FunctionEvaluationException, ConvergenceException {

      // the following function has 4 local extrema:
      final double xM        = -3.841947088256863675365;
      final double yM        = -1.391745200270734924416;
      final double xP        =  0.2286682237349059125691;
      final double yP        = -yM;
      final double valueXmYm =  0.2373295333134216789769; // local  maximum
      final double valueXmYp = -valueXmYm;                // local  minimum
      final double valueXpYm = -0.7290400707055187115322; // global minimum
      final double valueXpYp = -valueXpYm;                // global maximum
      MultivariateRealFunction fourExtrema = new MultivariateRealFunction() {
          private static final long serialVersionUID = -7039124064449091152L;
          public double value(double[] variables) throws FunctionEvaluationException {
              final double x = variables[0];
              final double y = variables[1];
              return ((x == 0) || (y == 0)) ? 0 : (FastMath.atan(x) * FastMath.atan(x + 2) * FastMath.atan(y) * FastMath.atan(y) / (x * y));
          }
      };

      MultiDirectional optimizer = new MultiDirectional();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-11, 1.0e-30));
      optimizer.setMaxIterations(200);
      optimizer.setStartConfiguration(new double[] { 0.2, 0.2 });
      RealPointValuePair optimum;

      // minimization
      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { -3.0, 0 });
      Assert.assertEquals(xM,        optimum.getPoint()[0], 4.0e-6);
      Assert.assertEquals(yP,        optimum.getPoint()[1], 3.0e-6);
      Assert.assertEquals(valueXmYp, optimum.getValue(),    8.0e-13);
      Assert.assertTrue(optimizer.getEvaluations() > 120);
      Assert.assertTrue(optimizer.getEvaluations() < 150);

      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { +1, 0 });
      Assert.assertEquals(xP,        optimum.getPoint()[0], 2.0e-8);
      Assert.assertEquals(yM,        optimum.getPoint()[1], 3.0e-6);
      Assert.assertEquals(valueXpYm, optimum.getValue(),    2.0e-12);
      Assert.assertTrue(optimizer.getEvaluations() > 120);
      Assert.assertTrue(optimizer.getEvaluations() < 150);

      // maximization
      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { -3.0, 0.0 });
      Assert.assertEquals(xM,        optimum.getPoint()[0], 7.0e-7);
      Assert.assertEquals(yM,        optimum.getPoint()[1], 3.0e-7);
      Assert.assertEquals(valueXmYm, optimum.getValue(),    2.0e-14);
      Assert.assertTrue(optimizer.getEvaluations() > 120);
      Assert.assertTrue(optimizer.getEvaluations() < 150);

      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-15, 1.0e-30));
      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { +1, 0 });
      Assert.assertEquals(xP,        optimum.getPoint()[0], 2.0e-8);
      Assert.assertEquals(yP,        optimum.getPoint()[1], 3.0e-6);
      Assert.assertEquals(valueXpYp, optimum.getValue(),    2.0e-12);
      Assert.assertTrue(optimizer.getEvaluations() > 180);
      Assert.assertTrue(optimizer.getEvaluations() < 220);

  }

  @Test
  public void testRosenbrock()
    throws FunctionEvaluationException, ConvergenceException {

    MultivariateRealFunction rosenbrock =
      new MultivariateRealFunction() {
        private static final long serialVersionUID = -9044950469615237490L;
        public double value(double[] x) throws FunctionEvaluationException {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
        }
      };

    count = 0;
    MultiDirectional optimizer = new MultiDirectional();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1, 1.0e-3));
    optimizer.setMaxIterations(100);
    optimizer.setStartConfiguration(new double[][] {
            { -1.2,  1.0 }, { 0.9, 1.2 } , {  3.5, -2.3 }
    });
    RealPointValuePair optimum =
        optimizer.optimize(rosenbrock, GoalType.MINIMIZE, new double[] { -1.2, 1.0 });

    Assert.assertEquals(count, optimizer.getEvaluations());
    Assert.assertTrue(optimizer.getEvaluations() > 50);
    Assert.assertTrue(optimizer.getEvaluations() < 100);
    Assert.assertTrue(optimum.getValue() > 1.0e-2);

  }

  @Test
  public void testPowell()
    throws FunctionEvaluationException, ConvergenceException {

    MultivariateRealFunction powell =
      new MultivariateRealFunction() {
        private static final long serialVersionUID = -832162886102041840L;
        public double value(double[] x) throws FunctionEvaluationException {
          ++count;
          double a = x[0] + 10 * x[1];
          double b = x[2] - x[3];
          double c = x[1] - 2 * x[2];
          double d = x[0] - x[3];
          return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }
      };

    count = 0;
    MultiDirectional optimizer = new MultiDirectional();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-3));
    optimizer.setMaxIterations(1000);
    RealPointValuePair optimum =
      optimizer.optimize(powell, GoalType.MINIMIZE, new double[] { 3.0, -1.0, 0.0, 1.0 });
    Assert.assertEquals(count, optimizer.getEvaluations());
    Assert.assertTrue(optimizer.getEvaluations() > 800);
    Assert.assertTrue(optimizer.getEvaluations() < 900);
    Assert.assertTrue(optimum.getValue() > 1.0e-2);

  }

  @Test
  public void testMath283()
      throws FunctionEvaluationException, OptimizationException {
      // fails because MultiDirectional.iterateSimplex is looping forever
      // the while(true) should be replaced with a convergence check
      MultiDirectional multiDirectional = new MultiDirectional();
      multiDirectional.setMaxIterations(100);
      multiDirectional.setMaxEvaluations(1000);

      final Gaussian2D function = new Gaussian2D(0.0, 0.0, 1.0);

      RealPointValuePair estimate = multiDirectional.optimize(function,
                                    GoalType.MAXIMIZE, function.getMaximumPosition());

      final double EPSILON = 1e-5;

      final double expectedMaximum = function.getMaximum();
      final double actualMaximum = estimate.getValue();
      Assert.assertEquals(expectedMaximum, actualMaximum, EPSILON);

      final double[] expectedPosition = function.getMaximumPosition();
      final double[] actualPosition = estimate.getPoint();
      Assert.assertEquals(expectedPosition[0], actualPosition[0], EPSILON );
      Assert.assertEquals(expectedPosition[1], actualPosition[1], EPSILON );

  }

  private static class Gaussian2D implements MultivariateRealFunction {

      private final double[] maximumPosition;

      private final double std;

      public Gaussian2D(double xOpt, double yOpt, double std) {
          maximumPosition = new double[] { xOpt, yOpt };
          this.std = std;
      }

      public double getMaximum() {
          return value(maximumPosition);
      }

      public double[] getMaximumPosition() {
          return maximumPosition.clone();
      }

      public double value(double[] point) {
          final double x = point[0], y = point[1];
          final double twoS2 = 2.0 * std * std;
          return 1.0 / (twoS2 * FastMath.PI) * FastMath.exp(-(x * x + y * y) / twoS2);
      }
  }

  private int count;

}
