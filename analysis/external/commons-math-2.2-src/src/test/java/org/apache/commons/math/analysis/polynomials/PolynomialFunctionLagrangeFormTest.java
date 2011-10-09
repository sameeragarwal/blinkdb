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
package org.apache.commons.math.analysis.polynomials;

import org.apache.commons.math.FunctionEvaluationException;

import junit.framework.TestCase;

/**
 * Testcase for Lagrange form of polynomial function.
 * <p>
 * We use n+1 points to interpolate a polynomial of degree n. This should
 * give us the exact same polynomial as result. Thus we can use a very
 * small tolerance to account only for round-off errors.
 *
 * @version $Revision: 1073498 $ $Date: 2011-02-22 21:57:26 +0100 (mar. 22 févr. 2011) $
 */
public final class PolynomialFunctionLagrangeFormTest extends TestCase {

    /**
     * Test of polynomial for the linear function.
     */
    public void testLinearFunction() throws FunctionEvaluationException {
        PolynomialFunctionLagrangeForm p;
        double c[], z, expected, result, tolerance = 1E-12;

        // p(x) = 1.5x - 4
        double x[] = { 0.0, 3.0 };
        double y[] = { -4.0, 0.5 };
        p = new PolynomialFunctionLagrangeForm(x, y);

        z = 2.0; expected = -1.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = 4.5; expected = 2.75; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = 6.0; expected = 5.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        assertEquals(1, p.degree());

        c = p.getCoefficients();
        assertEquals(2, c.length);
        assertEquals(-4.0, c[0], tolerance);
        assertEquals(1.5, c[1], tolerance);
    }

    /**
     * Test of polynomial for the quadratic function.
     */
    public void testQuadraticFunction() throws FunctionEvaluationException {
        PolynomialFunctionLagrangeForm p;
        double c[], z, expected, result, tolerance = 1E-12;

        // p(x) = 2x^2 + 5x - 3 = (2x - 1)(x + 3)
        double x[] = { 0.0, -1.0, 0.5 };
        double y[] = { -3.0, -6.0, 0.0 };
        p = new PolynomialFunctionLagrangeForm(x, y);

        z = 1.0; expected = 4.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = 2.5; expected = 22.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = -2.0; expected = -5.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        assertEquals(2, p.degree());

        c = p.getCoefficients();
        assertEquals(3, c.length);
        assertEquals(-3.0, c[0], tolerance);
        assertEquals(5.0, c[1], tolerance);
        assertEquals(2.0, c[2], tolerance);
    }

    /**
     * Test of polynomial for the quintic function.
     */
    public void testQuinticFunction() throws FunctionEvaluationException {
        PolynomialFunctionLagrangeForm p;
        double c[], z, expected, result, tolerance = 1E-12;

        // p(x) = x^5 - x^4 - 7x^3 + x^2 + 6x = x(x^2 - 1)(x + 2)(x - 3)
        double x[] = { 1.0, -1.0, 2.0, 3.0, -3.0, 0.5 };
        double y[] = { 0.0, 0.0, -24.0, 0.0, -144.0, 2.34375 };
        p = new PolynomialFunctionLagrangeForm(x, y);

        z = 0.0; expected = 0.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = -2.0; expected = 0.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        z = 4.0; expected = 360.0; result = p.value(z);
        assertEquals(expected, result, tolerance);

        assertEquals(5, p.degree());

        c = p.getCoefficients();
        assertEquals(6, c.length);
        assertEquals(0.0, c[0], tolerance);
        assertEquals(6.0, c[1], tolerance);
        assertEquals(1.0, c[2], tolerance);
        assertEquals(-7.0, c[3], tolerance);
        assertEquals(-1.0, c[4], tolerance);
        assertEquals(1.0, c[5], tolerance);
    }

    /**
     * Test of parameters for the polynomial.
     */
    public void testParameters() throws Exception {

        try {
            // bad input array length
            double x[] = { 1.0 };
            double y[] = { 2.0 };
            new PolynomialFunctionLagrangeForm(x, y);
            fail("Expecting IllegalArgumentException - bad input array length");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // mismatch input arrays
            double x[] = { 1.0, 2.0, 3.0, 4.0 };
            double y[] = { 0.0, -4.0, -24.0 };
            new PolynomialFunctionLagrangeForm(x, y);
            fail("Expecting IllegalArgumentException - mismatch input arrays");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
