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
package org.apache.commons.math.analysis.integration;

import java.util.Random;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.util.FastMath;

import junit.framework.*;

public class LegendreGaussIntegratorTest
extends TestCase {

    public LegendreGaussIntegratorTest(String name) {
        super(name);
    }

    public void testSinFunction() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealIntegrator integrator = new LegendreGaussIntegrator(5, 64);
        integrator.setAbsoluteAccuracy(1.0e-10);
        integrator.setRelativeAccuracy(1.0e-14);
        integrator.setMinimalIterationCount(2);
        integrator.setMaximalIterationCount(15);
        double min, max, expected, result, tolerance;

        min = 0; max = FastMath.PI; expected = 2;
        tolerance = FastMath.max(integrator.getAbsoluteAccuracy(),
                             FastMath.abs(expected * integrator.getRelativeAccuracy()));
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);

        min = -FastMath.PI/3; max = 0; expected = -0.5;
        tolerance = FastMath.max(integrator.getAbsoluteAccuracy(),
                FastMath.abs(expected * integrator.getRelativeAccuracy()));
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);
    }

    public void testQuinticFunction() throws MathException {
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealIntegrator integrator = new LegendreGaussIntegrator(3, 64);
        double min, max, expected, result;

        min = 0; max = 1; expected = -1.0/48;
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, 1.0e-16);

        min = 0; max = 0.5; expected = 11.0/768;
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, 1.0e-16);

        min = -1; max = 4; expected = 2048/3.0 - 78 + 1.0/48;
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, 1.0e-16);
    }

    public void testExactIntegration()
        throws ConvergenceException, FunctionEvaluationException {
        Random random = new Random(86343623467878363l);
        for (int n = 2; n < 6; ++n) {
            LegendreGaussIntegrator integrator =
                new LegendreGaussIntegrator(n, 64);

            // an n points Gauss-Legendre integrator integrates 2n-1 degree polynoms exactly
            for (int degree = 0; degree <= 2 * n - 1; ++degree) {
                for (int i = 0; i < 10; ++i) {
                    double[] coeff = new double[degree + 1];
                    for (int k = 0; k < coeff.length; ++k) {
                        coeff[k] = 2 * random.nextDouble() - 1;
                    }
                    PolynomialFunction p = new PolynomialFunction(coeff);
                    double result    = integrator.integrate(p, -5.0, 15.0);
                    double reference = exactIntegration(p, -5.0, 15.0);
                    assertEquals(n + " " + degree + " " + i, reference, result, 1.0e-12 * (1.0 + FastMath.abs(reference)));
                }
            }

        }
    }

    private double exactIntegration(PolynomialFunction p, double a, double b) {
        final double[] coeffs = p.getCoefficients();
        double yb = coeffs[coeffs.length - 1] / coeffs.length;
        double ya = yb;
        for (int i = coeffs.length - 2; i >= 0; --i) {
            yb = yb * b + coeffs[i] / (i + 1);
            ya = ya * a + coeffs[i] / (i + 1);
        }
        return yb * b - ya * a;
    }

}
