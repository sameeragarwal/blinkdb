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

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.apache.commons.math.random.GaussianRandomGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomVectorGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;
import org.junit.Test;

public class MultiStartMultivariateRealOptimizerTest {

  @Test
  public void testRosenbrock() throws Exception {

    Rosenbrock rosenbrock = new Rosenbrock();
    NelderMead underlying = new NelderMead();
    underlying.setStartConfiguration(new double[][] {
                                         { -1.2,  1.0 }, { 0.9, 1.2 } , {  3.5, -2.3 }
                                     });
    JDKRandomGenerator g = new JDKRandomGenerator();
    g.setSeed(16069223052l);
    RandomVectorGenerator generator =
        new UncorrelatedRandomVectorGenerator(2, new GaussianRandomGenerator(g));
    MultiStartMultivariateRealOptimizer optimizer =
        new MultiStartMultivariateRealOptimizer(underlying, 10, generator);
    optimizer.setConvergenceChecker(new SimpleScalarValueChecker(-1, 1.0e-3));
    optimizer.setMaxIterations(100);
    RealPointValuePair optimum =
        optimizer.optimize(rosenbrock, GoalType.MINIMIZE, new double[] { -1.2, 1.0 });

    assertEquals(rosenbrock.getCount(), optimizer.getEvaluations());
    assertTrue(optimizer.getEvaluations() > 20);
    assertTrue(optimizer.getEvaluations() < 250);
    assertTrue(optimum.getValue() < 8.0e-4);

  }

  private static class Rosenbrock implements MultivariateRealFunction {

      private int count;

      public Rosenbrock() {
          count = 0;
      }

      public double value(double[] x) throws FunctionEvaluationException {
          ++count;
          double a = x[1] - x[0] * x[0];
          double b = 1.0 - x[0];
          return 100 * a * a + b * b;
      }

      public int getCount() {
          return count;
      }

  }

}
