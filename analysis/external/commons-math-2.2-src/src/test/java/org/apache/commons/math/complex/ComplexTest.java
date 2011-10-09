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

package org.apache.commons.math.complex;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.util.FastMath;

import java.util.List;

import junit.framework.TestCase;

/**
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public class ComplexTest extends TestCase {


    private double inf = Double.POSITIVE_INFINITY;
    private double neginf = Double.NEGATIVE_INFINITY;
    private double nan = Double.NaN;
    private double pi = FastMath.PI;
    private Complex oneInf = new Complex(1, inf);
    private Complex oneNegInf = new Complex(1, neginf);
    private Complex infOne = new Complex(inf, 1);
    private Complex infZero = new Complex(inf, 0);
    private Complex infNaN = new Complex(inf, nan);
    private Complex infNegInf = new Complex(inf, neginf);
    private Complex infInf = new Complex(inf, inf);
    private Complex negInfInf = new Complex(neginf, inf);
    private Complex negInfZero = new Complex(neginf, 0);
    private Complex negInfOne = new Complex(neginf, 1);
    private Complex negInfNaN = new Complex(neginf, nan);
    private Complex negInfNegInf = new Complex(neginf, neginf);
    private Complex oneNaN = new Complex(1, nan);
    private Complex zeroInf = new Complex(0, inf);
    private Complex zeroNaN = new Complex(0, nan);
    private Complex nanInf = new Complex(nan, inf);
    private Complex nanNegInf = new Complex(nan, neginf);
    private Complex nanZero = new Complex(nan, 0);

    public void testConstructor() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(3.0, z.getReal(), 1.0e-5);
        assertEquals(4.0, z.getImaginary(), 1.0e-5);
    }

    public void testConstructorNaN() {
        Complex z = new Complex(3.0, Double.NaN);
        assertTrue(z.isNaN());

        z = new Complex(nan, 4.0);
        assertTrue(z.isNaN());

        z = new Complex(3.0, 4.0);
        assertFalse(z.isNaN());
    }

    public void testAbs() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(5.0, z.abs(), 1.0e-5);
    }

    public void testAbsNaN() {
        assertTrue(Double.isNaN(Complex.NaN.abs()));
        Complex z = new Complex(inf, nan);
        assertTrue(Double.isNaN(z.abs()));
    }

    public void testAbsInfinite() {
        Complex z = new Complex(inf, 0);
        assertEquals(inf, z.abs(), 0);
        z = new Complex(0, neginf);
        assertEquals(inf, z.abs(), 0);
        z = new Complex(inf, neginf);
        assertEquals(inf, z.abs(), 0);
    }

    public void testAdd() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.add(y);
        assertEquals(8.0, z.getReal(), 1.0e-5);
        assertEquals(10.0, z.getImaginary(), 1.0e-5);
    }

    public void testAddNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.add(Complex.NaN);
        assertTrue(z.isNaN());
        z = new Complex(1, nan);
        Complex w = x.add(z);
        assertEquals(w.getReal(), 4.0, 0);
        assertTrue(Double.isNaN(w.getImaginary()));
    }

    public void testAddInfinite() {
        Complex x = new Complex(1, 1);
        Complex z = new Complex(inf, 0);
        Complex w = x.add(z);
        assertEquals(w.getImaginary(), 1, 0);
        assertEquals(inf, w.getReal(), 0);

        x = new Complex(neginf, 0);
        assertTrue(Double.isNaN(x.add(z).getReal()));
    }

    public void testConjugate() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.conjugate();
        assertEquals(3.0, z.getReal(), 1.0e-5);
        assertEquals(-4.0, z.getImaginary(), 1.0e-5);
    }

    public void testConjugateNaN() {
        Complex z = Complex.NaN.conjugate();
        assertTrue(z.isNaN());
    }

    public void testConjugateInfiinite() {
        Complex z = new Complex(0, inf);
        assertEquals(neginf, z.conjugate().getImaginary(), 0);
        z = new Complex(0, neginf);
        assertEquals(inf, z.conjugate().getImaginary(), 0);
    }

    public void testDivide() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.divide(y);
        assertEquals(39.0 / 61.0, z.getReal(), 1.0e-5);
        assertEquals(2.0 / 61.0, z.getImaginary(), 1.0e-5);
    }

    public void testDivideReal() {
        Complex x = new Complex(2d, 3d);
        Complex y = new Complex(2d, 0d);
        assertEquals(new Complex(1d, 1.5), x.divide(y));

    }

    public void testDivideImaginary() {
        Complex x = new Complex(2d, 3d);
        Complex y = new Complex(0d, 2d);
        assertEquals(new Complex(1.5d, -1d), x.divide(y));
    }

    public void testDivideInfinite() {
        Complex x = new Complex(3, 4);
        Complex w = new Complex(neginf, inf);
        assertTrue(x.divide(w).equals(Complex.ZERO));

        Complex z = w.divide(x);
        assertTrue(Double.isNaN(z.getReal()));
        assertEquals(inf, z.getImaginary(), 0);

        w = new Complex(inf, inf);
        z = w.divide(x);
        assertTrue(Double.isNaN(z.getImaginary()));
        assertEquals(inf, z.getReal(), 0);

        w = new Complex(1, inf);
        z = w.divide(w);
        assertTrue(Double.isNaN(z.getReal()));
        assertTrue(Double.isNaN(z.getImaginary()));
    }

    public void testDivideZero() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.divide(Complex.ZERO);
        assertEquals(z, Complex.NaN);
    }

    public void testDivideNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.divide(Complex.NaN);
        assertTrue(z.isNaN());
    }

    public void testDivideNaNInf() {
       Complex z = oneInf.divide(Complex.ONE);
       assertTrue(Double.isNaN(z.getReal()));
       assertEquals(inf, z.getImaginary(), 0);

       z = negInfNegInf.divide(oneNaN);
       assertTrue(Double.isNaN(z.getReal()));
       assertTrue(Double.isNaN(z.getImaginary()));

       z = negInfInf.divide(Complex.ONE);
       assertTrue(Double.isNaN(z.getReal()));
       assertTrue(Double.isNaN(z.getImaginary()));
    }

    public void testMultiply() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.multiply(y);
        assertEquals(-9.0, z.getReal(), 1.0e-5);
        assertEquals(38.0, z.getImaginary(), 1.0e-5);
    }

    public void testMultiplyNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.multiply(Complex.NaN);
        assertTrue(z.isNaN());
    }

    public void testMultiplyNaNInf() {
        Complex z = new Complex(1,1);
        Complex w = z.multiply(infOne);
        assertEquals(w.getReal(), inf, 0);
        assertEquals(w.getImaginary(), inf, 0);

        // [MATH-164]
        assertTrue(new Complex( 1,0).multiply(infInf).equals(Complex.INF));
        assertTrue(new Complex(-1,0).multiply(infInf).equals(Complex.INF));
        assertTrue(new Complex( 1,0).multiply(negInfZero).equals(Complex.INF));

        w = oneInf.multiply(oneNegInf);
        assertEquals(w.getReal(), inf, 0);
        assertEquals(w.getImaginary(), inf, 0);

        w = negInfNegInf.multiply(oneNaN);
        assertTrue(Double.isNaN(w.getReal()));
        assertTrue(Double.isNaN(w.getImaginary()));
    }

    public void testScalarMultiply() {
        Complex x = new Complex(3.0, 4.0);
        double y = 2.0;
        Complex z = x.multiply(y);
        assertEquals(6.0, z.getReal(), 1.0e-5);
        assertEquals(8.0, z.getImaginary(), 1.0e-5);
    }

    public void testScalarMultiplyNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.multiply(Double.NaN);
        assertTrue(z.isNaN());
    }

    public void testScalarMultiplyInf() {
        Complex z = new Complex(1,1);
        Complex w = z.multiply(Double.POSITIVE_INFINITY);
        assertEquals(w.getReal(), inf, 0);
        assertEquals(w.getImaginary(), inf, 0);

        w = z.multiply(Double.NEGATIVE_INFINITY);
        assertEquals(w.getReal(), inf, 0);
        assertEquals(w.getImaginary(), inf, 0);
    }

    public void testNegate() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.negate();
        assertEquals(-3.0, z.getReal(), 1.0e-5);
        assertEquals(-4.0, z.getImaginary(), 1.0e-5);
    }

    public void testNegateNaN() {
        Complex z = Complex.NaN.negate();
        assertTrue(z.isNaN());
    }

    public void testSubtract() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(5.0, 6.0);
        Complex z = x.subtract(y);
        assertEquals(-2.0, z.getReal(), 1.0e-5);
        assertEquals(-2.0, z.getImaginary(), 1.0e-5);
    }

    public void testSubtractNaN() {
        Complex x = new Complex(3.0, 4.0);
        Complex z = x.subtract(Complex.NaN);
        assertTrue(z.isNaN());
    }

    public void testEqualsNull() {
        Complex x = new Complex(3.0, 4.0);
        assertFalse(x.equals(null));
    }

    public void testEqualsClass() {
        Complex x = new Complex(3.0, 4.0);
        assertFalse(x.equals(this));
    }

    public void testEqualsSame() {
        Complex x = new Complex(3.0, 4.0);
        assertTrue(x.equals(x));
    }

    public void testEqualsTrue() {
        Complex x = new Complex(3.0, 4.0);
        Complex y = new Complex(3.0, 4.0);
        assertTrue(x.equals(y));
    }

    public void testEqualsRealDifference() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0 + Double.MIN_VALUE, 0.0);
        assertFalse(x.equals(y));
    }

    public void testEqualsImaginaryDifference() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0, 0.0 + Double.MIN_VALUE);
        assertFalse(x.equals(y));
    }

    public void testEqualsNaN() {
        Complex realNaN = new Complex(Double.NaN, 0.0);
        Complex imaginaryNaN = new Complex(0.0, Double.NaN);
        Complex complexNaN = Complex.NaN;
        assertTrue(realNaN.equals(imaginaryNaN));
        assertTrue(imaginaryNaN.equals(complexNaN));
        assertTrue(realNaN.equals(complexNaN));
    }

    public void testHashCode() {
        Complex x = new Complex(0.0, 0.0);
        Complex y = new Complex(0.0, 0.0 + Double.MIN_VALUE);
        assertFalse(x.hashCode()==y.hashCode());
        y = new Complex(0.0 + Double.MIN_VALUE, 0.0);
        assertFalse(x.hashCode()==y.hashCode());
        Complex realNaN = new Complex(Double.NaN, 0.0);
        Complex imaginaryNaN = new Complex(0.0, Double.NaN);
        assertEquals(realNaN.hashCode(), imaginaryNaN.hashCode());
        assertEquals(imaginaryNaN.hashCode(), Complex.NaN.hashCode());
    }

    public void testAcos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.936812, -2.30551);
        TestUtils.assertEquals(expected, z.acos(), 1.0e-5);
        TestUtils.assertEquals(new Complex(FastMath.acos(0), 0),
                Complex.ZERO.acos(), 1.0e-12);
    }

    public void testAcosInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.acos());
        TestUtils.assertSame(Complex.NaN, oneNegInf.acos());
        TestUtils.assertSame(Complex.NaN, infOne.acos());
        TestUtils.assertSame(Complex.NaN, negInfOne.acos());
        TestUtils.assertSame(Complex.NaN, infInf.acos());
        TestUtils.assertSame(Complex.NaN, infNegInf.acos());
        TestUtils.assertSame(Complex.NaN, negInfInf.acos());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.acos());
    }

    public void testAcosNaN() {
        assertTrue(Complex.NaN.acos().isNaN());
    }

    public void testAsin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(0.633984, 2.30551);
        TestUtils.assertEquals(expected, z.asin(), 1.0e-5);
    }

    public void testAsinNaN() {
        assertTrue(Complex.NaN.asin().isNaN());
    }

    public void testAsinInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.asin());
        TestUtils.assertSame(Complex.NaN, oneNegInf.asin());
        TestUtils.assertSame(Complex.NaN, infOne.asin());
        TestUtils.assertSame(Complex.NaN, negInfOne.asin());
        TestUtils.assertSame(Complex.NaN, infInf.asin());
        TestUtils.assertSame(Complex.NaN, infNegInf.asin());
        TestUtils.assertSame(Complex.NaN, negInfInf.asin());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.asin());
    }


    public void testAtan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.44831, 0.158997);
        TestUtils.assertEquals(expected, z.atan(), 1.0e-5);
    }

    public void testAtanInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.atan());
        TestUtils.assertSame(Complex.NaN, oneNegInf.atan());
        TestUtils.assertSame(Complex.NaN, infOne.atan());
        TestUtils.assertSame(Complex.NaN, negInfOne.atan());
        TestUtils.assertSame(Complex.NaN, infInf.atan());
        TestUtils.assertSame(Complex.NaN, infNegInf.atan());
        TestUtils.assertSame(Complex.NaN, negInfInf.atan());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.atan());
    }

    public void testAtanNaN() {
        assertTrue(Complex.NaN.atan().isNaN());
        assertTrue(Complex.I.atan().isNaN());
    }

    public void testCos() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-27.03495, -3.851153);
        TestUtils.assertEquals(expected, z.cos(), 1.0e-5);
    }

    public void testCosNaN() {
        assertTrue(Complex.NaN.cos().isNaN());
    }

    public void testCosInf() {
        TestUtils.assertSame(infNegInf, oneInf.cos());
        TestUtils.assertSame(infInf, oneNegInf.cos());
        TestUtils.assertSame(Complex.NaN, infOne.cos());
        TestUtils.assertSame(Complex.NaN, negInfOne.cos());
        TestUtils.assertSame(Complex.NaN, infInf.cos());
        TestUtils.assertSame(Complex.NaN, infNegInf.cos());
        TestUtils.assertSame(Complex.NaN, negInfInf.cos());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.cos());
    }

    public void testCosh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.58066, -7.58155);
        TestUtils.assertEquals(expected, z.cosh(), 1.0e-5);
    }

    public void testCoshNaN() {
        assertTrue(Complex.NaN.cosh().isNaN());
    }

    public void testCoshInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.cosh());
        TestUtils.assertSame(Complex.NaN, oneNegInf.cosh());
        TestUtils.assertSame(infInf, infOne.cosh());
        TestUtils.assertSame(infNegInf, negInfOne.cosh());
        TestUtils.assertSame(Complex.NaN, infInf.cosh());
        TestUtils.assertSame(Complex.NaN, infNegInf.cosh());
        TestUtils.assertSame(Complex.NaN, negInfInf.cosh());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.cosh());
    }

    public void testExp() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-13.12878, -15.20078);
        TestUtils.assertEquals(expected, z.exp(), 1.0e-5);
        TestUtils.assertEquals(Complex.ONE,
                Complex.ZERO.exp(), 10e-12);
        Complex iPi = Complex.I.multiply(new Complex(pi,0));
        TestUtils.assertEquals(Complex.ONE.negate(),
                iPi.exp(), 10e-12);
    }

    public void testExpNaN() {
        assertTrue(Complex.NaN.exp().isNaN());
    }

    public void testExpInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.exp());
        TestUtils.assertSame(Complex.NaN, oneNegInf.exp());
        TestUtils.assertSame(infInf, infOne.exp());
        TestUtils.assertSame(Complex.ZERO, negInfOne.exp());
        TestUtils.assertSame(Complex.NaN, infInf.exp());
        TestUtils.assertSame(Complex.NaN, infNegInf.exp());
        TestUtils.assertSame(Complex.NaN, negInfInf.exp());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.exp());
    }

    public void testLog() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.60944, 0.927295);
        TestUtils.assertEquals(expected, z.log(), 1.0e-5);
    }

    public void testLogNaN() {
        assertTrue(Complex.NaN.log().isNaN());
    }

    public void testLogInf() {
        TestUtils.assertEquals(new Complex(inf, pi / 2),
                oneInf.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, -pi / 2),
                oneNegInf.log(), 10e-12);
        TestUtils.assertEquals(infZero, infOne.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, pi),
                negInfOne.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, pi / 4),
                infInf.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, -pi / 4),
                infNegInf.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, 3d * pi / 4),
                negInfInf.log(), 10e-12);
        TestUtils.assertEquals(new Complex(inf, - 3d * pi / 4),
                negInfNegInf.log(), 10e-12);
    }

    public void testLogZero() {
        TestUtils.assertSame(negInfZero, Complex.ZERO.log());
    }

    public void testPow() {
        Complex x = new Complex(3, 4);
        Complex y = new Complex(5, 6);
        Complex expected = new Complex(-1.860893, 11.83677);
        TestUtils.assertEquals(expected, x.pow(y), 1.0e-5);
    }

    public void testPowNaNBase() {
        Complex x = new Complex(3, 4);
        assertTrue(Complex.NaN.pow(x).isNaN());
    }

    public void testPowNaNExponent() {
        Complex x = new Complex(3, 4);
        assertTrue(x.pow(Complex.NaN).isNaN());
    }

   public void testPowInf() {
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(oneInf));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(oneNegInf));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(infOne));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(infInf));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(infNegInf));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(negInfInf));
       TestUtils.assertSame(Complex.NaN,Complex.ONE.pow(negInfNegInf));
       TestUtils.assertSame(Complex.NaN,infOne.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,negInfOne.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,infInf.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,infNegInf.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,negInfInf.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,negInfNegInf.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,negInfNegInf.pow(infNegInf));
       TestUtils.assertSame(Complex.NaN,negInfNegInf.pow(negInfNegInf));
       TestUtils.assertSame(Complex.NaN,negInfNegInf.pow(infInf));
       TestUtils.assertSame(Complex.NaN,infInf.pow(infNegInf));
       TestUtils.assertSame(Complex.NaN,infInf.pow(negInfNegInf));
       TestUtils.assertSame(Complex.NaN,infInf.pow(infInf));
       TestUtils.assertSame(Complex.NaN,infNegInf.pow(infNegInf));
       TestUtils.assertSame(Complex.NaN,infNegInf.pow(negInfNegInf));
       TestUtils.assertSame(Complex.NaN,infNegInf.pow(infInf));
   }

   public void testPowZero() {
       TestUtils.assertSame(Complex.NaN,
               Complex.ZERO.pow(Complex.ONE));
       TestUtils.assertSame(Complex.NaN,
               Complex.ZERO.pow(Complex.ZERO));
       TestUtils.assertSame(Complex.NaN,
               Complex.ZERO.pow(Complex.I));
       TestUtils.assertEquals(Complex.ONE,
               Complex.ONE.pow(Complex.ZERO), 10e-12);
       TestUtils.assertEquals(Complex.ONE,
               Complex.I.pow(Complex.ZERO), 10e-12);
       TestUtils.assertEquals(Complex.ONE,
               new Complex(-1, 3).pow(Complex.ZERO), 10e-12);
   }

    public void testpowNull() {
        try {
            Complex.ONE.pow(null);
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testSin() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(3.853738, -27.01681);
        TestUtils.assertEquals(expected, z.sin(), 1.0e-5);
    }

    public void testSinInf() {
        TestUtils.assertSame(infInf, oneInf.sin());
        TestUtils.assertSame(infNegInf, oneNegInf.sin());
        TestUtils.assertSame(Complex.NaN, infOne.sin());
        TestUtils.assertSame(Complex.NaN, negInfOne.sin());
        TestUtils.assertSame(Complex.NaN, infInf.sin());
        TestUtils.assertSame(Complex.NaN, infNegInf.sin());
        TestUtils.assertSame(Complex.NaN, negInfInf.sin());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.sin());
    }

    public void testSinNaN() {
        assertTrue(Complex.NaN.sin().isNaN());
    }

    public void testSinh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-6.54812, -7.61923);
        TestUtils.assertEquals(expected, z.sinh(), 1.0e-5);
    }

    public void testSinhNaN() {
        assertTrue(Complex.NaN.sinh().isNaN());
    }

    public void testSinhInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.sinh());
        TestUtils.assertSame(Complex.NaN, oneNegInf.sinh());
        TestUtils.assertSame(infInf, infOne.sinh());
        TestUtils.assertSame(negInfInf, negInfOne.sinh());
        TestUtils.assertSame(Complex.NaN, infInf.sinh());
        TestUtils.assertSame(Complex.NaN, infNegInf.sinh());
        TestUtils.assertSame(Complex.NaN, negInfInf.sinh());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.sinh());
    }

    public void testSqrtRealPositive() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(2, 1);
        TestUtils.assertEquals(expected, z.sqrt(), 1.0e-5);
    }

    public void testSqrtRealZero() {
        Complex z = new Complex(0.0, 4);
        Complex expected = new Complex(1.41421, 1.41421);
        TestUtils.assertEquals(expected, z.sqrt(), 1.0e-5);
    }

    public void testSqrtRealNegative() {
        Complex z = new Complex(-3.0, 4);
        Complex expected = new Complex(1, 2);
        TestUtils.assertEquals(expected, z.sqrt(), 1.0e-5);
    }

    public void testSqrtImaginaryZero() {
        Complex z = new Complex(-3.0, 0.0);
        Complex expected = new Complex(0.0, 1.73205);
        TestUtils.assertEquals(expected, z.sqrt(), 1.0e-5);
    }

    public void testSqrtImaginaryNegative() {
        Complex z = new Complex(-3.0, -4.0);
        Complex expected = new Complex(1.0, -2.0);
        TestUtils.assertEquals(expected, z.sqrt(), 1.0e-5);
    }

    public void testSqrtPolar() {
        double r = 1;
        for (int i = 0; i < 5; i++) {
            r += i;
            double theta = 0;
            for (int j =0; j < 11; j++) {
                theta += pi /12;
                Complex z = ComplexUtils.polar2Complex(r, theta);
                Complex sqrtz = ComplexUtils.polar2Complex(FastMath.sqrt(r), theta / 2);
                TestUtils.assertEquals(sqrtz, z.sqrt(), 10e-12);
            }
        }
    }

    public void testSqrtNaN() {
        assertTrue(Complex.NaN.sqrt().isNaN());
    }

    public void testSqrtInf() {
        TestUtils.assertSame(infNaN, oneInf.sqrt());
        TestUtils.assertSame(infNaN, oneNegInf.sqrt());
        TestUtils.assertSame(infZero, infOne.sqrt());
        TestUtils.assertSame(zeroInf, negInfOne.sqrt());
        TestUtils.assertSame(infNaN, infInf.sqrt());
        TestUtils.assertSame(infNaN, infNegInf.sqrt());
        TestUtils.assertSame(nanInf, negInfInf.sqrt());
        TestUtils.assertSame(nanNegInf, negInfNegInf.sqrt());
    }

    public void testSqrt1z() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(4.08033, -2.94094);
        TestUtils.assertEquals(expected, z.sqrt1z(), 1.0e-5);
    }

    public void testSqrt1zNaN() {
        assertTrue(Complex.NaN.sqrt1z().isNaN());
    }

    public void testTan() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(-0.000187346, 0.999356);
        TestUtils.assertEquals(expected, z.tan(), 1.0e-5);
    }

    public void testTanNaN() {
        assertTrue(Complex.NaN.tan().isNaN());
    }

    public void testTanInf() {
        TestUtils.assertSame(zeroNaN, oneInf.tan());
        TestUtils.assertSame(zeroNaN, oneNegInf.tan());
        TestUtils.assertSame(Complex.NaN, infOne.tan());
        TestUtils.assertSame(Complex.NaN, negInfOne.tan());
        TestUtils.assertSame(Complex.NaN, infInf.tan());
        TestUtils.assertSame(Complex.NaN, infNegInf.tan());
        TestUtils.assertSame(Complex.NaN, negInfInf.tan());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.tan());
    }

   public void testTanCritical() {
        TestUtils.assertSame(infNaN, new Complex(pi/2, 0).tan());
        TestUtils.assertSame(negInfNaN, new Complex(-pi/2, 0).tan());
    }

    public void testTanh() {
        Complex z = new Complex(3, 4);
        Complex expected = new Complex(1.00071, 0.00490826);
        TestUtils.assertEquals(expected, z.tanh(), 1.0e-5);
    }

    public void testTanhNaN() {
        assertTrue(Complex.NaN.tanh().isNaN());
    }

    public void testTanhInf() {
        TestUtils.assertSame(Complex.NaN, oneInf.tanh());
        TestUtils.assertSame(Complex.NaN, oneNegInf.tanh());
        TestUtils.assertSame(nanZero, infOne.tanh());
        TestUtils.assertSame(nanZero, negInfOne.tanh());
        TestUtils.assertSame(Complex.NaN, infInf.tanh());
        TestUtils.assertSame(Complex.NaN, infNegInf.tanh());
        TestUtils.assertSame(Complex.NaN, negInfInf.tanh());
        TestUtils.assertSame(Complex.NaN, negInfNegInf.tanh());
    }

    public void testTanhCritical() {
        TestUtils.assertSame(nanInf, new Complex(0, pi/2).tanh());
    }

    /** test issue MATH-221 */
    public void testMath221() {
        assertEquals(new Complex(0,-1), new Complex(0,1).multiply(new Complex(-1,0)));
    }

    /**
     * Test: computing <b>third roots</b> of z.
     * <pre>
     * <code>
     * <b>z = -2 + 2 * i</b>
     *   => z_0 =  1      +          i
     *   => z_1 = -1.3660 + 0.3660 * i
     *   => z_2 =  0.3660 - 1.3660 * i
     * </code>
     * </pre>
     */
    public void testNthRoot_normal_thirdRoot() {
        // The complex number we want to compute all third-roots for.
        Complex z = new Complex(-2,2);
        // The List holding all third roots
        Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);
        // Returned Collection must not be empty!
        assertEquals(3, thirdRootsOfZ.length);
        // test z_0
        assertEquals(1.0,                  thirdRootsOfZ[0].getReal(),      1.0e-5);
        assertEquals(1.0,                  thirdRootsOfZ[0].getImaginary(), 1.0e-5);
        // test z_1
        assertEquals(-1.3660254037844386,  thirdRootsOfZ[1].getReal(),      1.0e-5);
        assertEquals(0.36602540378443843,  thirdRootsOfZ[1].getImaginary(), 1.0e-5);
        // test z_2
        assertEquals(0.366025403784439,    thirdRootsOfZ[2].getReal(),      1.0e-5);
        assertEquals(-1.3660254037844384,  thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }


    /**
     * Test: computing <b>fourth roots</b> of z.
     * <pre>
     * <code>
     * <b>z = 5 - 2 * i</b>
     *   => z_0 =  1.5164 - 0.1446 * i
     *   => z_1 =  0.1446 + 1.5164 * i
     *   => z_2 = -1.5164 + 0.1446 * i
     *   => z_3 = -1.5164 - 0.1446 * i
     * </code>
     * </pre>
     */
    public void testNthRoot_normal_fourthRoot() {
        // The complex number we want to compute all third-roots for.
        Complex z = new Complex(5,-2);
        // The List holding all fourth roots
        Complex[] fourthRootsOfZ = z.nthRoot(4).toArray(new Complex[0]);
        // Returned Collection must not be empty!
        assertEquals(4, fourthRootsOfZ.length);
        // test z_0
        assertEquals(1.5164629308487783,     fourthRootsOfZ[0].getReal(),      1.0e-5);
        assertEquals(-0.14469266210702247,   fourthRootsOfZ[0].getImaginary(), 1.0e-5);
        // test z_1
        assertEquals(0.14469266210702256,    fourthRootsOfZ[1].getReal(),      1.0e-5);
        assertEquals(1.5164629308487783,     fourthRootsOfZ[1].getImaginary(), 1.0e-5);
        // test z_2
        assertEquals(-1.5164629308487783,    fourthRootsOfZ[2].getReal(),      1.0e-5);
        assertEquals(0.14469266210702267,    fourthRootsOfZ[2].getImaginary(), 1.0e-5);
        // test z_3
        assertEquals(-0.14469266210702275,   fourthRootsOfZ[3].getReal(),      1.0e-5);
        assertEquals(-1.5164629308487783,    fourthRootsOfZ[3].getImaginary(), 1.0e-5);
    }

    /**
     * Test: computing <b>third roots</b> of z.
     * <pre>
     * <code>
     * <b>z = 8</b>
     *   => z_0 =  2
     *   => z_1 = -1 + 1.73205 * i
     *   => z_2 = -1 - 1.73205 * i
     * </code>
     * </pre>
     */
    public void testNthRoot_cornercase_thirdRoot_imaginaryPartEmpty() {
        // The number 8 has three third roots. One we all already know is the number 2.
        // But there are two more complex roots.
        Complex z = new Complex(8,0);
        // The List holding all third roots
        Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);
        // Returned Collection must not be empty!
        assertEquals(3, thirdRootsOfZ.length);
        // test z_0
        assertEquals(2.0,                thirdRootsOfZ[0].getReal(),      1.0e-5);
        assertEquals(0.0,                thirdRootsOfZ[0].getImaginary(), 1.0e-5);
        // test z_1
        assertEquals(-1.0,               thirdRootsOfZ[1].getReal(),      1.0e-5);
        assertEquals(1.7320508075688774, thirdRootsOfZ[1].getImaginary(), 1.0e-5);
        // test z_2
        assertEquals(-1.0,               thirdRootsOfZ[2].getReal(),      1.0e-5);
        assertEquals(-1.732050807568877, thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }


    /**
     * Test: computing <b>third roots</b> of z with real part 0.
     * <pre>
     * <code>
     * <b>z = 2 * i</b>
     *   => z_0 =  1.0911 + 0.6299 * i
     *   => z_1 = -1.0911 + 0.6299 * i
     *   => z_2 = -2.3144 - 1.2599 * i
     * </code>
     * </pre>
     */
    public void testNthRoot_cornercase_thirdRoot_realPartZero() {
        // complex number with only imaginary part
        Complex z = new Complex(0,2);
        // The List holding all third roots
        Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);
        // Returned Collection must not be empty!
        assertEquals(3, thirdRootsOfZ.length);
        // test z_0
        assertEquals(1.0911236359717216,      thirdRootsOfZ[0].getReal(),      1.0e-5);
        assertEquals(0.6299605249474365,      thirdRootsOfZ[0].getImaginary(), 1.0e-5);
        // test z_1
        assertEquals(-1.0911236359717216,     thirdRootsOfZ[1].getReal(),      1.0e-5);
        assertEquals(0.6299605249474365,      thirdRootsOfZ[1].getImaginary(), 1.0e-5);
        // test z_2
        assertEquals(-2.3144374213981936E-16, thirdRootsOfZ[2].getReal(),      1.0e-5);
        assertEquals(-1.2599210498948732,     thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }

    /**
     * Test cornercases with NaN and Infinity.
     */
    public void testNthRoot_cornercase_NAN_Inf() {
        // NaN + finite -> NaN
        List<Complex> roots = oneNaN.nthRoot(3);
        assertEquals(1,roots.size());
        assertEquals(Complex.NaN, roots.get(0));

        roots = nanZero.nthRoot(3);
        assertEquals(1,roots.size());
        assertEquals(Complex.NaN, roots.get(0));

        // NaN + infinite -> NaN
        roots = nanInf.nthRoot(3);
        assertEquals(1,roots.size());
        assertEquals(Complex.NaN, roots.get(0));

        // finite + infinite -> Inf
        roots = oneInf.nthRoot(3);
        assertEquals(1,roots.size());
        assertEquals(Complex.INF, roots.get(0));

        // infinite + infinite -> Inf
        roots = negInfInf.nthRoot(3);
        assertEquals(1,roots.size());
        assertEquals(Complex.INF, roots.get(0));
    }

    /**
     * Test standard values
     */
    public void testGetArgument() {
        Complex z = new Complex(1, 0);
        assertEquals(0.0, z.getArgument(), 1.0e-12);

        z = new Complex(1, 1);
        assertEquals(FastMath.PI/4, z.getArgument(), 1.0e-12);

        z = new Complex(0, 1);
        assertEquals(FastMath.PI/2, z.getArgument(), 1.0e-12);

        z = new Complex(-1, 1);
        assertEquals(3 * FastMath.PI/4, z.getArgument(), 1.0e-12);

        z = new Complex(-1, 0);
        assertEquals(FastMath.PI, z.getArgument(), 1.0e-12);

        z = new Complex(-1, -1);
        assertEquals(-3 * FastMath.PI/4, z.getArgument(), 1.0e-12);

        z = new Complex(0, -1);
        assertEquals(-FastMath.PI/2, z.getArgument(), 1.0e-12);

        z = new Complex(1, -1);
        assertEquals(-FastMath.PI/4, z.getArgument(), 1.0e-12);

    }

    /**
     * Verify atan2-style handling of infinite parts
     */
    public void testGetArgumentInf() {
        assertEquals(FastMath.PI/4, infInf.getArgument(), 1.0e-12);
        assertEquals(FastMath.PI/2, oneInf.getArgument(), 1.0e-12);
        assertEquals(0.0, infOne.getArgument(), 1.0e-12);
        assertEquals(FastMath.PI/2, zeroInf.getArgument(), 1.0e-12);
        assertEquals(0.0, infZero.getArgument(), 1.0e-12);
        assertEquals(FastMath.PI, negInfOne.getArgument(), 1.0e-12);
        assertEquals(-3.0*FastMath.PI/4, negInfNegInf.getArgument(), 1.0e-12);
        assertEquals(-FastMath.PI/2, oneNegInf.getArgument(), 1.0e-12);
    }

    /**
     * Verify that either part NaN results in NaN
     */
    public void testGetArgumentNaN() {
        assertEquals(nan, nanZero.getArgument());
        assertEquals(nan, zeroNaN.getArgument());
        assertEquals(nan, Complex.NaN.getArgument());
    }

    public void testSerial() {
        Complex z = new Complex(3.0, 4.0);
        assertEquals(z, TestUtils.serializeAndRecover(z));
        Complex ncmplx = (Complex)TestUtils.serializeAndRecover(oneNaN);
        assertEquals(nanZero, ncmplx);
        assertTrue(ncmplx.isNaN());
        Complex infcmplx = (Complex)TestUtils.serializeAndRecover(infInf);
        assertEquals(infInf, infcmplx);
        assertTrue(infcmplx.isInfinite());
        TestComplex tz = new TestComplex(3.0, 4.0);
        assertEquals(tz, TestUtils.serializeAndRecover(tz));
        TestComplex ntcmplx = (TestComplex)TestUtils.serializeAndRecover(new TestComplex(oneNaN));
        assertEquals(nanZero, ntcmplx);
        assertTrue(ntcmplx.isNaN());
        TestComplex inftcmplx = (TestComplex)TestUtils.serializeAndRecover(new TestComplex(infInf));
        assertEquals(infInf, inftcmplx);
        assertTrue(inftcmplx.isInfinite());
    }

    /**
     * Class to test extending Complex
     */
    public static class TestComplex extends Complex {

        /**
         * Serialization identifier.
         */
        private static final long serialVersionUID = 3268726724160389237L;

        public TestComplex(double real, double imaginary) {
            super(real, imaginary);
        }

        public TestComplex(Complex other){
            this(other.getReal(), other.getImaginary());
        }

        @Override
        protected TestComplex createComplex(double real, double imaginary){
            return new TestComplex(real, imaginary);
        }

    }

}
