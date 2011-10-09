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

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblem1;
import org.apache.commons.math.ode.TestProblem3;
import org.apache.commons.math.ode.TestProblem4;
import org.apache.commons.math.ode.TestProblem5;
import org.apache.commons.math.ode.TestProblemAbstract;
import org.apache.commons.math.ode.TestProblemHandler;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

import junit.framework.*;

public class GraggBulirschStoerIntegratorTest
  extends TestCase {

  public GraggBulirschStoerIntegratorTest(String name) {
    super(name);
  }

  public void testDimensionCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      AdaptiveStepsizeIntegrator integrator =
        new GraggBulirschStoerIntegrator(0.0, 1.0, 1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()+10],
                           1.0, new double[pb.getDimension()+10]);
      fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }

  public void testNullIntervalCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      GraggBulirschStoerIntegrator integrator =
        new GraggBulirschStoerIntegrator(0.0, 1.0, 1.0e-10, 1.0e-10);
      integrator.integrate(pb,
                           0.0, new double[pb.getDimension()],
                           0.0, new double[pb.getDimension()]);
      fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }

  public void testMinStep() {

    try {
      TestProblem5 pb  = new TestProblem5();
      double minStep   = 0.1 * FastMath.abs(pb.getFinalTime() - pb.getInitialTime());
      double maxStep   = FastMath.abs(pb.getFinalTime() - pb.getInitialTime());
      double[] vecAbsoluteTolerance = { 1.0e-20, 1.0e-21 };
      double[] vecRelativeTolerance = { 1.0e-20, 1.0e-21 };

      FirstOrderIntegrator integ =
        new GraggBulirschStoerIntegrator(minStep, maxStep,
                                         vecAbsoluteTolerance, vecRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);
      fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }

  }

  public void testBackward()
      throws DerivativeException, IntegratorException {

      TestProblem5 pb = new TestProblem5();
      double minStep = 0;
      double maxStep = pb.getFinalTime() - pb.getInitialTime();
      double scalAbsoluteTolerance = 1.0e-8;
      double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

      FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(minStep, maxStep,
                                                                    scalAbsoluteTolerance,
                                                                    scalRelativeTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      assertTrue(handler.getLastError() < 7.5e-9);
      assertTrue(handler.getMaximalValueError() < 8.1e-9);
      assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
      assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
  }

  public void testIncreasingTolerance()
    throws DerivativeException, IntegratorException {

    int previousCalls = Integer.MAX_VALUE;
    for (int i = -12; i < -4; ++i) {
      TestProblem1 pb     = new TestProblem1();
      double minStep      = 0;
      double maxStep      = pb.getFinalTime() - pb.getInitialTime();
      double absTolerance = FastMath.pow(10.0, i);
      double relTolerance = absTolerance;

      FirstOrderIntegrator integ =
        new GraggBulirschStoerIntegrator(minStep, maxStep,
                                         absTolerance, relTolerance);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb,
                      pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      // the coefficients are only valid for this test
      // and have been obtained from trial and error
      // there is no general relation between local and global errors
      double ratio =  handler.getMaximalValueError() / absTolerance;
      assertTrue(ratio < 2.4);
      assertTrue(ratio > 0.02);
      assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);

      int calls = pb.getCalls();
      assertEquals(integ.getEvaluations(), calls);
      assertTrue(calls <= previousCalls);
      previousCalls = calls;

    }

  }

  public void testIntegratorControls()
  throws DerivativeException, IntegratorException {

    TestProblem3 pb = new TestProblem3(0.999);
    GraggBulirschStoerIntegrator integ =
        new GraggBulirschStoerIntegrator(0, pb.getFinalTime() - pb.getInitialTime(),
                1.0e-8, 1.0e-10);

    double errorWithDefaultSettings = getMaxError(integ, pb);

    // stability control
    integ.setStabilityCheck(true, 2, 1, 0.99);
    assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setStabilityCheck(true, -1, -1, -1);

    integ.setStepsizeControl(0.5, 0.99, 0.1, 2.5);
    assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setStepsizeControl(-1, -1, -1, -1);

    integ.setOrderControl(10, 0.7, 0.95);
    assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setOrderControl(-1, -1, -1);

    integ.setInterpolationControl(true, 3);
    assertTrue(errorWithDefaultSettings < getMaxError(integ, pb));
    integ.setInterpolationControl(true, -1);

  }

  private double getMaxError(FirstOrderIntegrator integrator, TestProblemAbstract pb)
    throws DerivativeException, IntegratorException {
      TestProblemHandler handler = new TestProblemHandler(pb, integrator);
      integrator.addStepHandler(handler);
      integrator.integrate(pb,
                           pb.getInitialTime(), pb.getInitialState(),
                           pb.getFinalTime(), new double[pb.getDimension()]);
      return handler.getMaximalValueError();
  }

  public void testEvents()
    throws DerivativeException, IntegratorException {

    TestProblem4 pb = new TestProblem4();
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-10;
    double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

    FirstOrderIntegrator integ = new GraggBulirschStoerIntegrator(minStep, maxStep,
                                                                  scalAbsoluteTolerance,
                                                                  scalRelativeTolerance);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    EventHandler[] functions = pb.getEventsHandlers();
    double convergence = 1.0e-8 * maxStep;
    for (int l = 0; l < functions.length; ++l) {
      integ.addEventHandler(functions[l], Double.POSITIVE_INFINITY, convergence, 1000);
    }
    assertEquals(functions.length, integ.getEventHandlers().size());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getMaximalValueError() < 5.0e-8);
    assertEquals(0, handler.getMaximalTimeError(), convergence);
    assertEquals(12.0, handler.getLastTime(), convergence);
    integ.clearEventHandlers();
    assertEquals(0, integ.getEventHandlers().size());

  }

  public void testKepler()
    throws DerivativeException, IntegratorException {

    final TestProblem3 pb = new TestProblem3(0.9);
    double minStep        = 0;
    double maxStep        = pb.getFinalTime() - pb.getInitialTime();
    double absTolerance   = 1.0e-6;
    double relTolerance   = 1.0e-6;

    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(minStep, maxStep,
                                       absTolerance, relTolerance);
    integ.addStepHandler(new KeplerStepHandler(pb));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertEquals(integ.getEvaluations(), pb.getCalls());
    assertTrue(pb.getCalls() < 2150);

  }

  public void testVariableSteps()
    throws DerivativeException, IntegratorException {

    final TestProblem3 pb = new TestProblem3(0.9);
    double minStep        = 0;
    double maxStep        = pb.getFinalTime() - pb.getInitialTime();
    double absTolerance   = 1.0e-8;
    double relTolerance   = 1.0e-8;
    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(minStep, maxStep,
                                       absTolerance, relTolerance);
    integ.addStepHandler(new VariableStepHandler());
    double stopTime = integ.integrate(pb,
                                      pb.getInitialTime(), pb.getInitialState(),
                                      pb.getFinalTime(), new double[pb.getDimension()]);
    assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
    assertEquals("Gragg-Bulirsch-Stoer", integ.getName());
  }

  public void testUnstableDerivative()
    throws DerivativeException, IntegratorException {
    final StepProblem stepProblem = new StepProblem(0.0, 1.0, 2.0);
    FirstOrderIntegrator integ =
      new GraggBulirschStoerIntegrator(0.1, 10, 1.0e-12, 0.0);
    integ.addEventHandler(stepProblem, 1.0, 1.0e-12, 1000);
    double[] y = { Double.NaN };
    integ.integrate(stepProblem, 0.0, new double[] { 0.0 }, 10.0, y);
    assertEquals(8.0, y[0], 1.0e-12);
  }

  private static class KeplerStepHandler implements StepHandler {
    public KeplerStepHandler(TestProblem3 pb) {
      this.pb = pb;
      reset();
    }
    public boolean requiresDenseOutput() {
      return true;
    }
    public void reset() {
      nbSteps = 0;
      maxError = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast)
    throws DerivativeException {

      ++nbSteps;
      for (int a = 1; a < 100; ++a) {

        double prev   = interpolator.getPreviousTime();
        double curr   = interpolator.getCurrentTime();
        double interp = ((100 - a) * prev + a * curr) / 100;
        interpolator.setInterpolatedTime(interp);

        double[] interpolatedY = interpolator.getInterpolatedState ();
        double[] theoreticalY  = pb.computeTheoreticalState(interpolator.getInterpolatedTime());
        double dx = interpolatedY[0] - theoreticalY[0];
        double dy = interpolatedY[1] - theoreticalY[1];
        double error = dx * dx + dy * dy;
        if (error > maxError) {
          maxError = error;
        }
      }
      if (isLast) {
        assertTrue(maxError < 2.7e-6);
        assertTrue(nbSteps < 80);
      }
    }
    private int nbSteps;
    private double maxError;
    private TestProblem3 pb;
  }

  public static class VariableStepHandler implements StepHandler {
    public VariableStepHandler() {
      reset();
    }
    public boolean requiresDenseOutput() {
      return false;
    }
    public void reset() {
      firstTime = true;
      minStep = 0;
      maxStep = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast) {

      double step = FastMath.abs(interpolator.getCurrentTime()
                             - interpolator.getPreviousTime());
      if (firstTime) {
        minStep   = FastMath.abs(step);
        maxStep   = minStep;
        firstTime = false;
      } else {
        if (step < minStep) {
          minStep = step;
        }
        if (step > maxStep) {
          maxStep = step;
        }
      }

      if (isLast) {
        assertTrue(minStep < 8.2e-3);
        assertTrue(maxStep > 1.7);
      }
    }
    private boolean firstTime;
    private double  minStep;
    private double  maxStep;
  }

}
