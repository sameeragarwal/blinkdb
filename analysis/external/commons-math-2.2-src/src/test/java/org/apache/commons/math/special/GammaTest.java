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
package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;

import junit.framework.TestCase;

/**
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public class GammaTest extends TestCase {

    public GammaTest(String name) {
        super(name);
    }

    private void testRegularizedGamma(double expected, double a, double x) {
        try {
            double actualP = Gamma.regularizedGammaP(a, x);
            double actualQ = Gamma.regularizedGammaQ(a, x);
            TestUtils.assertEquals(expected, actualP, 10e-15);
            TestUtils.assertEquals(actualP, 1.0 - actualQ, 10e-15);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }

    private void testLogGamma(double expected, double x) {
        double actual = Gamma.logGamma(x);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }

    public void testRegularizedGammaNanPositive() {
        testRegularizedGamma(Double.NaN, Double.NaN, 1.0);
    }

    public void testRegularizedGammaPositiveNan() {
        testRegularizedGamma(Double.NaN, 1.0, Double.NaN);
    }

    public void testRegularizedGammaNegativePositive() {
        testRegularizedGamma(Double.NaN, -1.5, 1.0);
    }

    public void testRegularizedGammaPositiveNegative() {
        testRegularizedGamma(Double.NaN, 1.0, -1.0);
    }

    public void testRegularizedGammaZeroPositive() {
        testRegularizedGamma(Double.NaN, 0.0, 1.0);
    }

    public void testRegularizedGammaPositiveZero() {
        testRegularizedGamma(0.0, 1.0, 0.0);
    }

    public void testRegularizedGammaPositivePositive() {
        testRegularizedGamma(0.632120558828558, 1.0, 1.0);
    }

    public void testLogGammaNan() {
        testLogGamma(Double.NaN, Double.NaN);
    }

    public void testLogGammaNegative() {
        testLogGamma(Double.NaN, -1.0);
    }

    public void testLogGammaZero() {
        testLogGamma(Double.NaN, 0.0);
    }

    public void testLogGammaPositive() {
        testLogGamma(0.6931471805599457, 3.0);
    }

    public void testDigammaLargeArgs() {
        double eps = 1e-8;
        assertEquals(4.6001618527380874002, Gamma.digamma(100), eps);
        assertEquals(3.9019896734278921970, Gamma.digamma(50), eps);
        assertEquals(2.9705239922421490509, Gamma.digamma(20), eps);
        assertEquals(2.9958363947076465821, Gamma.digamma(20.5), eps);
        assertEquals(2.2622143570941481605, Gamma.digamma(10.1), eps);
        assertEquals(2.1168588189004379233, Gamma.digamma(8.8), eps);
        assertEquals(1.8727843350984671394, Gamma.digamma(7), eps);
        assertEquals(0.42278433509846713939, Gamma.digamma(2), eps);
        assertEquals(-100.56088545786867450, Gamma.digamma(0.01), eps);
        assertEquals(-4.0390398965921882955, Gamma.digamma(-0.8), eps);
        assertEquals(4.2003210041401844726, Gamma.digamma(-6.3), eps);
    }

    public void testDigammaSmallArgs() {
        // values for negative powers of 10 from 1 to 30 as computed by webMathematica with 20 digits
        // see functions.wolfram.com
        double[] expected = {-10.423754940411076795, -100.56088545786867450, -1000.5755719318103005,
                -10000.577051183514335, -100000.57719921568107, -1.0000005772140199687e6, -1.0000000577215500408e7,
                -1.0000000057721564845e8, -1.0000000005772156633e9, -1.0000000000577215665e10, -1.0000000000057721566e11,
                -1.0000000000005772157e12, -1.0000000000000577216e13, -1.0000000000000057722e14, -1.0000000000000005772e15, -1e+16,
                -1e+17, -1e+18, -1e+19, -1e+20, -1e+21, -1e+22, -1e+23, -1e+24, -1e+25, -1e+26,
                -1e+27, -1e+28, -1e+29, -1e+30};
        for (double n = 1; n < 30; n++) {
            checkRelativeError(String.format("Test %.0f: ", n), expected[(int) (n - 1)], Gamma.digamma(FastMath.pow(10.0, -n)), 1e-8);
        }
    }

    public void testTrigamma() {
        double eps = 1e-8;
        // computed using webMathematica.  For example, to compute trigamma($i) = Polygamma(1, $i), use
        //
        // http://functions.wolfram.com/webMathematica/Evaluated.jsp?name=PolyGamma2&plottype=0&vars={%221%22,%22$i%22}&digits=20
        double[] data = {
                1e-4, 1.0000000164469368793e8,
                1e-3, 1.0000016425331958690e6,
                1e-2, 10001.621213528313220,
                1e-1, 101.43329915079275882,
                1, 1.6449340668482264365,
                2, 0.64493406684822643647,
                3, 0.39493406684822643647,
                4, 0.28382295573711532536,
                5, 0.22132295573711532536,
                10, 0.10516633568168574612,
                20, 0.051270822935203119832,
                50, 0.020201333226697125806,
                100, 0.010050166663333571395
        };
        for (int i = data.length - 2; i >= 0; i -= 2) {
            assertEquals(String.format("trigamma %.0f", data[i]), data[i + 1], Gamma.trigamma(data[i]), eps);
        }
    }

    private void checkRelativeError(String msg, double expected, double actual, double tolerance) {
        assertEquals(msg, expected, actual, FastMath.abs(tolerance * actual));
    }
}
