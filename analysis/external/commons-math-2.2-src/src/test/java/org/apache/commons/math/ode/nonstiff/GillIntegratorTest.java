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

import junit.framework.*;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblem1;
import org.apache.commons.math.ode.TestProblem3;
import org.apache.commons.math.ode.TestProblem5;
import org.apache.commons.math.ode.TestProblemAbstract;
import org.apache.commons.math.ode.TestProblemFactory;
import org.apache.commons.math.ode.TestProblemHandler;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.nonstiff.GillIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

public class GillIntegratorTest
  extends TestCase {

  public GillIntegratorTest(String name) {
    super(name);
  }

  public void testDimensionCheck() {
    try  {
      TestProblem1 pb = new TestProblem1();
      new GillIntegrator(0.01).integrate(pb,
                                         0.0, new double[pb.getDimension()+10],
                                         1.0, new double[pb.getDimension()+10]);
        fail("an exception should have been thrown");
    } catch(DerivativeException de) {
      fail("wrong exception caught");
    } catch(IntegratorException ie) {
    }
  }

  public void testDecreasingSteps()
    throws DerivativeException, IntegratorException  {

    TestProblemAbstract[] problems = TestProblemFactory.getProblems();
    for (int k = 0; k < problems.length; ++k) {

      double previousValueError = Double.NaN;
      double previousTimeError = Double.NaN;
      for (int i = 5; i < 10; ++i) {

        TestProblemAbstract pb = problems[k].copy();
        double step = (pb.getFinalTime() - pb.getInitialTime()) * FastMath.pow(2.0, -i);

        FirstOrderIntegrator integ = new GillIntegrator(step);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        EventHandler[] functions = pb.getEventsHandlers();
        for (int l = 0; l < functions.length; ++l) {
          integ.addEventHandler(functions[l],
                                     Double.POSITIVE_INFINITY, 1.0e-6 * step, 1000);
        }
        double stopTime = integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                                          pb.getFinalTime(), new double[pb.getDimension()]);
        if (functions.length == 0) {
            assertEquals(pb.getFinalTime(), stopTime, 1.0e-10);
        }

        double valueError = handler.getMaximalValueError();
        if (i > 5) {
          assertTrue(valueError < FastMath.abs(previousValueError));
        }
        previousValueError = valueError;

        double timeError = handler.getMaximalTimeError();
        if (i > 5) {
          assertTrue(timeError <= FastMath.abs(previousTimeError));
        }
        previousTimeError = timeError;

      }

    }

  }

  public void testSmallStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.001;

    FirstOrderIntegrator integ = new GillIntegrator(step);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getLastError() < 2.0e-13);
    assertTrue(handler.getMaximalValueError() < 4.0e-12);
    assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
    assertEquals("Gill", integ.getName());

  }

  public void testBigStep()
    throws DerivativeException, IntegratorException {

    TestProblem1 pb = new TestProblem1();
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.2;

    FirstOrderIntegrator integ = new GillIntegrator(step);
    TestProblemHandler handler = new TestProblemHandler(pb, integ);
    integ.addStepHandler(handler);
    integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    assertTrue(handler.getLastError() > 0.0004);
    assertTrue(handler.getMaximalValueError() > 0.005);
    assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);

  }

  public void testBackward()
      throws DerivativeException, IntegratorException {

      TestProblem5 pb = new TestProblem5();
      double step = FastMath.abs(pb.getFinalTime() - pb.getInitialTime()) * 0.001;

      FirstOrderIntegrator integ = new GillIntegrator(step);
      TestProblemHandler handler = new TestProblemHandler(pb, integ);
      integ.addStepHandler(handler);
      integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                      pb.getFinalTime(), new double[pb.getDimension()]);

      assertTrue(handler.getLastError() < 5.0e-10);
      assertTrue(handler.getMaximalValueError() < 7.0e-10);
      assertEquals(0, handler.getMaximalTimeError(), 1.0e-12);
      assertEquals("Gill", integ.getName());
  }

  public void testKepler()
    throws DerivativeException, IntegratorException {

    final TestProblem3 pb  = new TestProblem3(0.9);
    double step = (pb.getFinalTime() - pb.getInitialTime()) * 0.0003;

    FirstOrderIntegrator integ = new GillIntegrator(step);
    integ.addStepHandler(new KeplerStepHandler(pb));
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
  }

  public void testUnstableDerivative()
  throws DerivativeException, IntegratorException {
    final StepProblem stepProblem = new StepProblem(0.0, 1.0, 2.0);
    FirstOrderIntegrator integ = new GillIntegrator(0.3);
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
      return false;
    }
    public void reset() {
      maxError = 0;
    }
    public void handleStep(StepInterpolator interpolator,
                           boolean isLast) throws DerivativeException {

      double[] interpolatedY = interpolator.getInterpolatedState();
      double[] theoreticalY  = pb.computeTheoreticalState(interpolator.getCurrentTime());
      double dx = interpolatedY[0] - theoreticalY[0];
      double dy = interpolatedY[1] - theoreticalY[1];
      double error = dx * dx + dy * dy;
      if (error > maxError) {
        maxError = error;
      }
      if (isLast) {
        // even with more than 1000 evaluations per period,
        // RK4 is not able to integrate such an eccentric
        // orbit with a good accuracy
        assertTrue(maxError > 0.001);
      }
    }
    private double maxError;
    private TestProblem3 pb;
  }

  public void testStepSize()
    throws DerivativeException, IntegratorException {
      final double step = 1.23456;
      FirstOrderIntegrator integ = new GillIntegrator(step);
      integ.addStepHandler(new StepHandler() {
          public void handleStep(StepInterpolator interpolator, boolean isLast) {
              if (! isLast) {
                  assertEquals(step,
                               interpolator.getCurrentTime() - interpolator.getPreviousTime(),
                               1.0e-12);
              }
          }
          public boolean requiresDenseOutput() {
              return false;
          }
          public void reset() {
          }
      });
      integ.integrate(new FirstOrderDifferentialEquations() {
          private static final long serialVersionUID = 0L;
          public void computeDerivatives(double t, double[] y, double[] dot) {
              dot[0] = 1.0;
          }
          public int getDimension() {
              return 1;
          }
      }, 0.0, new double[] { 0.0 }, 5.0, new double[1]);
  }

}
