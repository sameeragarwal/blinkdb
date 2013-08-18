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

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ODEIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

/**
 * This class is used to handle steps for the test problems
 * integrated during the junit tests for the ODE integrators.
 */
public class TestProblemHandler
  implements StepHandler {

  /** Associated problem. */
  private TestProblemAbstract problem;

  /** Maximal errors encountered during the integration. */
  private double maxValueError;
  private double maxTimeError;

  /** Error at the end of the integration. */
  private double lastError;

  /** Time at the end of integration. */
  private double lastTime;

  /** ODE solver used. */
  private ODEIntegrator integrator;

  /** Expected start for step. */
  private double expectedStepStart;

  /**
   * Simple constructor.
   * @param problem problem for which steps should be handled
   * @param integrator ODE solver used
   */
  public TestProblemHandler(TestProblemAbstract problem, ODEIntegrator integrator) {
    this.problem = problem;
    this.integrator = integrator;
    reset();
  }

  public boolean requiresDenseOutput() {
    return true;
  }

  public void reset() {
    maxValueError = 0;
    maxTimeError  = 0;
    lastError     = 0;
    expectedStepStart = Double.NaN;
  }

  public void handleStep(StepInterpolator interpolator,
                         boolean isLast)
    throws DerivativeException {

    double start = integrator.getCurrentStepStart();
    if (FastMath.abs((start - problem.getInitialTime()) / integrator.getCurrentSignedStepsize()) > 0.001) {
        // multistep integrators do not handle the first steps themselves
        // so we have to make sure the integrator we look at has really started its work
        if (!Double.isNaN(expectedStepStart)) {
            // the step should either start at the end of the integrator step
            // or at an event if the step is split into several substeps
            double stepError = FastMath.max(maxTimeError, FastMath.abs(start - expectedStepStart));
            for (double eventTime : problem.getTheoreticalEventsTimes()) {
                stepError = FastMath.min(stepError, FastMath.abs(start - eventTime));
            }
            maxTimeError = FastMath.max(maxTimeError, stepError);
        }
        expectedStepStart = start + integrator.getCurrentSignedStepsize();
    }

    double pT = interpolator.getPreviousTime();
    double cT = interpolator.getCurrentTime();
    double[] errorScale = problem.getErrorScale();

    // store the error at the last step
    if (isLast) {
      double[] interpolatedY = interpolator.getInterpolatedState();
      double[] theoreticalY  = problem.computeTheoreticalState(cT);
      for (int i = 0; i < interpolatedY.length; ++i) {
        double error = FastMath.abs(interpolatedY[i] - theoreticalY[i]);
        lastError = FastMath.max(error, lastError);
      }
      lastTime = cT;
    }

    // walk through the step
    for (int k = 0; k <= 20; ++k) {

      double time = pT + (k * (cT - pT)) / 20;
      interpolator.setInterpolatedTime(time);
      double[] interpolatedY = interpolator.getInterpolatedState();
      double[] theoreticalY  = problem.computeTheoreticalState(interpolator.getInterpolatedTime());

      // update the errors
      for (int i = 0; i < interpolatedY.length; ++i) {
        double error = errorScale[i] * FastMath.abs(interpolatedY[i] - theoreticalY[i]);
        maxValueError = FastMath.max(error, maxValueError);
      }
    }
  }

  /**
   * Get the maximal value error encountered during integration.
   * @return maximal value error
   */
  public double getMaximalValueError() {
    return maxValueError;
  }

  /**
   * Get the maximal time error encountered during integration.
   * @return maximal time error
   */
  public double getMaximalTimeError() {
    return maxTimeError;
  }

  /**
   * Get the error at the end of the integration.
   * @return error at the end of the integration
   */
  public double getLastError() {
    return lastError;
  }

  /**
   * Get the time at the end of the integration.
   * @return time at the end of the integration.
   */
  public double getLastTime() {
    return lastTime;
  }

}
