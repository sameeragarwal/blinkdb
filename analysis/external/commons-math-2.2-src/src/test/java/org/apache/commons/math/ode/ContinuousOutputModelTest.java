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

import junit.framework.*;
import java.util.Random;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

public class ContinuousOutputModelTest
  extends TestCase {

  public ContinuousOutputModelTest(String name) {
    super(name);
    pb    = null;
    integ = null;
  }

  public void testBoundaries()
    throws DerivativeException, IntegratorException {
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);
    ContinuousOutputModel cm = (ContinuousOutputModel) integ.getStepHandlers().iterator().next();
    cm.setInterpolatedTime(2.0 * pb.getInitialTime() - pb.getFinalTime());
    cm.setInterpolatedTime(2.0 * pb.getFinalTime() - pb.getInitialTime());
    cm.setInterpolatedTime(0.5 * (pb.getFinalTime() + pb.getInitialTime()));
  }

  public void testRandomAccess()
    throws DerivativeException, IntegratorException {

    ContinuousOutputModel cm = new ContinuousOutputModel();
    integ.addStepHandler(cm);
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    Random random = new Random(347588535632l);
    double maxError = 0.0;
    for (int i = 0; i < 1000; ++i) {
      double r = random.nextDouble();
      double time = r * pb.getInitialTime() + (1.0 - r) * pb.getFinalTime();
      cm.setInterpolatedTime(time);
      double[] interpolatedY = cm.getInterpolatedState ();
      double[] theoreticalY  = pb.computeTheoreticalState(time);
      double dx = interpolatedY[0] - theoreticalY[0];
      double dy = interpolatedY[1] - theoreticalY[1];
      double error = dx * dx + dy * dy;
      if (error > maxError) {
        maxError = error;
      }
    }

    assertTrue(maxError < 1.0e-9);

  }

  public void testModelsMerging()
    throws DerivativeException, IntegratorException {

      // theoretical solution: y[0] = cos(t), y[1] = sin(t)
      FirstOrderDifferentialEquations problem =
          new FirstOrderDifferentialEquations() {
              private static final long serialVersionUID = 2472449657345878299L;
              public void computeDerivatives(double t, double[] y, double[] dot)
                  throws DerivativeException {
                  dot[0] = -y[1];
                  dot[1] =  y[0];
              }
              public int getDimension() {
                  return 2;
              }
          };

      // integrate backward from &pi; to 0;
      ContinuousOutputModel cm1 = new ContinuousOutputModel();
      FirstOrderIntegrator integ1 =
          new DormandPrince853Integrator(0, 1.0, 1.0e-8, 1.0e-8);
      integ1.addStepHandler(cm1);
      integ1.integrate(problem, FastMath.PI, new double[] { -1.0, 0.0 },
                       0, new double[2]);

      // integrate backward from 2&pi; to &pi;
      ContinuousOutputModel cm2 = new ContinuousOutputModel();
      FirstOrderIntegrator integ2 =
          new DormandPrince853Integrator(0, 0.1, 1.0e-12, 1.0e-12);
      integ2.addStepHandler(cm2);
      integ2.integrate(problem, 2.0 * FastMath.PI, new double[] { 1.0, 0.0 },
                       FastMath.PI, new double[2]);

      // merge the two half circles
      ContinuousOutputModel cm = new ContinuousOutputModel();
      cm.append(cm2);
      cm.append(new ContinuousOutputModel());
      cm.append(cm1);

      // check circle
      assertEquals(2.0 * FastMath.PI, cm.getInitialTime(), 1.0e-12);
      assertEquals(0, cm.getFinalTime(), 1.0e-12);
      assertEquals(cm.getFinalTime(), cm.getInterpolatedTime(), 1.0e-12);
      for (double t = 0; t < 2.0 * FastMath.PI; t += 0.1) {
          cm.setInterpolatedTime(t);
          double[] y = cm.getInterpolatedState();
          assertEquals(FastMath.cos(t), y[0], 1.0e-7);
          assertEquals(FastMath.sin(t), y[1], 1.0e-7);
      }

  }

  public void testErrorConditions()
    throws DerivativeException {

      ContinuousOutputModel cm = new ContinuousOutputModel();
      cm.handleStep(buildInterpolator(0, new double[] { 0.0, 1.0, -2.0 }, 1), true);

      // dimension mismatch
      assertTrue(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0 }, 2.0));

      // hole between time ranges
      assertTrue(checkAppendError(cm, 10.0, new double[] { 0.0, 1.0, -2.0 }, 20.0));

      // propagation direction mismatch
      assertTrue(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0, -2.0 }, 0.0));

      // no errors
      assertFalse(checkAppendError(cm, 1.0, new double[] { 0.0, 1.0, -2.0 }, 2.0));

  }

  private boolean checkAppendError(ContinuousOutputModel cm,
                                   double t0, double[] y0, double t1)
  throws DerivativeException {
      try {
          ContinuousOutputModel otherCm = new ContinuousOutputModel();
          otherCm.handleStep(buildInterpolator(t0, y0, t1), true);
          cm.append(otherCm);
      } catch(IllegalArgumentException iae) {
          return true; // there was an allowable error
      }
      return false; // no allowable error
  }

  private StepInterpolator buildInterpolator(double t0, double[] y0, double t1) {
      DummyStepInterpolator interpolator  = new DummyStepInterpolator(y0, new double[y0.length], t1 >= t0);
      interpolator.storeTime(t0);
      interpolator.shift();
      interpolator.storeTime(t1);
      return interpolator;
  }

  public void checkValue(double value, double reference) {
    assertTrue(FastMath.abs(value - reference) < 1.0e-10);
  }

  @Override
  public void setUp() {
    pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    integ = new DormandPrince54Integrator(minStep, maxStep, 1.0e-8, 1.0e-8);
  }

  @Override
  public void tearDown() {
    pb    = null;
    integ = null;
  }

  TestProblem3 pb;
  FirstOrderIntegrator integ;

}
