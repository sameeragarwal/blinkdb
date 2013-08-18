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

import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.util.FastMath;

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    x'' = -x
 * </pre>
 * And when x decreases down to 0, the state should be changed as follows :
 * <pre>
 *   x' -> -x'
 * </pre>
 * The theoretical solution of this problem is x = |sin(t+a)|
 * </p>

 */
public class TestProblem4
  extends TestProblemAbstract {

  /** Serializable version identifier. */
  private static final long serialVersionUID = -5910438521889015745L;

  /** Time offset. */
  private double a;

  /** theoretical state */
  private double[] y;

  /** Simple constructor. */
  public TestProblem4() {
    super();
    a = 1.2;
    double[] y0 = { FastMath.sin(a), FastMath.cos(a) };
    setInitialConditions(0.0, y0);
    setFinalConditions(15);
    double[] errorScale = { 1.0, 0.0 };
    setErrorScale(errorScale);
    y = new double[y0.length];
  }

  /**
   * Copy constructor.
   * @param problem problem to copy
   */
  public TestProblem4(TestProblem4 problem) {
    super(problem);
    a = problem.a;
    y = problem.y.clone();
  }

  /** {@inheritDoc} */
  @Override
public TestProblem4 copy() {
    return new TestProblem4(this);
  }

  @Override
  public EventHandler[] getEventsHandlers() {
    return new EventHandler[] { new Bounce(), new Stop() };
  }

  /**
   * Get the theoretical events times.
   * @return theoretical events times
   */
  @Override
  public double[] getTheoreticalEventsTimes() {
      return new double[] {
          1 * FastMath.PI - a,
          2 * FastMath.PI - a,
          3 * FastMath.PI - a,
          4 * FastMath.PI - a,
          12.0
      };
  }

  @Override
  public void doComputeDerivatives(double t, double[] y, double[] yDot) {
    yDot[0] =  y[1];
    yDot[1] = -y[0];
  }

  @Override
  public double[] computeTheoreticalState(double t) {
    double sin = FastMath.sin(t + a);
    double cos = FastMath.cos(t + a);
    y[0] = FastMath.abs(sin);
    y[1] = (sin >= 0) ? cos : -cos;
    return y;
  }

  private static class Bounce implements EventHandler {

    private static final long serialVersionUID = 1356097180027801200L;
    private int sign;

    public Bounce() {
      sign = +1;
    }

    public double g(double t, double[] y) {
      return sign * y[0];
    }

    public int eventOccurred(double t, double[] y, boolean increasing) {
      // this sign change is needed because the state will be reset soon
      sign = -sign;
      return EventHandler.RESET_STATE;
    }

    public void resetState(double t, double[] y) {
      y[0] = -y[0];
      y[1] = -y[1];
    }

  }

  private static class Stop implements EventHandler {

    private static final long serialVersionUID = 6975050568227951931L;

    public Stop() {
    }

    public double g(double t, double[] y) {
      return t - 12.0;
    }

    public int eventOccurred(double t, double[] y, boolean increasing) {
      return EventHandler.STOP;
    }

    public void resetState(double t, double[] y) {
    }

  }

}
