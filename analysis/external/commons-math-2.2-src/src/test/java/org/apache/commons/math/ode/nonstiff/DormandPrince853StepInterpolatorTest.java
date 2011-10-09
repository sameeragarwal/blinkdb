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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblem3;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.ode.sampling.StepInterpolatorTestUtils;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class DormandPrince853StepInterpolatorTest {

  @Test
  public void derivativesConsistency()
  throws DerivativeException, IntegratorException {
    TestProblem3 pb = new TestProblem3(0.1);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;
    DormandPrince853Integrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
    StepInterpolatorTestUtils.checkDerivativesConsistency(integ, pb, 1.0e-10);
  }

  @Test
  public void serialization()
    throws DerivativeException, IntegratorException,
           IOException, ClassNotFoundException {

    TestProblem3 pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;
    DormandPrince853Integrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
    integ.addStepHandler(new ContinuousOutputModel());
    integ.integrate(pb,
                    pb.getInitialTime(), pb.getInitialState(),
                    pb.getFinalTime(), new double[pb.getDimension()]);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    oos = new ObjectOutputStream(bos);
    for (StepHandler handler : integ.getStepHandlers()) {
        oos.writeObject(handler);
    }

    assertTrue(bos.size () > 88000);
    assertTrue(bos.size () < 89000);

    ByteArrayInputStream  bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream     ois = new ObjectInputStream(bis);
    ContinuousOutputModel cm  = (ContinuousOutputModel) ois.readObject();

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

    assertTrue(maxError < 2.4e-10);

  }

  @Test
  public void checklone()
  throws DerivativeException, IntegratorException {
    TestProblem3 pb = new TestProblem3(0.9);
    double minStep = 0;
    double maxStep = pb.getFinalTime() - pb.getInitialTime();
    double scalAbsoluteTolerance = 1.0e-8;
    double scalRelativeTolerance = scalAbsoluteTolerance;
    DormandPrince853Integrator integ = new DormandPrince853Integrator(minStep, maxStep,
                                                                      scalAbsoluteTolerance,
                                                                      scalRelativeTolerance);
    integ.addStepHandler(new StepHandler() {
        public void handleStep(StepInterpolator interpolator, boolean isLast)
        throws DerivativeException {
            StepInterpolator cloned = interpolator.copy();
            double tA = cloned.getPreviousTime();
            double tB = cloned.getCurrentTime();
            double halfStep = FastMath.abs(tB - tA) / 2;
            assertEquals(interpolator.getPreviousTime(), tA, 1.0e-12);
            assertEquals(interpolator.getCurrentTime(), tB, 1.0e-12);
            for (int i = 0; i < 10; ++i) {
                double t = (i * tB + (9 - i) * tA) / 9;
                interpolator.setInterpolatedTime(t);
                assertTrue(FastMath.abs(cloned.getInterpolatedTime() - t) > (halfStep / 10));
                cloned.setInterpolatedTime(t);
                assertEquals(t, cloned.getInterpolatedTime(), 1.0e-12);
                double[] referenceState = interpolator.getInterpolatedState();
                double[] cloneState     = cloned.getInterpolatedState();
                for (int j = 0; j < referenceState.length; ++j) {
                    assertEquals(referenceState[j], cloneState[j], 1.0e-12);
                }
            }
        }
        public boolean requiresDenseOutput() {
            return true;
        }
        public void reset() {
        }
    });
    integ.integrate(pb,
            pb.getInitialTime(), pb.getInitialState(),
            pb.getFinalTime(), new double[pb.getDimension()]);

  }

}
