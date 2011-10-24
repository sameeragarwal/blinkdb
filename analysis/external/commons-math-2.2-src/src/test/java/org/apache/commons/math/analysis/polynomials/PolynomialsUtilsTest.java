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

import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * Tests the PolynomialsUtils class.
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public class PolynomialsUtilsTest extends TestCase {

    public void testFirstChebyshevPolynomials() {

        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(3), "-3.0 x + 4.0 x^3");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(2), "-1.0 + 2.0 x^2");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(1), "x");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(0), "1.0");

        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(7), "-7.0 x + 56.0 x^3 - 112.0 x^5 + 64.0 x^7");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(6), "-1.0 + 18.0 x^2 - 48.0 x^4 + 32.0 x^6");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(5), "5.0 x - 20.0 x^3 + 16.0 x^5");
        checkPolynomial(PolynomialsUtils.createChebyshevPolynomial(4), "1.0 - 8.0 x^2 + 8.0 x^4");

    }

    public void testChebyshevBounds() {
        for (int k = 0; k < 12; ++k) {
            PolynomialFunction Tk = PolynomialsUtils.createChebyshevPolynomial(k);
            for (double x = -1.0; x <= 1.0; x += 0.02) {
                assertTrue(k + " " + Tk.value(x), FastMath.abs(Tk.value(x)) < (1.0 + 1.0e-12));
            }
        }
    }

    public void testChebyshevDifferentials() {
        for (int k = 0; k < 12; ++k) {

            PolynomialFunction Tk0 = PolynomialsUtils.createChebyshevPolynomial(k);
            PolynomialFunction Tk1 = Tk0.polynomialDerivative();
            PolynomialFunction Tk2 = Tk1.polynomialDerivative();

            PolynomialFunction g0 = new PolynomialFunction(new double[] { k * k });
            PolynomialFunction g1 = new PolynomialFunction(new double[] { 0, -1});
            PolynomialFunction g2 = new PolynomialFunction(new double[] { 1, 0, -1 });

            PolynomialFunction Tk0g0 = Tk0.multiply(g0);
            PolynomialFunction Tk1g1 = Tk1.multiply(g1);
            PolynomialFunction Tk2g2 = Tk2.multiply(g2);

            checkNullPolynomial(Tk0g0.add(Tk1g1.add(Tk2g2)));

        }
    }

    public void testFirstHermitePolynomials() {

        checkPolynomial(PolynomialsUtils.createHermitePolynomial(3), "-12.0 x + 8.0 x^3");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(2), "-2.0 + 4.0 x^2");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(1), "2.0 x");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(0), "1.0");

        checkPolynomial(PolynomialsUtils.createHermitePolynomial(7), "-1680.0 x + 3360.0 x^3 - 1344.0 x^5 + 128.0 x^7");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(6), "-120.0 + 720.0 x^2 - 480.0 x^4 + 64.0 x^6");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(5), "120.0 x - 160.0 x^3 + 32.0 x^5");
        checkPolynomial(PolynomialsUtils.createHermitePolynomial(4), "12.0 - 48.0 x^2 + 16.0 x^4");

    }

    public void testHermiteDifferentials() {
        for (int k = 0; k < 12; ++k) {

            PolynomialFunction Hk0 = PolynomialsUtils.createHermitePolynomial(k);
            PolynomialFunction Hk1 = Hk0.polynomialDerivative();
            PolynomialFunction Hk2 = Hk1.polynomialDerivative();

            PolynomialFunction g0 = new PolynomialFunction(new double[] { 2 * k });
            PolynomialFunction g1 = new PolynomialFunction(new double[] { 0, -2 });
            PolynomialFunction g2 = new PolynomialFunction(new double[] { 1 });

            PolynomialFunction Hk0g0 = Hk0.multiply(g0);
            PolynomialFunction Hk1g1 = Hk1.multiply(g1);
            PolynomialFunction Hk2g2 = Hk2.multiply(g2);

            checkNullPolynomial(Hk0g0.add(Hk1g1.add(Hk2g2)));

        }
    }

    public void testFirstLaguerrePolynomials() {

        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(3), 6l, "6.0 - 18.0 x + 9.0 x^2 - x^3");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(2), 2l, "2.0 - 4.0 x + x^2");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(1), 1l, "1.0 - x");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(0), 1l, "1.0");

        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(7), 5040l,
                "5040.0 - 35280.0 x + 52920.0 x^2 - 29400.0 x^3"
                + " + 7350.0 x^4 - 882.0 x^5 + 49.0 x^6 - x^7");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(6),  720l,
                "720.0 - 4320.0 x + 5400.0 x^2 - 2400.0 x^3 + 450.0 x^4"
                + " - 36.0 x^5 + x^6");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(5),  120l,
        "120.0 - 600.0 x + 600.0 x^2 - 200.0 x^3 + 25.0 x^4 - x^5");
        checkPolynomial(PolynomialsUtils.createLaguerrePolynomial(4),   24l,
        "24.0 - 96.0 x + 72.0 x^2 - 16.0 x^3 + x^4");

    }

    public void testLaguerreDifferentials() {
        for (int k = 0; k < 12; ++k) {

            PolynomialFunction Lk0 = PolynomialsUtils.createLaguerrePolynomial(k);
            PolynomialFunction Lk1 = Lk0.polynomialDerivative();
            PolynomialFunction Lk2 = Lk1.polynomialDerivative();

            PolynomialFunction g0 = new PolynomialFunction(new double[] { k });
            PolynomialFunction g1 = new PolynomialFunction(new double[] { 1, -1 });
            PolynomialFunction g2 = new PolynomialFunction(new double[] { 0, 1 });

            PolynomialFunction Lk0g0 = Lk0.multiply(g0);
            PolynomialFunction Lk1g1 = Lk1.multiply(g1);
            PolynomialFunction Lk2g2 = Lk2.multiply(g2);

            checkNullPolynomial(Lk0g0.add(Lk1g1.add(Lk2g2)));

        }
    }

    public void testFirstLegendrePolynomials() {

        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(3),  2l, "-3.0 x + 5.0 x^3");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(2),  2l, "-1.0 + 3.0 x^2");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(1),  1l, "x");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(0),  1l, "1.0");

        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(7), 16l, "-35.0 x + 315.0 x^3 - 693.0 x^5 + 429.0 x^7");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(6), 16l, "-5.0 + 105.0 x^2 - 315.0 x^4 + 231.0 x^6");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(5),  8l, "15.0 x - 70.0 x^3 + 63.0 x^5");
        checkPolynomial(PolynomialsUtils.createLegendrePolynomial(4),  8l, "3.0 - 30.0 x^2 + 35.0 x^4");

    }

    public void testLegendreDifferentials() {
        for (int k = 0; k < 12; ++k) {

            PolynomialFunction Pk0 = PolynomialsUtils.createLegendrePolynomial(k);
            PolynomialFunction Pk1 = Pk0.polynomialDerivative();
            PolynomialFunction Pk2 = Pk1.polynomialDerivative();

            PolynomialFunction g0 = new PolynomialFunction(new double[] { k * (k + 1) });
            PolynomialFunction g1 = new PolynomialFunction(new double[] { 0, -2 });
            PolynomialFunction g2 = new PolynomialFunction(new double[] { 1, 0, -1 });

            PolynomialFunction Pk0g0 = Pk0.multiply(g0);
            PolynomialFunction Pk1g1 = Pk1.multiply(g1);
            PolynomialFunction Pk2g2 = Pk2.multiply(g2);

            checkNullPolynomial(Pk0g0.add(Pk1g1.add(Pk2g2)));

        }
    }

    public void testHighDegreeLegendre() {
        PolynomialsUtils.createLegendrePolynomial(40);
        double[] l40 = PolynomialsUtils.createLegendrePolynomial(40).getCoefficients();
        double denominator = 274877906944.0;
        double[] numerators = new double[] {
                          +34461632205.0,            -28258538408100.0,          +3847870979902950.0,        -207785032914759300.0,
                  +5929294332103310025.0,     -103301483474866556880.0,    +1197358103913226000200.0,    -9763073770369381232400.0,
              +58171647881784229843050.0,  -260061484647976556945400.0,  +888315281771246239250340.0, -2345767627188139419665400.0,
            +4819022625419112503443050.0, -7710436200670580005508880.0, +9566652323054238154983240.0, -9104813935044723209570256.0,
            +6516550296251767619752905.0, -3391858621221953912598660.0, +1211378079007840683070950.0,  -265365894974690562152100.0,
              +26876802183334044115405.0
        };
        for (int i = 0; i < l40.length; ++i) {
            if (i % 2 == 0) {
                double ci = numerators[i / 2] / denominator;
                assertEquals(ci, l40[i], FastMath.abs(ci) * 1.0e-15);
            } else {
                assertEquals(0.0, l40[i], 0.0);
            }
        }
    }

    private void checkPolynomial(PolynomialFunction p, long denominator, String reference) {
        PolynomialFunction q = new PolynomialFunction(new double[] { denominator});
        assertEquals(reference, p.multiply(q).toString());
    }

    private void checkPolynomial(PolynomialFunction p, String reference) {
        assertEquals(reference, p.toString());
    }

    private void checkNullPolynomial(PolynomialFunction p) {
        for (double coefficient : p.getCoefficients()) {
            assertEquals(0.0, coefficient, 1.0e-13);
        }
    }

}
