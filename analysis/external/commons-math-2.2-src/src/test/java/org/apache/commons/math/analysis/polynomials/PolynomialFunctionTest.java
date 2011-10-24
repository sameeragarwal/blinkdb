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

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;
// junit
import junit.framework.TestCase;

/**
 * Tests the PolynomialFunction implementation of a UnivariateRealFunction.
 *
 * @version $Revision: 1070725 $
 * @author Matt Cliff <matt@mattcliff.com>
 */
public final class PolynomialFunctionTest extends TestCase {

    /** Error tolerance for tests */
    protected double tolerance = 1.0e-12;

    /**
     * tests the value of a constant polynomial.
     *
     * <p>value of this is 2.5 everywhere.</p>
     */
    public void testConstants() throws Exception {
        double[] c = { 2.5 };
        PolynomialFunction f = new PolynomialFunction( c );

        // verify that we are equal to c[0] at several (nonsymmetric) places
        assertEquals( f.value( 0.0), c[0], tolerance );
        assertEquals( f.value( -1.0), c[0], tolerance );
        assertEquals( f.value( -123.5), c[0], tolerance );
        assertEquals( f.value( 3.0), c[0], tolerance );
        assertEquals( f.value( 456.89), c[0], tolerance );

        assertEquals(f.degree(), 0);
        assertEquals(f.derivative().value(0), 0, tolerance);

        assertEquals(f.polynomialDerivative().derivative().value(0), 0, tolerance);
    }

    /**
     * tests the value of a linear polynomial.
     *
     * <p>This will test the function f(x) = 3*x - 1.5</p>
     * <p>This will have the values
     *  <tt>f(0.0) = -1.5, f(-1.0) = -4.5, f(-2.5) = -9.0,
     *      f(0.5) = 0.0, f(1.5) = 3.0</tt> and <tt>f(3.0) = 7.5</tt>
     * </p>
     */
    public void testLinear() throws Exception {
        double[] c = { -1.5, 3.0 };
        PolynomialFunction f = new PolynomialFunction( c );

        // verify that we are equal to c[0] when x=0
        assertEquals( f.value( 0.0), c[0], tolerance );

        // now check a few other places
        assertEquals( -4.5, f.value( -1.0), tolerance );
        assertEquals( -9.0, f.value( -2.5), tolerance );
        assertEquals( 0.0, f.value( 0.5), tolerance );
        assertEquals( 3.0, f.value( 1.5), tolerance );
        assertEquals( 7.5, f.value( 3.0), tolerance );

        assertEquals(f.degree(), 1);

        assertEquals(f.polynomialDerivative().derivative().value(0), 0, tolerance);

    }


    /**
     * Tests a second order polynomial.
     * <p> This will test the function f(x) = 2x^2 - 3x -2 = (2x+1)(x-2)</p>
     *
     */
    public void testQuadratic() {
        double[] c = { -2.0, -3.0, 2.0 };
        PolynomialFunction f = new PolynomialFunction( c );

        // verify that we are equal to c[0] when x=0
        assertEquals( f.value( 0.0), c[0], tolerance );

        // now check a few other places
        assertEquals( 0.0, f.value( -0.5), tolerance );
        assertEquals( 0.0, f.value( 2.0), tolerance );
        assertEquals( -2.0, f.value( 1.5), tolerance );
        assertEquals( 7.0, f.value( -1.5), tolerance );
        assertEquals( 265.5312, f.value( 12.34), tolerance );

    }


    /**
     * This will test the quintic function
     *   f(x) = x^2(x-5)(x+3)(x-1) = x^5 - 3x^4 -13x^3 + 15x^2</p>
     *
     */
    public void testQuintic() {
        double[] c = { 0.0, 0.0, 15.0, -13.0, -3.0, 1.0 };
        PolynomialFunction f = new PolynomialFunction( c );

        // verify that we are equal to c[0] when x=0
        assertEquals( f.value( 0.0), c[0], tolerance );

        // now check a few other places
        assertEquals( 0.0, f.value( 5.0), tolerance );
        assertEquals( 0.0, f.value( 1.0), tolerance );
        assertEquals( 0.0, f.value( -3.0), tolerance );
        assertEquals( 54.84375, f.value( -1.5), tolerance );
        assertEquals( -8.06637, f.value( 1.3), tolerance );

        assertEquals(f.degree(), 5);

    }


    /**
     * tests the firstDerivative function by comparison
     *
     * <p>This will test the functions
     * <tt>f(x) = x^3 - 2x^2 + 6x + 3, g(x) = 3x^2 - 4x + 6</tt>
     * and <tt>h(x) = 6x - 4</tt>
     */
    public void testfirstDerivativeComparison() throws Exception {
        double[] f_coeff = { 3.0, 6.0, -2.0, 1.0 };
        double[] g_coeff = { 6.0, -4.0, 3.0 };
        double[] h_coeff = { -4.0, 6.0 };

        PolynomialFunction f = new PolynomialFunction( f_coeff );
        PolynomialFunction g = new PolynomialFunction( g_coeff );
        PolynomialFunction h = new PolynomialFunction( h_coeff );

        // compare f' = g
        assertEquals( f.derivative().value(0.0), g.value(0.0), tolerance );
        assertEquals( f.derivative().value(1.0), g.value(1.0), tolerance );
        assertEquals( f.derivative().value(100.0), g.value(100.0), tolerance );
        assertEquals( f.derivative().value(4.1), g.value(4.1), tolerance );
        assertEquals( f.derivative().value(-3.25), g.value(-3.25), tolerance );

        // compare g' = h
        assertEquals( g.derivative().value(FastMath.PI), h.value(FastMath.PI), tolerance );
        assertEquals( g.derivative().value(FastMath.E),  h.value(FastMath.E),  tolerance );

    }

    public void testString() {
        PolynomialFunction p = new PolynomialFunction(new double[] { -5.0, 3.0, 1.0 });
        checkPolynomial(p, "-5.0 + 3.0 x + x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 0.0, -2.0, 3.0 }),
                        "-2.0 x + 3.0 x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 1.0, -2.0, 3.0 }),
                      "1.0 - 2.0 x + 3.0 x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 0.0,  2.0, 3.0 }),
                       "2.0 x + 3.0 x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 1.0,  2.0, 3.0 }),
                     "1.0 + 2.0 x + 3.0 x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 1.0,  0.0, 3.0 }),
                     "1.0 + 3.0 x^2");
        checkPolynomial(new PolynomialFunction(new double[] { 0.0 }),
                     "0");
    }

    public void testAddition() {

        PolynomialFunction p1 = new PolynomialFunction(new double[] { -2.0, 1.0 });
        PolynomialFunction p2 = new PolynomialFunction(new double[] { 2.0, -1.0, 0.0 });
        checkNullPolynomial(p1.add(p2));

        p2 = p1.add(p1);
        checkPolynomial(p2, "-4.0 + 2.0 x");

        p1 = new PolynomialFunction(new double[] { 1.0, -4.0, 2.0 });
        p2 = new PolynomialFunction(new double[] { -1.0, 3.0, -2.0 });
        p1 = p1.add(p2);
        assertEquals(1, p1.degree());
        checkPolynomial(p1, "-x");

    }

    public void testSubtraction() {

        PolynomialFunction p1 = new PolynomialFunction(new double[] { -2.0, 1.0 });
        checkNullPolynomial(p1.subtract(p1));

        PolynomialFunction p2 = new PolynomialFunction(new double[] { -2.0, 6.0 });
        p2 = p2.subtract(p1);
        checkPolynomial(p2, "5.0 x");

        p1 = new PolynomialFunction(new double[] { 1.0, -4.0, 2.0 });
        p2 = new PolynomialFunction(new double[] { -1.0, 3.0, 2.0 });
        p1 = p1.subtract(p2);
        assertEquals(1, p1.degree());
        checkPolynomial(p1, "2.0 - 7.0 x");

    }

    public void testMultiplication() {

        PolynomialFunction p1 = new PolynomialFunction(new double[] { -3.0, 2.0 });
        PolynomialFunction p2 = new PolynomialFunction(new double[] { 3.0, 2.0, 1.0 });
        checkPolynomial(p1.multiply(p2), "-9.0 + x^2 + 2.0 x^3");

        p1 = new PolynomialFunction(new double[] { 0.0, 1.0 });
        p2 = p1;
        for (int i = 2; i < 10; ++i) {
            p2 = p2.multiply(p1);
            checkPolynomial(p2, "x^" + i);
        }

    }

    public void testSerial() {
        PolynomialFunction p2 = new PolynomialFunction(new double[] { 3.0, 2.0, 1.0 });
        assertEquals(p2, TestUtils.serializeAndRecover(p2));
    }

    /**
     * tests the firstDerivative function by comparison
     *
     * <p>This will test the functions
     * <tt>f(x) = x^3 - 2x^2 + 6x + 3, g(x) = 3x^2 - 4x + 6</tt>
     * and <tt>h(x) = 6x - 4</tt>
     */
    public void testMath341() throws Exception {
        double[] f_coeff = { 3.0, 6.0, -2.0, 1.0 };
        double[] g_coeff = { 6.0, -4.0, 3.0 };
        double[] h_coeff = { -4.0, 6.0 };

        PolynomialFunction f = new PolynomialFunction( f_coeff );
        PolynomialFunction g = new PolynomialFunction( g_coeff );
        PolynomialFunction h = new PolynomialFunction( h_coeff );

        // compare f' = g
        assertEquals( f.derivative().value(0.0), g.value(0.0), tolerance );
        assertEquals( f.derivative().value(1.0), g.value(1.0), tolerance );
        assertEquals( f.derivative().value(100.0), g.value(100.0), tolerance );
        assertEquals( f.derivative().value(4.1), g.value(4.1), tolerance );
        assertEquals( f.derivative().value(-3.25), g.value(-3.25), tolerance );

        // compare g' = h
        assertEquals( g.derivative().value(FastMath.PI), h.value(FastMath.PI), tolerance );
        assertEquals( g.derivative().value(FastMath.E),  h.value(FastMath.E),  tolerance );
    }

    public void checkPolynomial(PolynomialFunction p, String reference) {
        assertEquals(reference, p.toString());
    }

    private void checkNullPolynomial(PolynomialFunction p) {
        for (double coefficient : p.getCoefficients()) {
            assertEquals(0.0, coefficient, 1.0e-15);
        }
    }

}
