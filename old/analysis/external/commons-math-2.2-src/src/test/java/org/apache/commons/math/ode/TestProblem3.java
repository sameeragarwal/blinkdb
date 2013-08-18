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
 *    y1'' = -y1/r^3  y1 (0) = 1-e  y1' (0) = 0
 *    y2'' = -y2/r^3  y2 (0) = 0    y2' (0) =sqrt((1+e)/(1-e))
 *    r = sqrt (y1^2 + y2^2), e = 0.9
 * </pre>
 * This is a two-body problem in the plane which can be solved by
 * Kepler's equation
 * <pre>
 *   y1 (t) = ...
 * </pre>
 * </p>

 */
public class TestProblem3
  extends TestProblemAbstract {

  /** Serializable version identifier. */
  private static final long serialVersionUID = 8567328542728919999L;

  /** Eccentricity */
  double e;

  /** theoretical state */
  private double[] y;

  /**
   * Simple constructor.
   * @param e eccentricity
   */
  public TestProblem3(double e) {
    super();
    this.e = e;
    double[] y0 = { 1 - e, 0, 0, FastMath.sqrt((1+e)/(1-e)) };
    setInitialConditions(0.0, y0);
    setFinalConditions(20.0);
    double[] errorScale = { 1.0, 1.0, 1.0, 1.0 };
    setErrorScale(errorScale);
    y = new double[y0.length];
  }

  /**
   * Simple constructor.
   */
  public TestProblem3() {
    this(0.1);
  }

  /**
   * Copy constructor.
   * @param problem problem to copy
   */
  public TestProblem3(TestProblem3 problem) {
    super(problem);
    e = problem.e;
    y = problem.y.clone();
  }

  /** {@inheritDoc} */
  @Override
public TestProblem3 copy() {
    return new TestProblem3(this);
  }

  @Override
  public void doComputeDerivatives(double t, double[] y, double[] yDot) {

    // current radius
    double r2 = y[0] * y[0] + y[1] * y[1];
    double invR3 = 1 / (r2 * FastMath.sqrt(r2));

    // compute the derivatives
    yDot[0] = y[2];
    yDot[1] = y[3];
    yDot[2] = -invR3  * y[0];
    yDot[3] = -invR3  * y[1];

  }

  @Override
  public double[] computeTheoreticalState(double t) {

    // solve Kepler's equation
    double E = t;
    double d = 0;
    double corr = 999.0;
    for (int i = 0; (i < 50) && (FastMath.abs(corr) > 1.0e-12); ++i) {
      double f2  = e * FastMath.sin(E);
      double f0  = d - f2;
      double f1  = 1 - e * FastMath.cos(E);
      double f12 = f1 + f1;
      corr  = f0 * f12 / (f1 * f12 - f0 * f2);
      d -= corr;
      E = t + d;
    }

    double cosE = FastMath.cos(E);
    double sinE = FastMath.sin(E);

    y[0] = cosE - e;
    y[1] = FastMath.sqrt(1 - e * e) * sinE;
    y[2] = -sinE / (1 - e * cosE);
    y[3] = FastMath.sqrt(1 - e * e) * cosE / (1 - e * cosE);

    return y;
  }

}
