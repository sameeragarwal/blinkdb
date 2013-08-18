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

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * Testcase for Romberg integrator.
 * <p>
 * Romberg algorithm is very fast for good behavior integrand. Test runs
 * show that for a default relative accuracy of 1E-6, it generally takes
 * takes less than 5 iterations for the integral to converge.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public final class RombergIntegratorTest extends TestCase {

    /**
     * Test of integrator for the sine function.
     */
    public void testSinFunction() throws MathException {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealIntegrator integrator = new RombergIntegrator();
        double min, max, expected, result, tolerance;

        min = 0; max = FastMath.PI; expected = 2;
        tolerance = FastMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);

        min = -FastMath.PI/3; max = 0; expected = -0.5;
        tolerance = FastMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);
    }

    /**
     * Test of integrator for the quintic function.
     */
    public void testQuinticFunction() throws MathException {
        UnivariateRealFunction f = new QuinticFunction();
        UnivariateRealIntegrator integrator = new RombergIntegrator();
        double min, max, expected, result, tolerance;

        min = 0; max = 1; expected = -1.0/48;
        tolerance = FastMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);

        min = 0; max = 0.5; expected = 11.0/768;
        tolerance = FastMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);

        min = -1; max = 4; expected = 2048/3.0 - 78 + 1.0/48;
        tolerance = FastMath.abs(expected * integrator.getRelativeAccuracy());
        result = integrator.integrate(f, min, max);
        assertEquals(expected, result, tolerance);
    }

    /**
     * Test of parameters for the integrator.
     */
    public void testParameters() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealIntegrator integrator = new RombergIntegrator();

        try {
            // bad interval
            integrator.integrate(f, 1, -1);
            fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad iteration limits
            integrator.setMinimalIterationCount(5);
            integrator.setMaximalIterationCount(4);
            integrator.integrate(f, -1, 1);
            fail("Expecting IllegalArgumentException - bad iteration limits");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad iteration limits
            integrator.setMinimalIterationCount(10);
            integrator.setMaximalIterationCount(50);
            integrator.integrate(f, -1, 1);
            fail("Expecting IllegalArgumentException - bad iteration limits");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
