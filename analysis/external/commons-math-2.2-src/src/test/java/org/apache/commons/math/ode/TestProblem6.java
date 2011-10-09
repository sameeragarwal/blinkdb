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

/**
 * This class is used in the junit tests for the ODE integrators.

 * <p>This specific problem is the following differential equation :
 * <pre>
 *    y' = 3x^5 - y
 * </pre>
 * when the initial condition is y(0) = -360, the solution of this
 * equation degenerates to a simple quintic polynomial function :
 * <pre>
 *   y (t) = 3x^5 - 15x^4 + 60x^3 - 180x^2 + 360x - 360
 * </pre>
 * </p>

 */
public class TestProblem6
  extends TestProblemAbstract {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1353409119804352378L;

    /** theoretical state */
    private double[] y;

    /**
     * Simple constructor.
     */
    public TestProblem6() {
        super();
        double[] y0 = { -360.0 };
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
    public TestProblem6(TestProblem6 problem) {
        super(problem);
        y = problem.y.clone();
    }

    /** {@inheritDoc} */
    @Override
    public TestProblem6 copy() {
      return new TestProblem6(this);
    }

    @Override
    public void doComputeDerivatives(double t, double[] y, double[] yDot) {

        // compute the derivatives
        double t2 = t  * t;
        double t4 = t2 * t2;
        double t5 = t4 * t;
        for (int i = 0; i < n; ++i) {
            yDot[i] = 3 * t5 - y[i];
        }

    }

    @Override
    public double[] computeTheoreticalState(double t) {
        for (int i = 0; i < n; ++i) {
            y[i] = ((((3 * t - 15) * t + 60) * t - 180) * t + 360) * t - 360;
        }
        return y;
    }

}
