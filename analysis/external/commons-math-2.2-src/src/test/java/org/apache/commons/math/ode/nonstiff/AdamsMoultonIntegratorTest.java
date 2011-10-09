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

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblem1;
import org.apache.commons.math.ode.TestProblem5;
import org.apache.commons.math.ode.TestProblem6;
import org.apache.commons.math.ode.TestProblemHandler;
import org.apache.commons.math.util.FastMath;
import org.junit.Test;

public class AdamsMoultonIntegratorTest {

    @Test(expected=IntegratorException.class)
    public void dimensionCheck() throws DerivativeException, IntegratorException {
        TestProblem1 pb = new TestProblem1();
        FirstOrderIntegrator integ =
            new AdamsMoultonIntegrator(2, 0.0, 1.0, 1.0e-10, 1.0e-10);
        integ.integrate(pb,
                        0.0, new double[pb.getDimension()+10],
                        1.0, new double[pb.getDimension()+10]);
    }

    @Test(expected=IntegratorException.class)
    public void testMinStep() throws DerivativeException, IntegratorException {

          TestProblem1 pb = new TestProblem1();
          double minStep = 0.1 * (pb.getFinalTime() - pb.getInitialTime());
          double maxStep = pb.getFinalTime() - pb.getInitialTime();
          double[] vecAbsoluteTolerance = { 1.0e-15, 1.0e-16 };
          double[] vecRelativeTolerance = { 1.0e-15, 1.0e-16 };

          FirstOrderIntegrator integ = new AdamsMoultonIntegrator(4, minStep, maxStep,
                                                                  vecAbsoluteTolerance,
                                                                  vecRelativeTolerance);
          TestProblemHandler handler = new TestProblemHandler(pb, integ);
          integ.addStepHandler(handler);
          integ.integrate(pb,
                          pb.getInitialTime(), pb.getInitialState(),
                          pb.getFinalTime(), new double[pb.getDimension()]);

    }

    @Test
    public void testIncreasingTolerance()
        throws DerivativeException, IntegratorException {

        int previousCalls = Integer.MAX_VALUE;
        for (int i = -12; i < -2; ++i) {
            TestProblem1 pb = new TestProblem1();
            double minStep = 0;
            double maxStep = pb.getFinalTime() - pb.getInitialTime();
            double scalAbsoluteTolerance = FastMath.pow(10.0, i);
            double scalRelativeTolerance = 0.01 * scalAbsoluteTolerance;

            FirstOrderIntegrator integ = new AdamsMoultonIntegrator(4, minStep, maxStep,
                                                                    scalAbsoluteTolerance,
                                                                    scalRelativeTolerance);
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb,
                            pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);

            // the 0.15 and 3.0 factors are only valid for this test
            // and has been obtained from trial and error
            // there is no general relation between local and global errors
            assertTrue(handler.getMaximalValueError() > (0.15 * scalAbsoluteTolerance));
            assertTrue(handler.getMaximalValueError() < (3.0 * scalAbsoluteTolerance));
            assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);

            int calls = pb.getCalls();
            assertEquals(integ.getEvaluations(), calls);
            assertTrue(calls <= previousCalls);
            previousCalls = calls;

        }

    }

    @Test(expected = DerivativeException.class)
    public void exceedMaxEvaluations() throws DerivativeException, IntegratorException {

        TestProblem1 pb  = new TestProblem1();
        double range = pb.getFinalTime() - pb.getInitialTime();

        AdamsMoultonIntegrator integ = new AdamsMoultonIntegrator(2, 0, range, 1.0e-12, 1.0e-12);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.setMaxEvaluations(650);
        integ.integrate(pb,
                        pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

    }

    @Test
    public void backward() throws DerivativeException, IntegratorException {

        TestProblem5 pb = new TestProblem5();
        double range = FastMath.abs(pb.getFinalTime() - pb.getInitialTime());

        FirstOrderIntegrator integ = new AdamsMoultonIntegrator(4, 0, range, 1.0e-12, 1.0e-12);
        TestProblemHandler handler = new TestProblemHandler(pb, integ);
        integ.addStepHandler(handler);
        integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                        pb.getFinalTime(), new double[pb.getDimension()]);

        assertTrue(handler.getLastError() < 1.0e-9);
        assertTrue(handler.getMaximalValueError() < 1.0e-9);
        assertEquals(0, handler.getMaximalTimeError(), 1.0e-16);
        assertEquals("Adams-Moulton", integ.getName());
    }

    @Test
    public void polynomial() throws DerivativeException, IntegratorException {
        TestProblem6 pb = new TestProblem6();
        double range = FastMath.abs(pb.getFinalTime() - pb.getInitialTime());

        for (int nSteps = 1; nSteps < 7; ++nSteps) {
            AdamsMoultonIntegrator integ =
                new AdamsMoultonIntegrator(nSteps, 1.0e-6 * range, 0.1 * range, 1.0e-9, 1.0e-9);
            TestProblemHandler handler = new TestProblemHandler(pb, integ);
            integ.addStepHandler(handler);
            integ.integrate(pb, pb.getInitialTime(), pb.getInitialState(),
                            pb.getFinalTime(), new double[pb.getDimension()]);
            if (nSteps < 4) {
                assertTrue(integ.getEvaluations() > 140);
            } else {
                assertTrue(integ.getEvaluations() < 90);
            }
        }

    }

}
