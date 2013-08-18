/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.exception.NonMonotonousSequenceException;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * Test cases for the MathUtils class.
 * @version $Revision: 1070122 $ $Date: 2007-08-16 15:36:33 -0500 (Thu, 16 Aug
 *          2007) $
 */
public final class MathUtilsTest extends TestCase {

    public MathUtilsTest(String name) {
        super(name);
    }

    /** cached binomial coefficients */
    private static final List<Map<Integer, Long>> binomialCache = new ArrayList<Map<Integer, Long>>();

    /**
     * Exact (caching) recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) throws ArithmeticException {
        if (binomialCache.size() > n) {
            Long cachedResult = binomialCache.get(n).get(Integer.valueOf(k));
            if (cachedResult != null) {
                return cachedResult.longValue();
            }
        }
        long result = -1;
        if ((n == k) || (k == 0)) {
            result = 1;
        } else if ((k == 1) || (k == n - 1)) {
            result = n;
        } else {
            // Reduce stack depth for larger values of n
            if (k < n - 100) {
                binomialCoefficient(n - 100, k);
            }
            if (k > 100) {
                binomialCoefficient(n - 100, k - 100);
            }
            result = MathUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
                binomialCoefficient(n - 1, k));
        }
        if (result == -1) {
            throw new ArithmeticException(
                "error computing binomial coefficient");
        }
        for (int i = binomialCache.size(); i < n + 1; i++) {
            binomialCache.add(new HashMap<Integer, Long>());
        }
        binomialCache.get(n).put(Integer.valueOf(k), Long.valueOf(result));
        return result;
    }

    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /** Verify that b(0,0) = 1 */
    public void test0Choose0() {
        assertEquals(MathUtils.binomialCoefficientDouble(0, 0), 1d, 0);
        assertEquals(MathUtils.binomialCoefficientLog(0, 0), 0d, 0);
        assertEquals(MathUtils.binomialCoefficient(0, 0), 1);
    }

    public void testAddAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        assertEquals(big, MathUtils.addAndCheck(big, 0));
        try {
            MathUtils.addAndCheck(big, 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
        try {
            MathUtils.addAndCheck(bigNeg, -1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
    }

    public void testAddAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        assertEquals(max, MathUtils.addAndCheck(max, 0L));
        assertEquals(min, MathUtils.addAndCheck(min, 0L));
        assertEquals(max, MathUtils.addAndCheck(0L, max));
        assertEquals(min, MathUtils.addAndCheck(0L, min));
        assertEquals(1, MathUtils.addAndCheck(-1L, 2L));
        assertEquals(1, MathUtils.addAndCheck(2L, -1L));
        assertEquals(-3, MathUtils.addAndCheck(-2L, -1L));
        assertEquals(min, MathUtils.addAndCheck(min + 1, -1L));
        testAddAndCheckLongFailure(max, 1L);
        testAddAndCheckLongFailure(min, -1L);
        testAddAndCheckLongFailure(1L, max);
        testAddAndCheckLongFailure(-1L, min);
    }

    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.addAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }
    }

    public void testBinomialCoefficient() {
        long[] bcoef5 = {
            1,
            5,
            10,
            10,
            5,
            1 };
        long[] bcoef6 = {
            1,
            6,
            15,
            20,
            15,
            6,
            1 };
        for (int i = 0; i < 6; i++) {
            assertEquals("5 choose " + i, bcoef5[i], MathUtils.binomialCoefficient(5, i));
        }
        for (int i = 0; i < 7; i++) {
            assertEquals("6 choose " + i, bcoef6[i], MathUtils.binomialCoefficient(6, i));
        }

        for (int n = 1; n < 10; n++) {
            for (int k = 0; k <= n; k++) {
                assertEquals(n + " choose " + k, binomialCoefficient(n, k), MathUtils.binomialCoefficient(n, k));
                assertEquals(n + " choose " + k, binomialCoefficient(n, k), MathUtils.binomialCoefficientDouble(n, k), Double.MIN_VALUE);
                assertEquals(n + " choose " + k, FastMath.log(binomialCoefficient(n, k)), MathUtils.binomialCoefficientLog(n, k), 10E-12);
            }
        }

        int[] n = { 34, 66, 100, 1500, 1500 };
        int[] k = { 17, 33, 10, 1500 - 4, 4 };
        for (int i = 0; i < n.length; i++) {
            long expected = binomialCoefficient(n[i], k[i]);
            assertEquals(n[i] + " choose " + k[i], expected,
                MathUtils.binomialCoefficient(n[i], k[i]));
            assertEquals(n[i] + " choose " + k[i], expected,
                MathUtils.binomialCoefficientDouble(n[i], k[i]), 0.0);
            assertEquals("log(" + n[i] + " choose " + k[i] + ")", FastMath.log(expected),
                MathUtils.binomialCoefficientLog(n[i], k[i]), 0.0);
        }
    }

    /**
     * Tests correctness for large n and sharpness of upper bound in API doc
     * JIRA: MATH-241
     */
    public void testBinomialCoefficientLarge() throws Exception {
        // This tests all legal and illegal values for n <= 200.
        for (int n = 0; n <= 200; n++) {
            for (int k = 0; k <= n; k++) {
                long ourResult = -1;
                long exactResult = -1;
                boolean shouldThrow = false;
                boolean didThrow = false;
                try {
                    ourResult = MathUtils.binomialCoefficient(n, k);
                } catch (ArithmeticException ex) {
                    didThrow = true;
                }
                try {
                    exactResult = binomialCoefficient(n, k);
                } catch (ArithmeticException ex) {
                    shouldThrow = true;
                }
                assertEquals(n + " choose " + k, exactResult, ourResult);
                assertEquals(n + " choose " + k, shouldThrow, didThrow);
                assertTrue(n + " choose " + k, (n > 66 || !didThrow));

                if (!shouldThrow && exactResult > 1) {
                    assertEquals(n + " choose " + k, 1.,
                        MathUtils.binomialCoefficientDouble(n, k) / exactResult, 1e-10);
                    assertEquals(n + " choose " + k, 1,
                        MathUtils.binomialCoefficientLog(n, k) / FastMath.log(exactResult), 1e-10);
                }
            }
        }

        long ourResult = MathUtils.binomialCoefficient(300, 3);
        long exactResult = binomialCoefficient(300, 3);
        assertEquals(exactResult, ourResult);

        ourResult = MathUtils.binomialCoefficient(700, 697);
        exactResult = binomialCoefficient(700, 697);
        assertEquals(exactResult, ourResult);

        // This one should throw
        try {
            MathUtils.binomialCoefficient(700, 300);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // Expected
        }

        int n = 10000;
        ourResult = MathUtils.binomialCoefficient(n, 3);
        exactResult = binomialCoefficient(n, 3);
        assertEquals(exactResult, ourResult);
        assertEquals(1, MathUtils.binomialCoefficientDouble(n, 3) / exactResult, 1e-10);
        assertEquals(1, MathUtils.binomialCoefficientLog(n, 3) / FastMath.log(exactResult), 1e-10);

    }

    public void testBinomialCoefficientFail() {
        try {
            MathUtils.binomialCoefficient(4, 5);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficientDouble(4, 5);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficientLog(4, 5);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficient(-1, -2);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficientDouble(-1, -2);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficientLog(-1, -2);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }

        try {
            MathUtils.binomialCoefficient(67, 30);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // ignored
        }
        try {
            MathUtils.binomialCoefficient(67, 34);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // ignored
        }
        double x = MathUtils.binomialCoefficientDouble(1030, 515);
        assertTrue("expecting infinite binomial coefficient", Double
            .isInfinite(x));
    }

    public void testCompareTo() {
      assertEquals(0, MathUtils.compareTo(152.33, 152.32, .011));
      assertTrue(MathUtils.compareTo(152.308, 152.32, .011) < 0);
      assertTrue(MathUtils.compareTo(152.33, 152.318, .011) > 0);
    }

    public void testCosh() {
        double x = 3.0;
        double expected = 10.06766;
        assertEquals(expected, MathUtils.cosh(x), 1.0e-5);
    }

    public void testCoshNaN() {
        assertTrue(Double.isNaN(MathUtils.cosh(Double.NaN)));
    }

    public void testEquals() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    assertTrue(MathUtils.equals(testArray[i], testArray[j]));
                    assertTrue(MathUtils.equals(testArray[j], testArray[i]));
                } else {
                    assertTrue(!MathUtils.equals(testArray[i], testArray[j]));
                    assertTrue(!MathUtils.equals(testArray[j], testArray[i]));
                }
            }
        }
    }

    public void testEqualsIncludingNaN() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    assertTrue(MathUtils.equalsIncludingNaN(testArray[i], testArray[j]));
                    assertTrue(MathUtils.equalsIncludingNaN(testArray[j], testArray[i]));
                } else {
                    assertTrue(!MathUtils.equalsIncludingNaN(testArray[i], testArray[j]));
                    assertTrue(!MathUtils.equalsIncludingNaN(testArray[j], testArray[i]));
                }
            }
        }
    }

    public void testEqualsWithAllowedDelta() {
        assertTrue(MathUtils.equals(153.0000, 153.0000, .0625));
        assertTrue(MathUtils.equals(153.0000, 153.0625, .0625));
        assertTrue(MathUtils.equals(152.9375, 153.0000, .0625));
        assertTrue(MathUtils.equals(Double.NaN, Double.NaN, 1.0)); // This will change in 3.0
        assertTrue(MathUtils.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        assertTrue(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        assertFalse(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        assertFalse(MathUtils.equals(153.0000, 153.0625, .0624));
        assertFalse(MathUtils.equals(152.9374, 153.0000, .0625));
    }

    public void testEqualsIncludingNaNWithAllowedDelta() {
        assertTrue(MathUtils.equalsIncludingNaN(153.0000, 153.0000, .0625));
        assertTrue(MathUtils.equalsIncludingNaN(153.0000, 153.0625, .0625));
        assertTrue(MathUtils.equalsIncludingNaN(152.9375, 153.0000, .0625));
        assertTrue(MathUtils.equalsIncludingNaN(Double.NaN, Double.NaN, 1.0));
        assertTrue(MathUtils.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        assertTrue(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        assertFalse(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        assertFalse(MathUtils.equalsIncludingNaN(153.0000, 153.0625, .0624));
        assertFalse(MathUtils.equalsIncludingNaN(152.9374, 153.0000, .0625));
    }

    // Tests for floating point equality
    @SuppressWarnings("deprecation") // Math.equals(float, float)
    public void testFloatEqualsWithAllowedUlps() {
        assertTrue("+0.0f == -0.0f",MathUtils.equals(0.0f, -0.0f));
        assertTrue("+0.0f == -0.0f (1 ulp)",MathUtils.equals(0.0f, -0.0f, 1));
        float oneFloat = 1.0f;
        // Deprecated method - requires parameters to be strictly equal
        assertFalse("1.0f != 1.0f + 1 ulp",MathUtils.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat))));
        assertTrue("1.0f == 1.0f + 1 ulp (1 ulp)",MathUtils.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat)), 1));
        assertFalse("1.0f != 1.0f + 2 ulp (1 ulp)",MathUtils.equals(oneFloat, Float.intBitsToFloat(2 + Float.floatToIntBits(oneFloat)), 1));

        assertTrue(MathUtils.equals(153.0f, 153.0f, 1));

        // These tests need adjusting for floating point precision
//        assertTrue(MathUtils.equals(153.0f, 153.00000000000003f, 1));
//        assertFalse(MathUtils.equals(153.0f, 153.00000000000006f, 1));
//        assertTrue(MathUtils.equals(153.0f, 152.99999999999997f, 1));
//        assertFalse(MathUtils.equals(153f, 152.99999999999994f, 1));
//
//        assertTrue(MathUtils.equals(-128.0f, -127.99999999999999f, 1));
//        assertFalse(MathUtils.equals(-128.0f, -127.99999999999997f, 1));
//        assertTrue(MathUtils.equals(-128.0f, -128.00000000000003f, 1));
//        assertFalse(MathUtils.equals(-128.0f, -128.00000000000006f, 1));

        assertTrue(MathUtils.equals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(Double.MAX_VALUE, Float.POSITIVE_INFINITY, 1));

        assertTrue(MathUtils.equals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, 1));

        assertFalse(MathUtils.equals(Float.NaN, Float.NaN, 1));

        assertFalse(MathUtils.equals(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 100000));
    }

    public void testEqualsWithAllowedUlps21() { // From 2.1
        assertTrue(MathUtils.equals(153, 153, 1));

        assertTrue(MathUtils.equals(153, 153.00000000000003, 1));
        assertFalse(MathUtils.equals(153, 153.00000000000006, 1));
        assertTrue(MathUtils.equals(153, 152.99999999999997, 1));
        assertFalse(MathUtils.equals(153, 152.99999999999994, 1));

        assertTrue(MathUtils.equals(-128, -127.99999999999999, 1));
        assertFalse(MathUtils.equals(-128, -127.99999999999997, 1));
        assertTrue(MathUtils.equals(-128, -128.00000000000003, 1));
        assertFalse(MathUtils.equals(-128, -128.00000000000006, 1));

        assertTrue(MathUtils.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        assertTrue(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));


        assertTrue(MathUtils.equals(Double.NaN, Double.NaN, 1));

        assertFalse(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    public void testEqualsWithAllowedUlps() {
        assertTrue(MathUtils.equals(0.0, -0.0, 1));

        assertTrue(MathUtils.equals(1.0, 1 + FastMath.ulp(1d), 1));
        assertFalse(MathUtils.equals(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        assertTrue(MathUtils.equals(1.0, nUp1, 1));
        assertTrue(MathUtils.equals(nUp1, nnUp1, 1));
        assertFalse(MathUtils.equals(1.0, nnUp1, 1));

        assertTrue(MathUtils.equals(0.0, FastMath.ulp(0d), 1));
        assertTrue(MathUtils.equals(0.0, -FastMath.ulp(0d), 1));

        assertTrue(MathUtils.equals(153.0, 153.0, 1));

        assertTrue(MathUtils.equals(153.0, 153.00000000000003, 1));
        assertFalse(MathUtils.equals(153.0, 153.00000000000006, 1));
        assertTrue(MathUtils.equals(153.0, 152.99999999999997, 1));
        assertFalse(MathUtils.equals(153, 152.99999999999994, 1));

        assertTrue(MathUtils.equals(-128.0, -127.99999999999999, 1));
        assertFalse(MathUtils.equals(-128.0, -127.99999999999997, 1));
        assertTrue(MathUtils.equals(-128.0, -128.00000000000003, 1));
        assertFalse(MathUtils.equals(-128.0, -128.00000000000006, 1));

        assertTrue(MathUtils.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        assertTrue(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        assertTrue(MathUtils.equals(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        assertTrue(MathUtils.equals(Double.NaN, Double.NaN, 1)); // This will change in 3.0

        assertFalse(MathUtils.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    public void testEqualsIncludingNaNWithAllowedUlps() {
        assertTrue(MathUtils.equalsIncludingNaN(0.0, -0.0, 1));

        assertTrue(MathUtils.equalsIncludingNaN(1.0, 1 + FastMath.ulp(1d), 1));
        assertFalse(MathUtils.equalsIncludingNaN(1.0, 1 + 2 * FastMath.ulp(1d), 1));

        final double nUp1 = FastMath.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = FastMath.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        assertTrue(MathUtils.equalsIncludingNaN(1.0, nUp1, 1));
        assertTrue(MathUtils.equalsIncludingNaN(nUp1, nnUp1, 1));
        assertFalse(MathUtils.equalsIncludingNaN(1.0, nnUp1, 1));

        assertTrue(MathUtils.equalsIncludingNaN(0.0, FastMath.ulp(0d), 1));
        assertTrue(MathUtils.equalsIncludingNaN(0.0, -FastMath.ulp(0d), 1));

        assertTrue(MathUtils.equalsIncludingNaN(153.0, 153.0, 1));

        assertTrue(MathUtils.equalsIncludingNaN(153.0, 153.00000000000003, 1));
        assertFalse(MathUtils.equalsIncludingNaN(153.0, 153.00000000000006, 1));
        assertTrue(MathUtils.equalsIncludingNaN(153.0, 152.99999999999997, 1));
        assertFalse(MathUtils.equalsIncludingNaN(153, 152.99999999999994, 1));

        assertTrue(MathUtils.equalsIncludingNaN(-128.0, -127.99999999999999, 1));
        assertFalse(MathUtils.equalsIncludingNaN(-128.0, -127.99999999999997, 1));
        assertTrue(MathUtils.equalsIncludingNaN(-128.0, -128.00000000000003, 1));
        assertFalse(MathUtils.equalsIncludingNaN(-128.0, -128.00000000000006, 1));

        assertTrue(MathUtils.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        assertTrue(MathUtils.equalsIncludingNaN(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        assertTrue(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        assertTrue(MathUtils.equalsIncludingNaN(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        assertTrue(MathUtils.equalsIncludingNaN(Double.NaN, Double.NaN, 1));

        assertFalse(MathUtils.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @SuppressWarnings("deprecation") // specific tests of deprecated methods
    public void testArrayEquals() {
        assertFalse(MathUtils.equals(new double[] { 1d }, null));
        assertFalse(MathUtils.equals(null, new double[] { 1d }));
        assertTrue(MathUtils.equals((double[]) null, (double[]) null));

        assertFalse(MathUtils.equals(new double[] { 1d }, new double[0]));
        assertTrue(MathUtils.equals(new double[] { 1d }, new double[] { 1d }));
        assertTrue(MathUtils.equals(new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }, new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }));
        assertFalse(MathUtils.equals(new double[] { Double.POSITIVE_INFINITY },
                                     new double[] { Double.NEGATIVE_INFINITY }));
        assertFalse(MathUtils.equals(new double[] { 1d },
                                     new double[] { FastMath.nextAfter(1d, 2d) }));

    }

    public void testArrayEqualsIncludingNaN() {
        assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d }, null));
        assertFalse(MathUtils.equalsIncludingNaN(null, new double[] { 1d }));
        assertTrue(MathUtils.equalsIncludingNaN((double[]) null, (double[]) null));

        assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d }, new double[0]));
        assertTrue(MathUtils.equalsIncludingNaN(new double[] { 1d }, new double[] { 1d }));
        assertTrue(MathUtils.equalsIncludingNaN(new double[] {
                    Double.NaN, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, 1d, 0d
                }, new double[] {
                    Double.NaN, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, 1d, 0d
                }));
        assertFalse(MathUtils.equalsIncludingNaN(new double[] { Double.POSITIVE_INFINITY },
                                                 new double[] { Double.NEGATIVE_INFINITY }));
        assertFalse(MathUtils.equalsIncludingNaN(new double[] { 1d },
                                                 new double[] { FastMath.nextAfter(FastMath.nextAfter(1d, 2d), 2d) }));
    }

    public void testFloatArrayEqualsIncludingNaN() {
        assertFalse(MathUtils.equalsIncludingNaN(new float[] { 1f }, null));
        assertFalse(MathUtils.equalsIncludingNaN(null, new float[] { 1f }));
        assertTrue(MathUtils.equalsIncludingNaN((float[]) null, (float[]) null));

        assertFalse(MathUtils.equalsIncludingNaN(new float[] { 1f }, new float[0]));
        assertTrue(MathUtils.equalsIncludingNaN(new float[] { 1f }, new float[] { 1f }));
        assertTrue(MathUtils.equalsIncludingNaN(new float[] {
                    Float.NaN, Float.POSITIVE_INFINITY,
                    Float.NEGATIVE_INFINITY, 1f, 0f
                }, new float[] {
                Float.NaN, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, 1f, 0f
                }));
        assertFalse(MathUtils.equalsIncludingNaN(new float[] { Float.POSITIVE_INFINITY },
                                                 new float[] { Float.NEGATIVE_INFINITY }));
//        assertFalse(MathUtils.equalsIncludingNaN(new float[] { 1f },
//                                                 new float[] { FastMath.nextAfter(FastMath.nextAfter(1f, 2f), 2f) }));
    }

    public void testFactorial() {
        for (int i = 1; i < 21; i++) {
            assertEquals(i + "! ", factorial(i), MathUtils.factorial(i));
            assertEquals(i + "! ", factorial(i), MathUtils.factorialDouble(i), Double.MIN_VALUE);
            assertEquals(i + "! ", FastMath.log(factorial(i)), MathUtils.factorialLog(i), 10E-12);
        }

        assertEquals("0", 1, MathUtils.factorial(0));
        assertEquals("0", 1.0d, MathUtils.factorialDouble(0), 1E-14);
        assertEquals("0", 0.0d, MathUtils.factorialLog(0), 1E-14);
    }

    public void testFactorialFail() {
        try {
            MathUtils.factorial(-1);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorialDouble(-1);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorialLog(-1);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            MathUtils.factorial(21);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // ignored
        }
        assertTrue("expecting infinite factorial value", Double.isInfinite(MathUtils.factorialDouble(171)));
    }

    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        assertEquals(0, MathUtils.gcd(0, 0));

        assertEquals(b, MathUtils.gcd(0, b));
        assertEquals(a, MathUtils.gcd(a, 0));
        assertEquals(b, MathUtils.gcd(0, -b));
        assertEquals(a, MathUtils.gcd(-a, 0));

        assertEquals(10, MathUtils.gcd(a, b));
        assertEquals(10, MathUtils.gcd(-a, b));
        assertEquals(10, MathUtils.gcd(a, -b));
        assertEquals(10, MathUtils.gcd(-a, -b));

        assertEquals(1, MathUtils.gcd(a, c));
        assertEquals(1, MathUtils.gcd(-a, c));
        assertEquals(1, MathUtils.gcd(a, -c));
        assertEquals(1, MathUtils.gcd(-a, -c));

        assertEquals(3 * (1<<15), MathUtils.gcd(3 * (1<<20), 9 * (1<<15)));

        assertEquals(Integer.MAX_VALUE, MathUtils.gcd(Integer.MAX_VALUE, 0));
        assertEquals(Integer.MAX_VALUE, MathUtils.gcd(-Integer.MAX_VALUE, 0));
        assertEquals(1<<30, MathUtils.gcd(1<<30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            MathUtils.gcd(Integer.MIN_VALUE, 0);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            MathUtils.gcd(0, Integer.MIN_VALUE);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            MathUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    public void  testGcdLong(){
        long a = 30;
        long b = 50;
        long c = 77;

        assertEquals(0, MathUtils.gcd(0L, 0));

        assertEquals(b, MathUtils.gcd(0, b));
        assertEquals(a, MathUtils.gcd(a, 0));
        assertEquals(b, MathUtils.gcd(0, -b));
        assertEquals(a, MathUtils.gcd(-a, 0));

        assertEquals(10, MathUtils.gcd(a, b));
        assertEquals(10, MathUtils.gcd(-a, b));
        assertEquals(10, MathUtils.gcd(a, -b));
        assertEquals(10, MathUtils.gcd(-a, -b));

        assertEquals(1, MathUtils.gcd(a, c));
        assertEquals(1, MathUtils.gcd(-a, c));
        assertEquals(1, MathUtils.gcd(a, -c));
        assertEquals(1, MathUtils.gcd(-a, -c));

        assertEquals(3L * (1L<<45), MathUtils.gcd(3L * (1L<<50), 9L * (1L<<45)));

        assertEquals(1L<<45, MathUtils.gcd(1L<<45, Long.MIN_VALUE));

        assertEquals(Long.MAX_VALUE, MathUtils.gcd(Long.MAX_VALUE, 0L));
        assertEquals(Long.MAX_VALUE, MathUtils.gcd(-Long.MAX_VALUE, 0L));
        assertEquals(1, MathUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            MathUtils.gcd(Long.MIN_VALUE, 0);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            MathUtils.gcd(0, Long.MIN_VALUE);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            MathUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    public void testGcdConsistency() {
        int[] primeList = {19, 23, 53, 67, 73, 79, 101, 103, 111, 131};
        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 0; i < primeList.length; i++) {
            primes.add(Integer.valueOf(primeList[i]));
        }
        RandomDataImpl randomData = new RandomDataImpl();
        for (int i = 0; i < 20; i++) {
            Object[] sample = randomData.nextSample(primes, 4);
            int p1 = ((Integer) sample[0]).intValue();
            int p2 = ((Integer) sample[1]).intValue();
            int p3 = ((Integer) sample[2]).intValue();
            int p4 = ((Integer) sample[3]).intValue();
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            assertEquals(gcd, MathUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            assertEquals(gcd, MathUtils.gcd(l1, l2));
        }
    }

    public void testHash() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d,
            1E-14,
            (1 + 1E-14),
            Double.MIN_VALUE,
            Double.MAX_VALUE };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    assertEquals(MathUtils.hash(testArray[i]), MathUtils.hash(testArray[j]));
                    assertEquals(MathUtils.hash(testArray[j]), MathUtils.hash(testArray[i]));
                } else {
                    assertTrue(MathUtils.hash(testArray[i]) != MathUtils.hash(testArray[j]));
                    assertTrue(MathUtils.hash(testArray[j]) != MathUtils.hash(testArray[i]));
                }
            }
        }
    }

    public void testArrayHash() {
        assertEquals(0, MathUtils.hash((double[]) null));
        assertEquals(MathUtils.hash(new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }),
                     MathUtils.hash(new double[] {
                                      Double.NaN, Double.POSITIVE_INFINITY,
                                      Double.NEGATIVE_INFINITY, 1d, 0d
                                    }));
        assertFalse(MathUtils.hash(new double[] { 1d }) ==
                    MathUtils.hash(new double[] { FastMath.nextAfter(1d, 2d) }));
        assertFalse(MathUtils.hash(new double[] { 1d }) ==
                    MathUtils.hash(new double[] { 1d, 1d }));
    }

    /**
     * Make sure that permuted arrays do not hash to the same value.
     */
    public void testPermutedArrayHash() {
        double[] original = new double[10];
        double[] permuted = new double[10];
        RandomDataImpl random = new RandomDataImpl();

        // Generate 10 distinct random values
        for (int i = 0; i < 10; i++) {
            original[i] = random.nextUniform(i + 0.5, i + 0.75);
        }

        // Generate a random permutation, making sure it is not the identity
        boolean isIdentity = true;
        do {
            int[] permutation = random.nextPermutation(10, 10);
            for (int i = 0; i < 10; i++) {
                if (i != permutation[i]) {
                    isIdentity = false;
                }
                permuted[i] = original[permutation[i]];
            }
        } while (isIdentity);

        // Verify that permuted array has different hash
        assertFalse(MathUtils.hash(original) == MathUtils.hash(permuted));
    }

    public void testIndicatorByte() {
        assertEquals((byte)1, MathUtils.indicator((byte)2));
        assertEquals((byte)1, MathUtils.indicator((byte)0));
        assertEquals((byte)(-1), MathUtils.indicator((byte)(-2)));
    }

    public void testIndicatorDouble() {
        double delta = 0.0;
        assertEquals(1.0, MathUtils.indicator(2.0), delta);
        assertEquals(1.0, MathUtils.indicator(0.0), delta);
        assertEquals(-1.0, MathUtils.indicator(-2.0), delta);
        assertEquals(Double.NaN, MathUtils.indicator(Double.NaN));
    }

    public void testIndicatorFloat() {
        float delta = 0.0F;
        assertEquals(1.0F, MathUtils.indicator(2.0F), delta);
        assertEquals(1.0F, MathUtils.indicator(0.0F), delta);
        assertEquals(-1.0F, MathUtils.indicator(-2.0F), delta);
    }

    public void testIndicatorInt() {
        assertEquals(1, MathUtils.indicator((2)));
        assertEquals(1, MathUtils.indicator((0)));
        assertEquals((-1), MathUtils.indicator((-2)));
    }

    public void testIndicatorLong() {
        assertEquals(1L, MathUtils.indicator(2L));
        assertEquals(1L, MathUtils.indicator(0L));
        assertEquals(-1L, MathUtils.indicator(-2L));
    }

    public void testIndicatorShort() {
        assertEquals((short)1, MathUtils.indicator((short)2));
        assertEquals((short)1, MathUtils.indicator((short)0));
        assertEquals((short)(-1), MathUtils.indicator((short)(-2)));
    }

    public void testLcm() {
        int a = 30;
        int b = 50;
        int c = 77;

        assertEquals(0, MathUtils.lcm(0, b));
        assertEquals(0, MathUtils.lcm(a, 0));
        assertEquals(b, MathUtils.lcm(1, b));
        assertEquals(a, MathUtils.lcm(a, 1));
        assertEquals(150, MathUtils.lcm(a, b));
        assertEquals(150, MathUtils.lcm(-a, b));
        assertEquals(150, MathUtils.lcm(a, -b));
        assertEquals(150, MathUtils.lcm(-a, -b));
        assertEquals(2310, MathUtils.lcm(a, c));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        assertEquals((1<<20)*15, MathUtils.lcm((1<<20)*3, (1<<20)*5));

        // Special case
        assertEquals(0, MathUtils.lcm(0, 0));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Integer.MIN_VALUE, 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Integer.MIN_VALUE, 1<<20);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        try {
            MathUtils.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    public void testLcmLong() {
        long a = 30;
        long b = 50;
        long c = 77;

        assertEquals(0, MathUtils.lcm(0, b));
        assertEquals(0, MathUtils.lcm(a, 0));
        assertEquals(b, MathUtils.lcm(1, b));
        assertEquals(a, MathUtils.lcm(a, 1));
        assertEquals(150, MathUtils.lcm(a, b));
        assertEquals(150, MathUtils.lcm(-a, b));
        assertEquals(150, MathUtils.lcm(a, -b));
        assertEquals(150, MathUtils.lcm(-a, -b));
        assertEquals(2310, MathUtils.lcm(a, c));

        assertEquals(Long.MAX_VALUE, MathUtils.lcm(60247241209L, 153092023L));

        // Assert that no intermediate value overflows:
        // The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
        assertEquals((1L<<50)*15, MathUtils.lcm((1L<<45)*3, (1L<<50)*5));

        // Special case
        assertEquals(0L, MathUtils.lcm(0L, 0L));

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Long.MIN_VALUE, 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        try {
            // lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
            MathUtils.lcm(Long.MIN_VALUE, 1<<20);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }

        assertEquals((long) Integer.MAX_VALUE * (Integer.MAX_VALUE - 1),
            MathUtils.lcm((long)Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        try {
            MathUtils.lcm(Long.MAX_VALUE, Long.MAX_VALUE - 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    public void testLog() {
        assertEquals(2.0, MathUtils.log(2, 4), 0);
        assertEquals(3.0, MathUtils.log(2, 8), 0);
        assertTrue(Double.isNaN(MathUtils.log(-1, 1)));
        assertTrue(Double.isNaN(MathUtils.log(1, -1)));
        assertTrue(Double.isNaN(MathUtils.log(0, 0)));
        assertEquals(0, MathUtils.log(0, 10), 0);
        assertEquals(Double.NEGATIVE_INFINITY, MathUtils.log(10, 0), 0);
    }

    public void testMulAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        assertEquals(big, MathUtils.mulAndCheck(big, 1));
        try {
            MathUtils.mulAndCheck(big, 2);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
        try {
            MathUtils.mulAndCheck(bigNeg, 2);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
    }

    public void testMulAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        assertEquals(max, MathUtils.mulAndCheck(max, 1L));
        assertEquals(min, MathUtils.mulAndCheck(min, 1L));
        assertEquals(0L, MathUtils.mulAndCheck(max, 0L));
        assertEquals(0L, MathUtils.mulAndCheck(min, 0L));
        assertEquals(max, MathUtils.mulAndCheck(1L, max));
        assertEquals(min, MathUtils.mulAndCheck(1L, min));
        assertEquals(0L, MathUtils.mulAndCheck(0L, max));
        assertEquals(0L, MathUtils.mulAndCheck(0L, min));
        assertEquals(1L, MathUtils.mulAndCheck(-1L, -1L));
        assertEquals(min, MathUtils.mulAndCheck(min / 2, 2));
        testMulAndCheckLongFailure(max, 2L);
        testMulAndCheckLongFailure(2L, max);
        testMulAndCheckLongFailure(min, 2L);
        testMulAndCheckLongFailure(2L, min);
        testMulAndCheckLongFailure(min, -1L);
        testMulAndCheckLongFailure(-1L, min);
    }

    private void testMulAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.mulAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }
    }

    public void testNextAfter() { // Test from Math 2.1
        // 0x402fffffffffffff 0x404123456789abcd -> 4030000000000000
        assertEquals(16.0, MathUtils.nextAfter(15.999999999999998, 34.27555555555555), 0.0);

        // 0xc02fffffffffffff 0x404123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, MathUtils.nextAfter(-15.999999999999998, 34.27555555555555), 0.0);

        // 0x402fffffffffffff 0x400123456789abcd -> 402ffffffffffffe
        assertEquals(15.999999999999996, MathUtils.nextAfter(15.999999999999998, 2.142222222222222), 0.0);

        // 0xc02fffffffffffff 0x400123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, MathUtils.nextAfter(-15.999999999999998, 2.142222222222222), 0.0);

        // 0x4020000000000000 0x404123456789abcd -> 4020000000000001
        assertEquals(8.000000000000002, MathUtils.nextAfter(8.0, 34.27555555555555), 0.0);

        // 0xc020000000000000 0x404123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, MathUtils.nextAfter(-8.0, 34.27555555555555), 0.0);

        // 0x4020000000000000 0x400123456789abcd -> 401fffffffffffff
        assertEquals(7.999999999999999, MathUtils.nextAfter(8.0, 2.142222222222222), 0.0);

        // 0xc020000000000000 0x400123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, MathUtils.nextAfter(-8.0, 2.142222222222222), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a224 -> 3f2e43753d36a224
        assertEquals(2.308922399667661E-4, MathUtils.nextAfter(2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a223 -> 3f2e43753d36a224
        assertEquals(2.308922399667661E-4, MathUtils.nextAfter(2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, MathUtils.nextAfter(2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a224 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, MathUtils.nextAfter(2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a223 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, MathUtils.nextAfter(2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, MathUtils.nextAfter(2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a224 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, MathUtils.nextAfter(-2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a223 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, MathUtils.nextAfter(-2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, MathUtils.nextAfter(-2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a224 -> bf2e43753d36a224
        assertEquals(-2.308922399667661E-4, MathUtils.nextAfter(-2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a223 -> bf2e43753d36a224
        assertEquals(-2.308922399667661E-4, MathUtils.nextAfter(-2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, MathUtils.nextAfter(-2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

    }

    public void testNextAfterSpecialCases() {
        assertTrue(Double.isInfinite(MathUtils.nextAfter(Double.NEGATIVE_INFINITY, 0)));
        assertTrue(Double.isInfinite(MathUtils.nextAfter(Double.POSITIVE_INFINITY, 0)));
        assertTrue(Double.isNaN(MathUtils.nextAfter(Double.NaN, 0)));
        assertTrue(Double.isInfinite(MathUtils.nextAfter(Double.MAX_VALUE, Double.POSITIVE_INFINITY)));
        assertTrue(Double.isInfinite(MathUtils.nextAfter(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY)));
        assertEquals(Double.MIN_VALUE, MathUtils.nextAfter(0, 1), 0);
        assertEquals(-Double.MIN_VALUE, MathUtils.nextAfter(0, -1), 0);
        assertEquals(0, MathUtils.nextAfter(Double.MIN_VALUE, -1), 0);
        assertEquals(0, MathUtils.nextAfter(-Double.MIN_VALUE, 1), 0);
    }

    public void testScalb() { // Math 2.1
        assertEquals( 0.0, MathUtils.scalb(0.0, 5), 1.0e-15);
        assertEquals(32.0, MathUtils.scalb(1.0, 5), 1.0e-15);
        assertEquals(1.0 / 32.0, MathUtils.scalb(1.0,  -5), 1.0e-15);
        assertEquals(Math.PI, MathUtils.scalb(Math.PI, 0), 1.0e-15);
        assertTrue(Double.isInfinite(MathUtils.scalb(Double.POSITIVE_INFINITY, 1)));
        assertTrue(Double.isInfinite(MathUtils.scalb(Double.NEGATIVE_INFINITY, 1)));
        assertTrue(Double.isNaN(MathUtils.scalb(Double.NaN, 1)));
    }

    public void testNormalizeAngle() {
        for (double a = -15.0; a <= 15.0; a += 0.1) {
            for (double b = -15.0; b <= 15.0; b += 0.2) {
                double c = MathUtils.normalizeAngle(a, b);
                assertTrue((b - FastMath.PI) <= c);
                assertTrue(c <= (b + FastMath.PI));
                double twoK = FastMath.rint((a - c) / FastMath.PI);
                assertEquals(c, a - twoK * FastMath.PI, 1.0e-14);
            }
        }
    }

    public void testNormalizeArray() {
        double[] testValues1 = new double[] {1, 1, 2};
        TestUtils.assertEquals(
                new double[] {.25, .25, .5},
                MathUtils.normalizeArray(testValues1, 1),
                Double.MIN_VALUE);

        double[] testValues2 = new double[] {-1, -1, 1};
        TestUtils.assertEquals(
                new double[] {1, 1, -1},
                MathUtils.normalizeArray(testValues2, 1),
                Double.MIN_VALUE);

        // Ignore NaNs
        double[] testValues3 = new double[] {-1, -1, Double.NaN, 1, Double.NaN};
        TestUtils.assertEquals(
                new double[] {1, 1,Double.NaN, -1, Double.NaN},
                MathUtils.normalizeArray(testValues3, 1),
                Double.MIN_VALUE);

        // Zero sum -> ArithmeticException
        double[] zeroSum = new double[] {-1, 1};
        try {
            MathUtils.normalizeArray(zeroSum, 1);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        // Infinite elements -> ArithmeticException
        double[] hasInf = new double[] {1, 2, 1, Double.NEGATIVE_INFINITY};
        try {
            MathUtils.normalizeArray(hasInf, 1);
            fail("expecting ArithmeticException");
        } catch (ArithmeticException ex) {}

        // Infinite target -> IllegalArgumentException
        try {
            MathUtils.normalizeArray(testValues1, Double.POSITIVE_INFINITY);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {}

        // NaN target -> IllegalArgumentException
        try {
            MathUtils.normalizeArray(testValues1, Double.NaN);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {}

    }

    public void testRoundDouble() {
        double x = 1.234567890;
        assertEquals(1.23, MathUtils.round(x, 2), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4), 0.0);

        // JIRA MATH-151
        assertEquals(39.25, MathUtils.round(39.245, 2), 0.0);
        assertEquals(39.24, MathUtils.round(39.245, 2, BigDecimal.ROUND_DOWN), 0.0);
        double xx = 39.0;
        xx = xx + 245d / 1000d;
        assertEquals(39.25, MathUtils.round(xx, 2), 0.0);

        // BZ 35904
        assertEquals(30.1d, MathUtils.round(30.095d, 2), 0.0d);
        assertEquals(30.1d, MathUtils.round(30.095d, 1), 0.0d);
        assertEquals(33.1d, MathUtils.round(33.095d, 1), 0.0d);
        assertEquals(33.1d, MathUtils.round(33.095d, 2), 0.0d);
        assertEquals(50.09d, MathUtils.round(50.085d, 2), 0.0d);
        assertEquals(50.19d, MathUtils.round(50.185d, 2), 0.0d);
        assertEquals(50.01d, MathUtils.round(50.005d, 2), 0.0d);
        assertEquals(30.01d, MathUtils.round(30.005d, 2), 0.0d);
        assertEquals(30.65d, MathUtils.round(30.645d, 2), 0.0d);

        assertEquals(1.24, MathUtils.round(x, 2, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.234, MathUtils.round(-x, 3, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.2345, MathUtils.round(-x, 4, BigDecimal.ROUND_CEILING), 0.0);

        assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(1.234, MathUtils.round(x, 3, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(1.2345, MathUtils.round(x, 4, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.234, MathUtils.round(-x, 3, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.2345, MathUtils.round(-x, 4, BigDecimal.ROUND_DOWN), 0.0);

        assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(1.234, MathUtils.round(x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(1.2345, MathUtils.round(x, 4, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.24, MathUtils.round(-x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_FLOOR), 0.0);

        assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.234, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.234, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);

        assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.234, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.234, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.236, MathUtils.round(1.2355, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.236, MathUtils.round(-1.2355, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);

        assertEquals(1.23, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.23, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.235, MathUtils.round(1.2345, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.235, MathUtils.round(-1.2345, 3, BigDecimal.ROUND_HALF_UP), 0.0);

        assertEquals(-1.23, MathUtils.round(-1.23, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);
        assertEquals(1.23, MathUtils.round(1.23, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);

        try {
            MathUtils.round(1.234, 2, BigDecimal.ROUND_UNNECESSARY);
            fail();
        } catch (ArithmeticException ex) {
            // success
        }

        assertEquals(1.24, MathUtils.round(x, 2, BigDecimal.ROUND_UP), 0.0);
        assertEquals(1.235, MathUtils.round(x, 3, BigDecimal.ROUND_UP), 0.0);
        assertEquals(1.2346, MathUtils.round(x, 4, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.24, MathUtils.round(-x, 2, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.235, MathUtils.round(-x, 3, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.2346, MathUtils.round(-x, 4, BigDecimal.ROUND_UP), 0.0);

        try {
            MathUtils.round(1.234, 2, 1923);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }

        // MATH-151
        assertEquals(39.25, MathUtils.round(39.245, 2, BigDecimal.ROUND_HALF_UP), 0.0);

        // special values
        TestUtils.assertEquals(Double.NaN, MathUtils.round(Double.NaN, 2), 0.0);
        assertEquals(0.0, MathUtils.round(0.0, 2), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, MathUtils.round(Double.POSITIVE_INFINITY, 2), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, MathUtils.round(Double.NEGATIVE_INFINITY, 2), 0.0);
    }

    public void testRoundFloat() {
        float x = 1.234567890f;
        assertEquals(1.23f, MathUtils.round(x, 2), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4), 0.0);

        // BZ 35904
        assertEquals(30.1f, MathUtils.round(30.095f, 2), 0.0f);
        assertEquals(30.1f, MathUtils.round(30.095f, 1), 0.0f);
        assertEquals(50.09f, MathUtils.round(50.085f, 2), 0.0f);
        assertEquals(50.19f, MathUtils.round(50.185f, 2), 0.0f);
        assertEquals(50.01f, MathUtils.round(50.005f, 2), 0.0f);
        assertEquals(30.01f, MathUtils.round(30.005f, 2), 0.0f);
        assertEquals(30.65f, MathUtils.round(30.645f, 2), 0.0f);

        assertEquals(1.24f, MathUtils.round(x, 2, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.234f, MathUtils.round(-x, 3, BigDecimal.ROUND_CEILING), 0.0);
        assertEquals(-1.2345f, MathUtils.round(-x, 4, BigDecimal.ROUND_CEILING), 0.0);

        assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(1.234f, MathUtils.round(x, 3, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(1.2345f, MathUtils.round(x, 4, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.234f, MathUtils.round(-x, 3, BigDecimal.ROUND_DOWN), 0.0);
        assertEquals(-1.2345f, MathUtils.round(-x, 4, BigDecimal.ROUND_DOWN), 0.0);

        assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(1.234f, MathUtils.round(x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(1.2345f, MathUtils.round(x, 4, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.24f, MathUtils.round(-x, 2, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_FLOOR), 0.0);
        assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_FLOOR), 0.0);

        assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(1.234f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);
        assertEquals(-1.234f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_DOWN), 0.0);

        assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.234f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.234f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(1.236f, MathUtils.round(1.2355f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);
        assertEquals(-1.236f, MathUtils.round(-1.2355f, 3, BigDecimal.ROUND_HALF_EVEN), 0.0);

        assertEquals(1.23f, MathUtils.round(x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.23f, MathUtils.round(-x, 2, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(1.235f, MathUtils.round(1.2345f, 3, BigDecimal.ROUND_HALF_UP), 0.0);
        assertEquals(-1.235f, MathUtils.round(-1.2345f, 3, BigDecimal.ROUND_HALF_UP), 0.0);

        assertEquals(-1.23f, MathUtils.round(-1.23f, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);
        assertEquals(1.23f, MathUtils.round(1.23f, 2, BigDecimal.ROUND_UNNECESSARY), 0.0);

        try {
            MathUtils.round(1.234f, 2, BigDecimal.ROUND_UNNECESSARY);
            fail();
        } catch (ArithmeticException ex) {
            // success
        }

        assertEquals(1.24f, MathUtils.round(x, 2, BigDecimal.ROUND_UP), 0.0);
        assertEquals(1.235f, MathUtils.round(x, 3, BigDecimal.ROUND_UP), 0.0);
        assertEquals(1.2346f, MathUtils.round(x, 4, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.24f, MathUtils.round(-x, 2, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.235f, MathUtils.round(-x, 3, BigDecimal.ROUND_UP), 0.0);
        assertEquals(-1.2346f, MathUtils.round(-x, 4, BigDecimal.ROUND_UP), 0.0);

        try {
            MathUtils.round(1.234f, 2, 1923);
            fail();
        } catch (IllegalArgumentException ex) {
            // success
        }

        // special values
        TestUtils.assertEquals(Float.NaN, MathUtils.round(Float.NaN, 2), 0.0f);
        assertEquals(0.0f, MathUtils.round(0.0f, 2), 0.0f);
        assertEquals(Float.POSITIVE_INFINITY, MathUtils.round(Float.POSITIVE_INFINITY, 2), 0.0f);
        assertEquals(Float.NEGATIVE_INFINITY, MathUtils.round(Float.NEGATIVE_INFINITY, 2), 0.0f);
    }

    public void testSignByte() {
        assertEquals((byte) 1, MathUtils.sign((byte) 2));
        assertEquals((byte) 0, MathUtils.sign((byte) 0));
        assertEquals((byte) (-1), MathUtils.sign((byte) (-2)));
    }

    public void testSignDouble() {
        double delta = 0.0;
        assertEquals(1.0, MathUtils.sign(2.0), delta);
        assertEquals(0.0, MathUtils.sign(0.0), delta);
        assertEquals(-1.0, MathUtils.sign(-2.0), delta);
        TestUtils.assertSame(-0. / 0., MathUtils.sign(Double.NaN));
    }

    public void testSignFloat() {
        float delta = 0.0F;
        assertEquals(1.0F, MathUtils.sign(2.0F), delta);
        assertEquals(0.0F, MathUtils.sign(0.0F), delta);
        assertEquals(-1.0F, MathUtils.sign(-2.0F), delta);
        TestUtils.assertSame(Float.NaN, MathUtils.sign(Float.NaN));
    }

    public void testSignInt() {
        assertEquals(1, MathUtils.sign(2));
        assertEquals(0, MathUtils.sign(0));
        assertEquals((-1), MathUtils.sign((-2)));
    }

    public void testSignLong() {
        assertEquals(1L, MathUtils.sign(2L));
        assertEquals(0L, MathUtils.sign(0L));
        assertEquals(-1L, MathUtils.sign(-2L));
    }

    public void testSignShort() {
        assertEquals((short) 1, MathUtils.sign((short) 2));
        assertEquals((short) 0, MathUtils.sign((short) 0));
        assertEquals((short) (-1), MathUtils.sign((short) (-2)));
    }

    public void testSinh() {
        double x = 3.0;
        double expected = 10.01787;
        assertEquals(expected, MathUtils.sinh(x), 1.0e-5);
    }

    public void testSinhNaN() {
        assertTrue(Double.isNaN(MathUtils.sinh(Double.NaN)));
    }

    public void testSubAndCheck() {
        int big = Integer.MAX_VALUE;
        int bigNeg = Integer.MIN_VALUE;
        assertEquals(big, MathUtils.subAndCheck(big, 0));
        assertEquals(bigNeg + 1, MathUtils.subAndCheck(bigNeg, -1));
        assertEquals(-1, MathUtils.subAndCheck(bigNeg, -big));
        try {
            MathUtils.subAndCheck(big, -1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
        try {
            MathUtils.subAndCheck(bigNeg, 1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
        }
    }

    public void testSubAndCheckErrorMessage() {
        int big = Integer.MAX_VALUE;
        try {
            MathUtils.subAndCheck(big, -1);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            assertTrue(ex.getMessage().length() > 1);
        }
    }

    public void testSubAndCheckLong() {
        long max = Long.MAX_VALUE;
        long min = Long.MIN_VALUE;
        assertEquals(max, MathUtils.subAndCheck(max, 0));
        assertEquals(min, MathUtils.subAndCheck(min, 0));
        assertEquals(-max, MathUtils.subAndCheck(0, max));
        assertEquals(min + 1, MathUtils.subAndCheck(min, -1));
        // min == -1-max
        assertEquals(-1, MathUtils.subAndCheck(-max - 1, -max));
        assertEquals(max, MathUtils.subAndCheck(-1, -1 - max));
        testSubAndCheckLongFailure(0L, min);
        testSubAndCheckLongFailure(max, -1L);
        testSubAndCheckLongFailure(min, 1L);
    }

    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.subAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }

    }

    public void testPow() {

        assertEquals(1801088541, MathUtils.pow(21, 7));
        assertEquals(1, MathUtils.pow(21, 0));
        try {
            MathUtils.pow(21, -7);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        assertEquals(1801088541, MathUtils.pow(21, 7l));
        assertEquals(1, MathUtils.pow(21, 0l));
        try {
            MathUtils.pow(21, -7l);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        assertEquals(1801088541l, MathUtils.pow(21l, 7));
        assertEquals(1l, MathUtils.pow(21l, 0));
        try {
            MathUtils.pow(21l, -7);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        assertEquals(1801088541l, MathUtils.pow(21l, 7l));
        assertEquals(1l, MathUtils.pow(21l, 0l));
        try {
            MathUtils.pow(21l, -7l);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        BigInteger twentyOne = BigInteger.valueOf(21l);
        assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, 7));
        assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, 0));
        try {
            MathUtils.pow(twentyOne, -7);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, 7l));
        assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, 0l));
        try {
            MathUtils.pow(twentyOne, -7l);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        assertEquals(BigInteger.valueOf(1801088541l), MathUtils.pow(twentyOne, BigInteger.valueOf(7l)));
        assertEquals(BigInteger.ONE, MathUtils.pow(twentyOne, BigInteger.ZERO));
        try {
            MathUtils.pow(twentyOne, BigInteger.valueOf(-7l));
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        BigInteger bigOne =
            new BigInteger("1543786922199448028351389769265814882661837148" +
                           "4763915343722775611762713982220306372888519211" +
                           "560905579993523402015636025177602059044911261");
        assertEquals(bigOne, MathUtils.pow(twentyOne, 103));
        assertEquals(bigOne, MathUtils.pow(twentyOne, 103l));
        assertEquals(bigOne, MathUtils.pow(twentyOne, BigInteger.valueOf(103l)));

    }

    public void testL1DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        assertEquals(7.0, MathUtils.distance1(p1, p2));
    }

    public void testL1DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        assertEquals(7, MathUtils.distance1(p1, p2));
    }

    public void testL2DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        assertEquals(5.0, MathUtils.distance(p1, p2));
    }

    public void testL2DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        assertEquals(5.0, MathUtils.distance(p1, p2));
    }

    public void testLInfDistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        assertEquals(4.0, MathUtils.distanceInf(p1, p2));
    }

    public void testLInfDistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        assertEquals(4, MathUtils.distanceInf(p1, p2));
    }

    public void testCheckOrder21() { // Test from 2.1
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 15}, 1, true);
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 2}, 1, false);
        MathUtils.checkOrder(new double[] {3, -5.5, -11, -27.5}, -1, true);
        MathUtils.checkOrder(new double[] {3, 0, 0, -5.5, -11, -27.5}, -1, false);

        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -1, 2, 15}, 1, true);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -2, 2}, 1, false);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, 3, -5.5, -11, -27.5}, -1, true);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, -1, 0, -5.5, -11, -27.5}, -1, false);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testCheckOrder() {
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 15},
                             MathUtils.OrderDirection.INCREASING, true);
        MathUtils.checkOrder(new double[] {-15, -5.5, -1, 2, 2},
                             MathUtils.OrderDirection.INCREASING, false);
        MathUtils.checkOrder(new double[] {3, -5.5, -11, -27.5},
                             MathUtils.OrderDirection.DECREASING, true);
        MathUtils.checkOrder(new double[] {3, 0, 0, -5.5, -11, -27.5},
                             MathUtils.OrderDirection.DECREASING, false);

        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -1, 2, 15},
                                 MathUtils.OrderDirection.INCREASING, true);
            fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {-15, -5.5, -1, -2, 2},
                                 MathUtils.OrderDirection.INCREASING, false);
            fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, 3, -5.5, -11, -27.5},
                                 MathUtils.OrderDirection.DECREASING, true);
            fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
        try {
            MathUtils.checkOrder(new double[] {3, -1, 0, -5.5, -11, -27.5},
                                 MathUtils.OrderDirection.DECREASING, false);
            fail("an exception should have been thrown");
        } catch (NonMonotonousSequenceException e) {
            // Expected
        }
    }
}
