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

package org.apache.commons.math;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexFormat;
import org.apache.commons.math.distribution.ContinuousDistribution;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.inference.ChiSquareTest;
import org.apache.commons.math.stat.inference.ChiSquareTestImpl;
import org.apache.commons.math.util.FastMath;

/**
 * @version $Revision: 1042376 $ $Date: 2010-12-05 16:54:55 +0100 (dim. 05 déc. 2010) $
 */
public class TestUtils {
    /**
     * Collection of static methods used in math unit tests.
     */
    private TestUtils() {
        super();
    }

    /**
     * Verifies that expected and actual are within delta, or are both NaN or
     * infinities of the same sign.
     */
    public static void assertEquals(double expected, double actual, double delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Verifies that expected and actual are within delta, or are both NaN or
     * infinities of the same sign.
     */
    public static void assertEquals(String msg, double expected, double actual, double delta) {
        // check for NaN
        if(Double.isNaN(expected)){
            Assert.assertTrue("" + actual + " is not NaN.",
                Double.isNaN(actual));
        } else {
            Assert.assertEquals(msg, expected, actual, delta);
        }
    }

    /**
     * Verifies that the two arguments are exactly the same, either
     * both NaN or infinities of same sign, or identical floating point values.
     */
    public static void assertSame(double expected, double actual) {
     assertEquals(expected, actual, 0);
    }

    /**
     * Verifies that real and imaginary parts of the two complex arguments
     * are exactly the same.  Also ensures that NaN / infinite components match.
     */
    public static void assertSame(Complex expected, Complex actual) {
        assertSame(expected.getReal(), actual.getReal());
        assertSame(expected.getImaginary(), actual.getImaginary());
    }

    /**
     * Verifies that real and imaginary parts of the two complex arguments
     * differ by at most delta.  Also ensures that NaN / infinite components match.
     */
    public static void assertEquals(Complex expected, Complex actual, double delta) {
        assertEquals(expected.getReal(), actual.getReal(), delta);
        assertEquals(expected.getImaginary(), actual.getImaginary(), delta);
    }

    /**
     * Verifies that two double arrays have equal entries, up to tolerance
     */
    public static void assertEquals(double expected[], double observed[], double tolerance) {
        assertEquals("Array comparison failure", expected, observed, tolerance);
    }

    /**
     * Serializes an object to a bytes array and then recovers the object from the bytes array.
     * Returns the deserialized object.
     *
     * @param o  object to serialize and recover
     * @return  the recovered, deserialized object
     */
    public static Object serializeAndRecover(Object o) {
        try {
            // serialize the Object
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bos);
            so.writeObject(o);

            // deserialize the Object
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream si = new ObjectInputStream(bis);
            return si.readObject();
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
    }

    /**
     * Verifies that serialization preserves equals and hashCode.
     * Serializes the object, then recovers it and checks equals and hash code.
     *
     * @param object  the object to serialize and recover
     */
    public static void checkSerializedEquality(Object object) {
        Object object2 = serializeAndRecover(object);
        Assert.assertEquals("Equals check", object, object2);
        Assert.assertEquals("HashCode check", object.hashCode(), object2.hashCode());
    }

    /**
     * Verifies that the relative error in actual vs. expected is less than or
     * equal to relativeError.  If expected is infinite or NaN, actual must be
     * the same (NaN or infinity of the same sign).
     *
     * @param expected expected value
     * @param actual  observed value
     * @param relativeError  maximum allowable relative error
     */
    public static void assertRelativelyEquals(double expected, double actual,
            double relativeError) {
        assertRelativelyEquals(null, expected, actual, relativeError);
    }

    /**
     * Verifies that the relative error in actual vs. expected is less than or
     * equal to relativeError.  If expected is infinite or NaN, actual must be
     * the same (NaN or infinity of the same sign).
     *
     * @param msg  message to return with failure
     * @param expected expected value
     * @param actual  observed value
     * @param relativeError  maximum allowable relative error
     */
    public static void assertRelativelyEquals(String msg, double expected,
            double actual, double relativeError) {
        if (Double.isNaN(expected)) {
            Assert.assertTrue(msg, Double.isNaN(actual));
        } else if (Double.isNaN(actual)) {
            Assert.assertTrue(msg, Double.isNaN(expected));
        } else if (Double.isInfinite(actual) || Double.isInfinite(expected)) {
            Assert.assertEquals(expected, actual, relativeError);
        } else if (expected == 0.0) {
            Assert.assertEquals(msg, actual, expected, relativeError);
        } else {
            double absError = FastMath.abs(expected) * relativeError;
            Assert.assertEquals(msg, expected, actual, absError);
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of z.
     *
     * @param msg  message to return with failure
     * @param values complex array to search
     * @param z  value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(String msg, Complex[] values,
            Complex z, double epsilon) {
        int i = 0;
        boolean found = false;
        while (!found && i < values.length) {
            try {
                assertEquals(values[i], z, epsilon);
                found = true;
            } catch (AssertionFailedError er) {
                // no match
            }
            i++;
        }
        if (!found) {
            Assert.fail(msg +
                " Unable to find " + ComplexFormat.formatComplex(z));
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of z.
     *
     * @param values complex array to search
     * @param z  value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(Complex[] values,
            Complex z, double epsilon) {
        assertContains(null, values, z, epsilon);
    }

    /**
     * Fails iff values does not contain a number within epsilon of x.
     *
     * @param msg  message to return with failure
     * @param values double array to search
     * @param x value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(String msg, double[] values,
            double x, double epsilon) {
        int i = 0;
        boolean found = false;
        while (!found && i < values.length) {
            try {
                assertEquals(values[i], x, epsilon);
                found = true;
            } catch (AssertionFailedError er) {
                // no match
            }
            i++;
        }
        if (!found) {
            Assert.fail(msg + " Unable to find" + x);
        }
    }

    /**
     * Fails iff values does not contain a number within epsilon of x.
     *
     * @param values double array to search
     * @param x value sought
     * @param epsilon  tolerance
     */
    public static void assertContains(double[] values, double x,
            double epsilon) {
       assertContains(null, values, x, epsilon);
    }

    /** verifies that two matrices are close (1-norm) */
    public static void assertEquals(String msg, RealMatrix expected, RealMatrix observed,
        double tolerance) {

        Assert.assertNotNull(msg + "\nObserved should not be null",observed);

        if (expected.getColumnDimension() != observed.getColumnDimension() ||
                expected.getRowDimension() != observed.getRowDimension()) {
            StringBuilder messageBuffer = new StringBuilder(msg);
            messageBuffer.append("\nObserved has incorrect dimensions.");
            messageBuffer.append("\nobserved is " + observed.getRowDimension() +
                    " x " + observed.getColumnDimension());
            messageBuffer.append("\nexpected " + expected.getRowDimension() +
                    " x " + expected.getColumnDimension());
            Assert.fail(messageBuffer.toString());
        }

        RealMatrix delta = expected.subtract(observed);
        if (delta.getNorm() >= tolerance) {
            StringBuilder messageBuffer = new StringBuilder(msg);
            messageBuffer.append("\nExpected: " + expected);
            messageBuffer.append("\nObserved: " + observed);
            messageBuffer.append("\nexpected - observed: " + delta);
            Assert.fail(messageBuffer.toString());
        }
    }

    /** verifies that two matrices are equal */
    public static void assertEquals(FieldMatrix<? extends FieldElement<?>> expected,
                                    FieldMatrix<? extends FieldElement<?>> observed) {

        Assert.assertNotNull("Observed should not be null",observed);

        if (expected.getColumnDimension() != observed.getColumnDimension() ||
                expected.getRowDimension() != observed.getRowDimension()) {
            StringBuilder messageBuffer = new StringBuilder();
            messageBuffer.append("Observed has incorrect dimensions.");
            messageBuffer.append("\nobserved is " + observed.getRowDimension() +
                    " x " + observed.getColumnDimension());
            messageBuffer.append("\nexpected " + expected.getRowDimension() +
                    " x " + expected.getColumnDimension());
            Assert.fail(messageBuffer.toString());
        }

        for (int i = 0; i < expected.getRowDimension(); ++i) {
            for (int j = 0; j < expected.getColumnDimension(); ++j) {
                FieldElement<?> eij = expected.getEntry(i, j);
                FieldElement<?> oij = observed.getEntry(i, j);
                Assert.assertEquals(eij, oij);
            }
        }
    }

    /** verifies that two arrays are close (sup norm) */
    public static void assertEquals(String msg, double[] expected, double[] observed,
        double tolerance) {
        StringBuilder out = new StringBuilder(msg);
        if (expected.length != observed.length) {
            out.append("\n Arrays not same length. \n");
            out.append("expected has length ");
            out.append(expected.length);
            out.append(" observed length = ");
            out.append(observed.length);
            Assert.fail(out.toString());
        }
        boolean failure = false;
        for (int i=0; i < expected.length; i++) {
            try {
                assertEquals(expected[i], observed[i], tolerance);
            } catch (AssertionFailedError ex) {
                failure = true;
                out.append("\n Elements at index ");
                out.append(i);
                out.append(" differ. ");
                out.append(" expected = ");
                out.append(expected[i]);
                out.append(" observed = ");
                out.append(observed[i]);
            }
        }
        if (failure) {
            Assert.fail(out.toString());
        }
    }

    /** verifies that two arrays are equal */
    public static <T extends FieldElement<T>> void assertEquals(T[] m, T[] n) {
        if (m.length != n.length) {
            Assert.fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(m[i],n[i]);
        }
    }

    /**
     * Computes the sum of squared deviations of <values> from <target>
     * @param values array of deviates
     * @param target value to compute deviations from
     *
     * @return sum of squared deviations
     */
    public static double sumSquareDev(double[] values, double target) {
        double sumsq = 0d;
        for (int i = 0; i < values.length; i++) {
            final double dev = values[i] - target;
            sumsq += (dev * dev);
        }
        return sumsq;
    }
    
    /**
     * Asserts the null hypothesis for a ChiSquare test.  Fails and dumps arguments and test
     * statistics if the null hypothesis can be rejected with confidence 100 * (1 - alpha)%
     * 
     * @param valueLabels
     * @param expected expected counts
     * @param observed observed counts
     * @param alpha significance level of the test
     */
    public static void assertChiSquareAccept(String[] valueLabels, double[] expected, long[] observed, double alpha) throws Exception {
        ChiSquareTest chiSquareTest = new ChiSquareTestImpl();
        try {
            // Fail if we can reject null hypothesis that distributions are the same
            Assert.assertFalse(chiSquareTest.chiSquareTest(expected, observed, alpha));
        } catch (AssertionFailedError ex) {
            StringBuilder msgBuffer = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#.##");
            msgBuffer.append("Chisquare test failed");
            msgBuffer.append(" p-value = ");
            msgBuffer.append(chiSquareTest.chiSquareTest(expected, observed));
            msgBuffer.append(" chisquare statistic = ");
            msgBuffer.append(chiSquareTest.chiSquare(expected, observed));
            msgBuffer.append(". \n");
            msgBuffer.append("value\texpected\tobserved\n");
            for (int i = 0; i < expected.length; i++) {
                msgBuffer.append(valueLabels[i]);
                msgBuffer.append("\t");
                msgBuffer.append(df.format(expected[i]));
                msgBuffer.append("\t\t");
                msgBuffer.append(observed[i]);
                msgBuffer.append("\n");
            }
            msgBuffer.append("This test can fail randomly due to sampling error with probability ");
            msgBuffer.append(alpha);
            msgBuffer.append(".");
            Assert.fail(msgBuffer.toString());
        }   
    }
    
    /**
     * Asserts the null hypothesis for a ChiSquare test.  Fails and dumps arguments and test
     * statistics if the null hypothesis can be rejected with confidence 100 * (1 - alpha)%
     * 
     * @param values
     * @param expected expected counts
     * @param observed observed counts
     * @param alpha significance level of the test
     */
    public static void assertChiSquareAccept(int[] values, double[] expected, long[] observed, double alpha) throws Exception {
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = Integer.toString(values[i]);
        }
        assertChiSquareAccept(labels, expected, observed, alpha);
    }
    
    /**
     * Asserts the null hypothesis for a ChiSquare test.  Fails and dumps arguments and test
     * statistics if the null hypothesis can be rejected with confidence 100 * (1 - alpha)%
     * 
     * @param values
     * @param expected expected counts
     * @param observed observed counts
     * @param alpha significance level of the test
     */
    public static void assertChiSquareAccept(double[] values, double[] expected, long[] observed, double alpha) throws Exception {
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = Double.toString(values[i]);
        }
        assertChiSquareAccept(labels, expected, observed, alpha);
    }
    
    /**
     * Asserts the null hypothesis for a ChiSquare test.  Fails and dumps arguments and test
     * statistics if the null hypothesis can be rejected with confidence 100 * (1 - alpha)%
     * 
     * @param expected expected counts
     * @param observed observed counts
     * @param alpha significance level of the test
     */
    public static void assertChiSquareAccept(double[] expected, long[] observed, double alpha) throws Exception {
        String[] labels = new String[expected.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = Integer.toString(i + 1);
        }
        assertChiSquareAccept(labels, expected, observed, alpha);
    }
    
    /**
     * Computes the 25th, 50th and 75th percentiles of the given distribution and returns
     * these values in an array.
     */
    public static double[] getDistributionQuartiles(ContinuousDistribution distribution) throws Exception {
        double[] quantiles = new double[3];
        quantiles[0] = distribution.inverseCumulativeProbability(0.25d);
        quantiles[1] = distribution.inverseCumulativeProbability(0.5d);
        quantiles[2] = distribution.inverseCumulativeProbability(0.75d);
        return quantiles;
    }
    
    /**
     * Updates observed counts of values in quartiles.
     * counts[0] <-> 1st quartile ... counts[3] <-> top quartile
     */
    public static void updateCounts(double value, long[] counts, double[] quartiles) {
        if (value < quartiles[0]) {
            counts[0]++;
        } else if (value > quartiles[2]) {
            counts[3]++;
        } else if (value > quartiles[1]) {
            counts[2]++;
        } else {
            counts[1]++;
        }  
    }
    
    /**
     * Eliminates points with zero mass from densityPoints and densityValues parallel
     * arrays.  Returns the number of positive mass points and collapses the arrays so
     * that the first <returned value> elements of the input arrays represent the positive
     * mass points.
     */
    public static int eliminateZeroMassPoints(int[] densityPoints, double[] densityValues) {
        int positiveMassCount = 0;
        for (int i = 0; i < densityValues.length; i++) {
            if (densityValues[i] > 0) {
                positiveMassCount++;
            }
        }
        if (positiveMassCount < densityValues.length) {
            int[] newPoints = new int[positiveMassCount];
            double[] newValues = new double[positiveMassCount];
            int j = 0;
            for (int i = 0; i < densityValues.length; i++) {
                if (densityValues[i] > 0) {
                    newPoints[j] = densityPoints[i];
                    newValues[j] = densityValues[i];
                    j++;
                }
            }
            System.arraycopy(newPoints,0,densityPoints,0,positiveMassCount);
            System.arraycopy(newValues,0,densityValues,0,positiveMassCount);
        }
        return positiveMassCount;
    } 
}
