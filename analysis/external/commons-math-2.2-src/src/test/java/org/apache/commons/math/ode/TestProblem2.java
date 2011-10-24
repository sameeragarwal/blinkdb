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

package org.apache.commons.math.ode;

import org.apache.commons.math.util.FastMath;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    y' = t^3 - t y
 * </pre>
 * with the initial condition y (0) = 0. The solution of this equation
 * is the following function :
 * <pre>
 *   y (t) = t^2 + 2 (exp (- t^2 / 2) - 1)
 * </pre>
 * </p>

 */
public class TestProblem2
  extends TestProblemAbstract {

  /** Serializable version identifier. */
  private static final long serialVersionUID = 8330741783213512366L;

  /** theoretical state */
  private double[] y;

  /**
   * Simple constructor.
   */
  public TestProblem2() {
    super();
    double[] y0 = { 0.0 };
    setInitialConditions(0.0, y0);
    setFinalConditions(1.0);
    double[] errorScale = { 1.0 };
    setErrorScale(errorScale);
    y = new double[y0.length];
  }

  /**
   * Copy constructor.
   * @param problem problem to copy
   */
  public TestProblem2(TestProblem2 problem) {
    super(problem);
    y = problem.y.clone();
  }

  /** {@inheritDoc} */
  @Override
public TestProblem2 copy() {
    return new TestProblem2(this);
  }

  @Override
  public void doComputeDerivatives(double t, double[] y, double[] yDot) {

    // compute the derivatives
    for (int i = 0; i < n; ++i)
      yDot[i] = t * (t * t - y[i]);

  }

  @Override
  public double[] computeTheoreticalState(double t) {
    double t2 = t * t;
    double c = t2 + 2 * (FastMath.exp (-0.5 * t2) - 1);
    for (int i = 0; i < n; ++i) {
      y[i] = c;
    }
    return y;
  }

}
