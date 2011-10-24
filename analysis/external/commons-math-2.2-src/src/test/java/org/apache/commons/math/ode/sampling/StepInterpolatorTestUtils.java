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
package org.apache.commons.math.ode.sampling;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblemAbstract;
import org.apache.commons.math.util.FastMath;

public class StepInterpolatorTestUtils {

    public static void checkDerivativesConsistency(final FirstOrderIntegrator integrator,
                                                   final TestProblemAbstract problem,
                                                   final double threshold)
        throws DerivativeException, IntegratorException {
        integrator.addStepHandler(new StepHandler() {

            public boolean requiresDenseOutput() {
                return true;
            }

            public void handleStep(StepInterpolator interpolator, boolean isLast)
                throws DerivativeException {

                final double h = 0.001 * (interpolator.getCurrentTime() - interpolator.getPreviousTime());
                final double t = interpolator.getCurrentTime() - 300 * h;

                if (FastMath.abs(h) < 10 * FastMath.ulp(t)) {
                    return;
                }

                interpolator.setInterpolatedTime(t - 4 * h);
                final double[] yM4h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - 3 * h);
                final double[] yM3h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - 2 * h);
                final double[] yM2h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t - h);
                final double[] yM1h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + h);
                final double[] yP1h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 2 * h);
                final double[] yP2h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 3 * h);
                final double[] yP3h = interpolator.getInterpolatedState().clone();
                interpolator.setInterpolatedTime(t + 4 * h);
                final double[] yP4h = interpolator.getInterpolatedState().clone();

                interpolator.setInterpolatedTime(t);
                final double[] yDot = interpolator.getInterpolatedDerivatives();

                for (int i = 0; i < yDot.length; ++i) {
                    final double approYDot = ( -3 * (yP4h[i] - yM4h[i]) +
                                               32 * (yP3h[i] - yM3h[i]) +
                                             -168 * (yP2h[i] - yM2h[i]) +
                                              672 * (yP1h[i] - yM1h[i])) / (840 * h);
                    assertEquals(approYDot, yDot[i], threshold);
                }

            }

            public void reset() {
            }

        });

        integrator.integrate(problem,
                             problem.getInitialTime(), problem.getInitialState(),
                             problem.getFinalTime(), new double[problem.getDimension()]);

    }
}

