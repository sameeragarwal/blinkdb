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
import org.apache.commons.math.ode.FirstOrderConverter;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.SecondOrderDifferentialEquations;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.util.FastMath;

import junit.framework.*;

public class FirstOrderConverterTest
  extends TestCase {

  public FirstOrderConverterTest(String name) {
    super(name);
  }

  public void testDoubleDimension() {
    for (int i = 1; i < 10; ++i) {
      SecondOrderDifferentialEquations eqn2 = new Equations(i, 0.2);
      FirstOrderConverter eqn1 = new FirstOrderConverter(eqn2);
      assertTrue(eqn1.getDimension() == (2 * eqn2.getDimension()));
    }
  }

  public void testDecreasingSteps()
    throws DerivativeException, IntegratorException {

    double previousError = Double.NaN;
    for (int i = 0; i < 10; ++i) {

      double step  = FastMath.pow(2.0, -(i + 1));
      double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, step)
                   - FastMath.sin(4.0);
      if (i > 0) {
        assertTrue(FastMath.abs(error) < FastMath.abs(previousError));
      }
      previousError = error;

    }
  }

  public void testSmallStep()
    throws DerivativeException, IntegratorException {
    double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, 1.0e-4)
                   - FastMath.sin(4.0);
    assertTrue(FastMath.abs(error) < 1.0e-10);
  }

  public void testBigStep()
    throws DerivativeException, IntegratorException {
    double error = integrateWithSpecifiedStep(4.0, 0.0, 1.0, 0.5)
                   - FastMath.sin(4.0);
    assertTrue(FastMath.abs(error) > 0.1);
  }

  private static class Equations
    implements SecondOrderDifferentialEquations {

     private int n;

      private double omega2;

      public Equations(int n, double omega) {
        this.n = n;
        omega2 = omega * omega;
      }

      public int getDimension() {
        return n;
      }

      public void computeSecondDerivatives(double t, double[] y, double[] yDot,
                                           double[] yDDot) {
        for (int i = 0; i < n; ++i) {
          yDDot[i] = -omega2 * y[i];
        }
    }

  }

  private double integrateWithSpecifiedStep(double omega,
                                            double t0, double t,
                                            double step)
  throws DerivativeException, IntegratorException {
    double[] y0 = new double[2];
    y0[0] = FastMath.sin(omega * t0);
    y0[1] = omega * FastMath.cos(omega * t0);
    ClassicalRungeKuttaIntegrator i = new ClassicalRungeKuttaIntegrator(step);
    double[] y = new double[2];
    i.integrate(new FirstOrderConverter(new Equations(1, omega)), t0, y0, t, y);
    return y[0];
  }

}
