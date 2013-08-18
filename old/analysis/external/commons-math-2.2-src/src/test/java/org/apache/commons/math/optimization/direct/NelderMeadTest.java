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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.LeastSquaresConverter;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleRealPointChecker;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class NelderMeadTest {

  @Test
  public void testFunctionEvaluationException() throws OptimizationException, FunctionEvaluationException, IllegalArgumentException {
      MultivariateRealFunction wrong =
          new MultivariateRealFunction() {
            private static final long serialVersionUID = 4751314470965489371L;
            public double value(double[] x) throws FunctionEvaluationException {
                if (x[0] < 0) {
                    throw new FunctionEvaluationException(x, LocalizedFormats.SIMPLE_MESSAGE, "oops");
                } else if (x[0] > 1) {
                    throw new FunctionEvaluationException(new RuntimeException("oops"), x);
                } else {
                    return x[0] * (1 - x[0]);
                }
            }
      };
      try {
          NelderMead optimizer = new NelderMead(0.9, 1.9, 0.4, 0.6);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { -1.0 });
          fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          assertNull(ce.getCause());
      }
      try {
          NelderMead optimizer = new NelderMead(0.9, 1.9, 0.4, 0.6);
          optimizer.optimize(wrong, GoalType.MINIMIZE, new double[] { +2.0 });
          fail("an exception should have been thrown");
      } catch (FunctionEvaluationException ce) {
          // expected behavior
          assertNotNull(ce.getCause());
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

      NelderMead optimizer = new NelderMead();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-10, 1.0e-30));
      optimizer.setMaxIterations(100);
      optimizer.setStartConfiguration(new double[] { 0.2, 0.2 });
      RealPointValuePair optimum;

      // minimization
      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { -3.0, 0 });
      assertEquals(xM,        optimum.getPoint()[0], 2.0e-7);
      assertEquals(yP,        optimum.getPoint()[1], 2.0e-5);
      assertEquals(valueXmYp, optimum.getValue(),    6.0e-12);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 90);

      optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[] { +1, 0 });
      assertEquals(xP,        optimum.getPoint()[0], 5.0e-6);
      assertEquals(yM,        optimum.getPoint()[1], 6.0e-6);
      assertEquals(valueXpYm, optimum.getValue(),    1.0e-11);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 90);

      // maximization
      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { -3.0, 0.0 });
      assertEquals(xM,        optimum.getPoint()[0], 1.0e-5);
      assertEquals(yM,        optimum.getPoint()[1], 3.0e-6);
      assertEquals(valueXmYm, optimum.getValue(),    3.0e-12);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 90);

      optimum = optimizer.optimize(fourExtrema, GoalType.MAXIMIZE, new double[] { +1, 0 });
      assertEquals(xP,        optimum.getPoint()[0], 4.0e-6);
      assertEquals(yP,        optimum.getPoint()[1], 5.0e-6);
      assertEquals(valueXpYp, optimum.getValue(),    7.0e-12);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 90);

  }

  @Test
  public void testRosenbrock()
    throws FunctionEvaluationException, ConvergenceException {

    Rosenbrock rosenbrock = new Rosenbrock();
    NelderMead optimizer = new NelderMead();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1, 1.0e-3));
    optimizer.setMaxIterations(100);
    optimizer.setStartConfiguration(new double[][] {
            { -1.2,  1.0 }, { 0.9, 1.2 } , {  3.5, -2.3 }
    });
    RealPointValuePair optimum =
        optimizer.optimize(rosenbrock, GoalType.MINIMIZE, new double[] { -1.2, 1.0 });

    assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
    assertTrue(optimizer.getEvaluations() > 40);
    assertTrue(optimizer.getEvaluations() < 50);
    assertTrue(optimum.getValue() < 8.0e-4);

  }

  @Test
  public void testPowell()
    throws FunctionEvaluationException, ConvergenceException {

    Powell powell = new Powell();
    NelderMead optimizer = new NelderMead();
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-3));
    optimizer.setMaxIterations(200);
    RealPointValuePair optimum =
      optimizer.optimize(powell, GoalType.MINIMIZE, new double[] { 3.0, -1.0, 0.0, 1.0 });
    assertEquals(powell.getCount(), optimizer.getEvaluations());
    assertTrue(optimizer.getEvaluations() > 110);
    assertTrue(optimizer.getEvaluations() < 130);
    assertTrue(optimum.getValue() < 2.0e-3);

  }

  @Test
  public void testLeastSquares1()
  throws FunctionEvaluationException, ConvergenceException {

      final RealMatrix factors =
          new Array2DRowRealMatrix(new double[][] {
              { 1.0, 0.0 },
              { 0.0, 1.0 }
          }, false);
      LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
          public double[] value(double[] variables) {
              return factors.operate(variables);
          }
      }, new double[] { 2.0, -3.0 });
      NelderMead optimizer = new NelderMead();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-6));
      optimizer.setMaxIterations(200);
      RealPointValuePair optimum =
          optimizer.optimize(ls, GoalType.MINIMIZE, new double[] { 10.0, 10.0 });
      assertEquals( 2.0, optimum.getPointRef()[0], 3.0e-5);
      assertEquals(-3.0, optimum.getPointRef()[1], 4.0e-4);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 80);
      assertTrue(optimum.getValue() < 1.0e-6);
  }

  @Test
  public void testLeastSquares2()
  throws FunctionEvaluationException, ConvergenceException {

      final RealMatrix factors =
          new Array2DRowRealMatrix(new double[][] {
              { 1.0, 0.0 },
              { 0.0, 1.0 }
          }, false);
      LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
          public double[] value(double[] variables) {
              return factors.operate(variables);
          }
      }, new double[] { 2.0, -3.0 }, new double[] { 10.0, 0.1 });
      NelderMead optimizer = new NelderMead();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-6));
      optimizer.setMaxIterations(200);
      RealPointValuePair optimum =
          optimizer.optimize(ls, GoalType.MINIMIZE, new double[] { 10.0, 10.0 });
      assertEquals( 2.0, optimum.getPointRef()[0], 5.0e-5);
      assertEquals(-3.0, optimum.getPointRef()[1], 8.0e-4);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 80);
      assertTrue(optimum.getValue() < 1.0e-6);
  }

  @Test
  public void testLeastSquares3()
  throws FunctionEvaluationException, ConvergenceException {

      final RealMatrix factors =
          new Array2DRowRealMatrix(new double[][] {
              { 1.0, 0.0 },
              { 0.0, 1.0 }
          }, false);
      LeastSquaresConverter ls = new LeastSquaresConverter(new MultivariateVectorialFunction() {
          public double[] value(double[] variables) {
              return factors.operate(variables);
          }
      }, new double[] { 2.0, -3.0 }, new Array2DRowRealMatrix(new double [][] {
          { 1.0, 1.2 }, { 1.2, 2.0 }
      }));
      NelderMead optimizer = new NelderMead();
      optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-6));
      optimizer.setMaxIterations(200);
      RealPointValuePair optimum =
          optimizer.optimize(ls, GoalType.MINIMIZE, new double[] { 10.0, 10.0 });
      assertEquals( 2.0, optimum.getPointRef()[0], 2.0e-3);
      assertEquals(-3.0, optimum.getPointRef()[1], 8.0e-4);
      assertTrue(optimizer.getEvaluations() > 60);
      assertTrue(optimizer.getEvaluations() < 80);
      assertTrue(optimum.getValue() < 1.0e-6);
  }

  @Test(expected = MaxIterationsExceededException.class)
  public void testMaxIterations() throws MathException {
      try {
          Powell powell = new Powell();
          NelderMead optimizer = new NelderMead();
          optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1.0, 1.0e-3));
          optimizer.setMaxIterations(20);
          optimizer.optimize(powell, GoalType.MINIMIZE, new double[] { 3.0, -1.0, 0.0, 1.0 });
      } catch (OptimizationException oe) {
          if (oe.getCause() instanceof ConvergenceException) {
              throw (ConvergenceException) oe.getCause();
          }
          throw oe;
      }
  }

  @Test(expected = MaxEvaluationsExceededException.class)
  public void testMaxEvaluations() throws MathException {
      try {
          Powell powell = new Powell();
          NelderMead optimizer = new NelderMead();
          optimizer.setConvergenceChecker(new SimpleRealPointChecker(-1.0, 1.0e-3));
          optimizer.setMaxEvaluations(20);
          optimizer.optimize(powell, GoalType.MINIMIZE, new double[] { 3.0, -1.0, 0.0, 1.0 });
      } catch (FunctionEvaluationException fee) {
          if (fee.getCause() instanceof ConvergenceException) {
              throw (ConvergenceException) fee.getCause();
          }
          throw fee;
      }
  }

  private static class Rosenbrock implements MultivariateRealFunction {

      private int count;

      public Rosenbrock() {
          count = 0;
      }

      public double value(double[] x) {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
      }

      public int getCount() {
          return count;
      }

  }

  private static class Powell implements MultivariateRealFunction {

      private int count;

      public Powell() {
          count = 0;
      }

      public double value(double[] x) {
          ++count;
          double a = x[0] + 10 * x[1];
          double b = x[2] - x[3];
          double c = x[1] - 2 * x[2];
          double d = x[0] - x[3];
          return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
      }

      public int getCount() {
          return count;
      }

  }

}
