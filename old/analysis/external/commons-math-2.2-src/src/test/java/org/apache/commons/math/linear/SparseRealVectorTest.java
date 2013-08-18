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
package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.util.FastMath;

/**
 * Test cases for the {@link OpenMapRealVector} class.
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public class SparseRealVectorTest extends TestCase {

    //
    protected double[][] ma1 = {{1d, 2d, 3d}, {4d, 5d, 6d}, {7d, 8d, 9d}};
    protected double[] vec1 = {1d, 2d, 3d};
    protected double[] vec2 = {4d, 5d, 6d};
    protected double[] vec3 = {7d, 8d, 9d};
    protected double[] vec4 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[] vec5 = { -4d, 0d, 3d, 1d, -6d, 3d};
    protected double[] vec_null = {0d, 0d, 0d};
    protected Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[][] mat1 = {{1d, 2d, 3d}, {4d, 5d, 6d},{ 7d, 8d, 9d}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    // Testclass to test the RealVector interface
    // only with enough content to support the test
    public static class SparseRealVectorTestImpl extends AbstractRealVector implements Serializable {

        private static final long serialVersionUID = -6251371752518113791L;
        /** Entries of the vector. */
        protected double data[];

        public SparseRealVectorTestImpl(double[] d) {
            data = d.clone();
        }

        private MatrixVisitorException unsupported() {
            return new MatrixVisitorException("Not supported, unneeded for test purposes", new Object[0]);
        }

        @Override
        public RealVector map(UnivariateRealFunction function) throws MatrixVisitorException {
            throw unsupported();
        }

        @Override
        public RealVector mapToSelf(UnivariateRealFunction function) throws MatrixVisitorException {
            throw unsupported();
        }

        @Override
        public Iterator<Entry> iterator() {
            throw unsupported();
        }

        @Override
        public AbstractRealVector copy() {
            return new SparseRealVectorTestImpl(data);
        }

        @Override
        public RealVector add(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector add(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector subtract(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector subtract(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector mapAdd(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapAddToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtract(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtractToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapMultiply(double d) {
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] * d;
            }
            return new OpenMapRealVector(out);
        }

        @Override
        public RealVector mapMultiplyToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivide(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivideToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapPow(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapPowToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapExp() {
            throw unsupported();
        }

        @Override
        public RealVector mapExpToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapExpm1() {
            throw unsupported();
        }

        @Override
        public RealVector mapExpm1ToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapLog() {
            throw unsupported();
        }

        @Override
        public RealVector mapLogToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapLog10() {
            throw unsupported();
        }

        @Override
        public RealVector mapLog10ToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapLog1p() {
            throw unsupported();
        }

        @Override
        public RealVector mapLog1pToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapCosh() {
            throw unsupported();
        }

        @Override
        public RealVector mapCoshToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapSinh() {
            throw unsupported();
        }

        @Override
        public RealVector mapSinhToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapTanh() {
            throw unsupported();
        }

        @Override
        public RealVector mapTanhToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapCos() {
            throw unsupported();
        }

        @Override
        public RealVector mapCosToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapSin() {
            throw unsupported();
        }

        @Override
        public RealVector mapSinToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapTan() {
            throw unsupported();
        }

        @Override
        public RealVector mapTanToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapAcos() {
            throw unsupported();
        }

        @Override
        public RealVector mapAcosToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapAsin() {
            throw unsupported();
        }

        @Override
        public RealVector mapAsinToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapAtan() {
            throw unsupported();
        }

        @Override
        public RealVector mapAtanToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapInv() {
            throw unsupported();
        }

        @Override
        public RealVector mapInvToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapAbs() {
            throw unsupported();
        }

        @Override
        public RealVector mapAbsToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapSqrt() {
            throw unsupported();
        }

        @Override
        public RealVector mapSqrtToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapCbrt() {
            throw unsupported();
        }

        @Override
        public RealVector mapCbrtToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapCeil() {
            throw unsupported();
        }

        @Override
        public RealVector mapCeilToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapFloor() {
            throw unsupported();
        }

        @Override
        public RealVector mapFloorToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapRint() {
            throw unsupported();
        }

        @Override
        public RealVector mapRintToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapSignum() {
            throw unsupported();
        }

        @Override
        public RealVector mapSignumToSelf() {
            throw unsupported();
        }

        @Override
        public RealVector mapUlp() {
            throw unsupported();
        }

        @Override
        public RealVector mapUlpToSelf() {
            throw unsupported();
        }

        public RealVector ebeMultiply(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector ebeMultiply(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public RealVector ebeDivide(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector ebeDivide(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double[] getData() {
            return data.clone();
        }

        @Override
        public double dotProduct(RealVector v) throws IllegalArgumentException {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v.getEntry(i);
            }
            return dot;
        }

        @Override
        public double dotProduct(double[] v) throws IllegalArgumentException {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v[i];
            }
            return dot;
        }

        @Override
        public double getNorm() {
            throw unsupported();
        }

        @Override
        public double getL1Norm() {
            throw unsupported();
        }

        @Override
        public double getLInfNorm() {
            throw unsupported();
        }

        @Override
        public double getDistance(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double getDistance(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double getL1Distance(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double getL1Distance(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double getLInfDistance(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double getLInfDistance(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector unitVector() {
            throw unsupported();
        }

        @Override
        public void unitize() {
            throw unsupported();
        }

        public RealVector projection(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealVector projection(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealMatrix outerProduct(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public RealMatrix outerProduct(double[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public double getEntry(int index) throws MatrixIndexException {
            return data[index];
        }

        public int getDimension() {
            return data.length;
        }

        public RealVector append(RealVector v) {
            throw unsupported();
        }

        public RealVector append(double d) {
            throw unsupported();
        }

        public RealVector append(double[] a) {
            throw unsupported();
        }

        public RealVector getSubVector(int index, int n) throws MatrixIndexException {
            throw unsupported();
        }

        public void setEntry(int index, double value) throws MatrixIndexException {
            data[index] = value;
        }

        @Override
        public void setSubVector(int index, RealVector v) throws MatrixIndexException {
            throw unsupported();
        }

        @Override
        public void setSubVector(int index, double[] v) throws MatrixIndexException {
            throw unsupported();
        }

        @Override
        public void set(double value) {
            throw unsupported();
        }

        @Override
        public double[] toArray() {
            throw unsupported();
        }

        public boolean isNaN() {
            throw unsupported();
        }

        public boolean isInfinite() {
            throw unsupported();
        }

    }

    public void testConstructors() {

        OpenMapRealVector v0 = new OpenMapRealVector();
        assertEquals("testData len", 0, v0.getDimension());

        OpenMapRealVector v1 = new OpenMapRealVector(7);
        assertEquals("testData len", 7, v1.getDimension());
        assertEquals("testData is 0.0 ", 0.0, v1.getEntry(6));

        OpenMapRealVector v3 = new OpenMapRealVector(vec1);
        assertEquals("testData len", 3, v3.getDimension());
        assertEquals("testData is 2.0 ", 2.0, v3.getEntry(1));

        //SparseRealVector v4 = new SparseRealVector(vec4, 3, 2);
        //assertEquals("testData len", 2, v4.getDimension());
        //assertEquals("testData is 4.0 ", 4.0, v4.getEntry(0));
        //try {
        //    new SparseRealVector(vec4, 8, 3);
        //    fail("IllegalArgumentException expected");
        //} catch (IllegalArgumentException ex) {
            // expected behavior
        //}

        RealVector v5_i = new OpenMapRealVector(dvec1);
        assertEquals("testData len", 9, v5_i.getDimension());
        assertEquals("testData is 9.0 ", 9.0, v5_i.getEntry(8));

        OpenMapRealVector v5 = new OpenMapRealVector(dvec1);
        assertEquals("testData len", 9, v5.getDimension());
        assertEquals("testData is 9.0 ", 9.0, v5.getEntry(8));

        OpenMapRealVector v7 = new OpenMapRealVector(v1);
        assertEquals("testData len", 7, v7.getDimension());
        assertEquals("testData is 0.0 ", 0.0, v7.getEntry(6));

        SparseRealVectorTestImpl v7_i = new SparseRealVectorTestImpl(vec1);

        OpenMapRealVector v7_2 = new OpenMapRealVector(v7_i);
        assertEquals("testData len", 3, v7_2.getDimension());
        assertEquals("testData is 0.0 ", 2.0d, v7_2.getEntry(1));

        OpenMapRealVector v8 = new OpenMapRealVector(v1);
        assertEquals("testData len", 7, v8.getDimension());
        assertEquals("testData is 0.0 ", 0.0, v8.getEntry(6));

    }

    public void testDataInOut() {

        OpenMapRealVector v1 = new OpenMapRealVector(vec1);
        OpenMapRealVector v2 = new OpenMapRealVector(vec2);
        OpenMapRealVector v4 = new OpenMapRealVector(vec4);
        SparseRealVectorTestImpl v2_t = new SparseRealVectorTestImpl(vec2);

        RealVector v_append_1 = v1.append(v2);
        assertEquals("testData len", 6, v_append_1.getDimension());
        assertEquals("testData is 4.0 ", 4.0, v_append_1.getEntry(3));

        RealVector v_append_2 = v1.append(2.0);
        assertEquals("testData len", 4, v_append_2.getDimension());
        assertEquals("testData is 2.0 ", 2.0, v_append_2.getEntry(3));

        RealVector v_append_3 = v1.append(vec2);
        assertEquals("testData len", 6, v_append_3.getDimension());
        assertEquals("testData is  ", 4.0, v_append_3.getEntry(3));

        RealVector v_append_4 = v1.append(v2_t);
        assertEquals("testData len", 6, v_append_4.getDimension());
        assertEquals("testData is 4.0 ", 4.0, v_append_4.getEntry(3));

        RealVector vout5 = v4.getSubVector(3, 3);
        assertEquals("testData len", 3, vout5.getDimension());
        assertEquals("testData is 4.0 ", 5.0, vout5.getEntry(1));
        try {
            v4.getSubVector(3, 7);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        OpenMapRealVector v_set1 = v1.copy();
        v_set1.setEntry(1, 11.0);
        assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1));
        try {
            v_set1.setEntry(3, 11.0);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        OpenMapRealVector v_set2 = v4.copy();
        v_set2.setSubVector(3, v1);
        assertEquals("testData is 1.0 ", 1.0, v_set2.getEntry(3));
        assertEquals("testData is 7.0 ", 7.0, v_set2.getEntry(6));
        try {
            v_set2.setSubVector(7, v1);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        OpenMapRealVector v_set3 = v1.copy();
        v_set3.set(13.0);
        assertEquals("testData is 13.0 ", 13.0, v_set3.getEntry(2));

        try {
            v_set3.getEntry(23);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        OpenMapRealVector v_set4 = v4.copy();
        v_set4.setSubVector(3, v2_t);
        assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3));
        assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6));
        try {
            v_set4.setSubVector(7, v2_t);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }


    }

    public void testMapFunctions() {
        OpenMapRealVector v1 = new OpenMapRealVector(vec1);

        //octave =  v1 .+ 2.0
        RealVector v_mapAdd = v1.mapAdd(2.0d);
        double[] result_mapAdd = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAdd,v_mapAdd.getData(),normTolerance);

        //octave =  v1 .+ 2.0
        RealVector v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(2.0d);
        double[] result_mapAddToSelf = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.getData(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtract = v1.mapSubtract(2.0d);
        double[] result_mapSubtract = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtract,v_mapSubtract.getData(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(2.0d);
        double[] result_mapSubtractToSelf = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.getData(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiply = v1.mapMultiply(2.0d);
        double[] result_mapMultiply = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiply,v_mapMultiply.getData(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(2.0d);
        double[] result_mapMultiplyToSelf = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.getData(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivide = v1.mapDivide(2.0d);
        double[] result_mapDivide = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivide,v_mapDivide.getData(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(2.0d);
        double[] result_mapDivideToSelf = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.getData(),normTolerance);

        //octave =  v1 .^ 2.0
        RealVector v_mapPow = v1.mapPow(2.0d);
        double[] result_mapPow = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPow,v_mapPow.getData(),normTolerance);

        //octave =  v1 .^ 2.0
        RealVector v_mapPowToSelf = v1.copy();
        v_mapPowToSelf.mapPowToSelf(2.0d);
        double[] result_mapPowToSelf = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPowToSelf,v_mapPowToSelf.getData(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExp = v1.mapExp();
        double[] result_mapExp = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExp,v_mapExp.getData(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExpToSelf = v1.copy();
        v_mapExpToSelf.mapExpToSelf();
        double[] result_mapExpToSelf = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExpToSelf,v_mapExpToSelf.getData(),normTolerance);


        //octave =  ???
        RealVector v_mapExpm1 = v1.mapExpm1();
        double[] result_mapExpm1 = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1,v_mapExpm1.getData(),normTolerance);

        //octave =  ???
        RealVector v_mapExpm1ToSelf = v1.copy();
        v_mapExpm1ToSelf.mapExpm1ToSelf();
        double[] result_mapExpm1ToSelf = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1ToSelf,v_mapExpm1ToSelf.getData(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog = v1.mapLog();
        double[] result_mapLog = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLog,v_mapLog.getData(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLogToSelf = v1.copy();
        v_mapLogToSelf.mapLogToSelf();
        double[] result_mapLogToSelf = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLogToSelf,v_mapLogToSelf.getData(),normTolerance);

        //octave =  log10(v1)
        RealVector v_mapLog10 = v1.mapLog10();
        double[] result_mapLog10 = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10,v_mapLog10.getData(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog10ToSelf = v1.copy();
        v_mapLog10ToSelf.mapLog10ToSelf();
        double[] result_mapLog10ToSelf = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10ToSelf,v_mapLog10ToSelf.getData(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1p = v1.mapLog1p();
        double[] result_mapLog1p = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1p,v_mapLog1p.getData(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1pToSelf = v1.copy();
        v_mapLog1pToSelf.mapLog1pToSelf();
        double[] result_mapLog1pToSelf = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1pToSelf,v_mapLog1pToSelf.getData(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCosh = v1.mapCosh();
        double[] result_mapCosh = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCosh,v_mapCosh.getData(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCoshToSelf = v1.copy();
        v_mapCoshToSelf.mapCoshToSelf();
        double[] result_mapCoshToSelf = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCoshToSelf,v_mapCoshToSelf.getData(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinh = v1.mapSinh();
        double[] result_mapSinh = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinh,v_mapSinh.getData(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinhToSelf = v1.copy();
        v_mapSinhToSelf.mapSinhToSelf();
        double[] result_mapSinhToSelf = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinhToSelf,v_mapSinhToSelf.getData(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanh = v1.mapTanh();
        double[] result_mapTanh = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanh,v_mapTanh.getData(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanhToSelf = v1.copy();
        v_mapTanhToSelf.mapTanhToSelf();
        double[] result_mapTanhToSelf = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanhToSelf,v_mapTanhToSelf.getData(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCos = v1.mapCos();
        double[] result_mapCos = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCos,v_mapCos.getData(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCosToSelf = v1.copy();
        v_mapCosToSelf.mapCosToSelf();
        double[] result_mapCosToSelf = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCosToSelf,v_mapCosToSelf.getData(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSin = v1.mapSin();
        double[] result_mapSin = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSin,v_mapSin.getData(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSinToSelf = v1.copy();
        v_mapSinToSelf.mapSinToSelf();
        double[] result_mapSinToSelf = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSinToSelf,v_mapSinToSelf.getData(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTan = v1.mapTan();
        double[] result_mapTan = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTan,v_mapTan.getData(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTanToSelf = v1.copy();
        v_mapTanToSelf.mapTanToSelf();
        double[] result_mapTanToSelf = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTanToSelf,v_mapTanToSelf.getData(),normTolerance);

        double[] vat_a = {0d, 0.5d, 1.0d};
        OpenMapRealVector vat = new OpenMapRealVector(vat_a);

        //octave =  acos(vat)
        RealVector v_mapAcos = vat.mapAcos();
        double[] result_mapAcos = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcos,v_mapAcos.getData(),normTolerance);

        //octave =  acos(vat)
        RealVector v_mapAcosToSelf = vat.copy();
        v_mapAcosToSelf.mapAcosToSelf();
        double[] result_mapAcosToSelf = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcosToSelf,v_mapAcosToSelf.getData(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsin = vat.mapAsin();
        double[] result_mapAsin = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsin,v_mapAsin.getData(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsinToSelf = vat.copy();
        v_mapAsinToSelf.mapAsinToSelf();
        double[] result_mapAsinToSelf = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsinToSelf,v_mapAsinToSelf.getData(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtan = vat.mapAtan();
        double[] result_mapAtan = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtan,v_mapAtan.getData(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtanToSelf = vat.copy();
        v_mapAtanToSelf.mapAtanToSelf();
        double[] result_mapAtanToSelf = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtanToSelf,v_mapAtanToSelf.getData(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInv = v1.mapInv();
        double[] result_mapInv = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInv,v_mapInv.getData(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapInvToSelf();
        double[] result_mapInvToSelf = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.getData(),normTolerance);

        double[] abs_a = {-1.0d, 0.0d, 1.0d};
        OpenMapRealVector abs_v = new OpenMapRealVector(abs_a);

        //octave =  abs(abs_v)
        RealVector v_mapAbs = abs_v.mapAbs();
        double[] result_mapAbs = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbs,v_mapAbs.getData(),normTolerance);

        //octave = abs(abs_v)
        RealVector v_mapAbsToSelf = abs_v.copy();
        v_mapAbsToSelf.mapAbsToSelf();
        double[] result_mapAbsToSelf = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbsToSelf,v_mapAbsToSelf.getData(),normTolerance);

        //octave =   sqrt(v1)
        RealVector v_mapSqrt = v1.mapSqrt();
        double[] result_mapSqrt = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrt,v_mapSqrt.getData(),normTolerance);

        //octave =  sqrt(v1)
        RealVector v_mapSqrtToSelf = v1.copy();
        v_mapSqrtToSelf.mapSqrtToSelf();
        double[] result_mapSqrtToSelf = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrtToSelf,v_mapSqrtToSelf.getData(),normTolerance);

        double[] cbrt_a = {-2.0d, 0.0d, 2.0d};
        OpenMapRealVector cbrt_v = new OpenMapRealVector(cbrt_a);

        //octave =  ???
        RealVector v_mapCbrt = cbrt_v.mapCbrt();
        double[] result_mapCbrt = {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrt,v_mapCbrt.getData(),normTolerance);

        //octave = ???
        RealVector v_mapCbrtToSelf = cbrt_v.copy();
        v_mapCbrtToSelf.mapCbrtToSelf();
        double[] result_mapCbrtToSelf =  {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrtToSelf,v_mapCbrtToSelf.getData(),normTolerance);

        double[] ceil_a = {-1.1d, 0.9d, 1.1d};
        OpenMapRealVector ceil_v = new OpenMapRealVector(ceil_a);

        //octave =  ceil(ceil_v)
        RealVector v_mapCeil = ceil_v.mapCeil();
        double[] result_mapCeil = {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeil,v_mapCeil.getData(),normTolerance);

        //octave = ceil(ceil_v)
        RealVector v_mapCeilToSelf = ceil_v.copy();
        v_mapCeilToSelf.mapCeilToSelf();
        double[] result_mapCeilToSelf =  {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeilToSelf,v_mapCeilToSelf.getData(),normTolerance);

        //octave =  floor(ceil_v)
        RealVector v_mapFloor = ceil_v.mapFloor();
        double[] result_mapFloor = {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloor,v_mapFloor.getData(),normTolerance);

        //octave = floor(ceil_v)
        RealVector v_mapFloorToSelf = ceil_v.copy();
        v_mapFloorToSelf.mapFloorToSelf();
        double[] result_mapFloorToSelf =  {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloorToSelf,v_mapFloorToSelf.getData(),normTolerance);

        //octave =  ???
        RealVector v_mapRint = ceil_v.mapRint();
        double[] result_mapRint = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRint,v_mapRint.getData(),normTolerance);

        //octave = ???
        RealVector v_mapRintToSelf = ceil_v.copy();
        v_mapRintToSelf.mapRintToSelf();
        double[] result_mapRintToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRintToSelf,v_mapRintToSelf.getData(),normTolerance);

        //octave =  ???
        RealVector v_mapSignum = ceil_v.mapSignum();
        double[] result_mapSignum = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignum,v_mapSignum.getData(),normTolerance);

        //octave = ???
        RealVector v_mapSignumToSelf = ceil_v.copy();
        v_mapSignumToSelf.mapSignumToSelf();
        double[] result_mapSignumToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignumToSelf,v_mapSignumToSelf.getData(),normTolerance);


        // Is with the used resolutions of limited value as test
        //octave =  ???
        RealVector v_mapUlp = ceil_v.mapUlp();
        double[] result_mapUlp = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlp,v_mapUlp.getData(),normTolerance);

        //octave = ???
        RealVector v_mapUlpToSelf = ceil_v.copy();
        v_mapUlpToSelf.mapUlpToSelf();
        double[] result_mapUlpToSelf = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlpToSelf,v_mapUlpToSelf.getData(),normTolerance);

    }

    public void testBasicFunctions() {
        OpenMapRealVector v1 = new OpenMapRealVector(vec1);
        OpenMapRealVector v2 = new OpenMapRealVector(vec2);
        OpenMapRealVector v5 = new OpenMapRealVector(vec5);
        OpenMapRealVector v_null = new OpenMapRealVector(vec_null);

        SparseRealVectorTestImpl v2_t = new SparseRealVectorTestImpl(vec2);

        // emacs calc: [-4, 0, 3, 1, -6, 3] A --> 8.4261497731763586307
        double d_getNorm = v5.getNorm();
        assertEquals("compare values  ", 8.4261497731763586307, d_getNorm);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vN --> 17
        double d_getL1Norm = v5.getL1Norm();
        assertEquals("compare values  ", 17.0, d_getL1Norm);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vn --> 6
        double d_getLInfNorm = v5.getLInfNorm();
        assertEquals("compare values  ", 6.0, d_getLInfNorm);

        //octave =  sqrt(sumsq(v1-v2))
        double dist = v1.getDistance(v2);
        assertEquals("compare values  ",v1.subtract(v2).getNorm(), dist );

        //octave =  sqrt(sumsq(v1-v2))
        double dist_2 = v1.getDistance(v2_t);
        assertEquals("compare values  ", v1.subtract(v2).getNorm(),dist_2 );

        //octave =  ???
        double d_getL1Distance = v1. getL1Distance(v2);
        assertEquals("compare values  ",9d, d_getL1Distance );

        double d_getL1Distance_2 = v1. getL1Distance(v2_t);
        assertEquals("compare values  ",9d, d_getL1Distance_2 );

        //octave =  ???
        double d_getLInfDistance = v1. getLInfDistance(v2);
        assertEquals("compare values  ",3d, d_getLInfDistance );

        double d_getLInfDistance_2 = v1. getLInfDistance(v2_t);
        assertEquals("compare values  ",3d, d_getLInfDistance_2 );

        //octave =  v1 + v2
        OpenMapRealVector v_add = v1.add(v2);
        double[] result_add = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add.getData(),result_add,normTolerance);

        SparseRealVectorTestImpl vt2 = new SparseRealVectorTestImpl(vec2);
        RealVector v_add_i = v1.add(vt2);
        double[] result_add_i = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add_i.getData(),result_add_i,normTolerance);

        //octave =  v1 - v2
        OpenMapRealVector v_subtract = v1.subtract(v2);
        double[] result_subtract = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract.getData(),result_subtract,normTolerance);

        RealVector v_subtract_i = v1.subtract(vt2);
        double[] result_subtract_i = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract_i.getData(),result_subtract_i,normTolerance);

        // octave v1 .* v2
        RealVector  v_ebeMultiply = v1.ebeMultiply(v2);
        double[] result_ebeMultiply = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply.getData(),result_ebeMultiply,normTolerance);

        RealVector  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        double[] result_ebeMultiply_2 = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply_2.getData(),result_ebeMultiply_2,normTolerance);

        // octave v1 ./ v2
        RealVector  v_ebeDivide = v1.ebeDivide(v2);
        double[] result_ebeDivide = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide.getData(),result_ebeDivide,normTolerance);

        RealVector  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        double[] result_ebeDivide_2 = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide_2.getData(),result_ebeDivide_2,normTolerance);

        // octave  dot(v1,v2)
        double dot =  v1.dotProduct(v2);
        assertEquals("compare val ",32d, dot);

        // octave  dot(v1,v2_t)
        double dot_2 =  v1.dotProduct(v2_t);
        assertEquals("compare val ",32d, dot_2);

        RealMatrix m_outerProduct = v1.outerProduct(v2);
        assertEquals("compare val ",4d, m_outerProduct.getEntry(0,0));

        RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
        assertEquals("compare val ",4d, m_outerProduct_2.getEntry(0,0));

        RealVector v_unitVector = v1.unitVector();
        RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
        assertClose("compare vect" ,v_unitVector.getData(),v_unitVector_2.getData(),normTolerance);

        try {
            v_null.unitVector();
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // expected behavior
        }

        OpenMapRealVector v_unitize = v1.copy();
        v_unitize.unitize();
        assertClose("compare vect" ,v_unitVector_2.getData(),v_unitize.getData(),normTolerance);
        try {
            v_null.unitize();
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // expected behavior
        }

        RealVector v_projection = v1.projection(v2);
        double[] result_projection = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection.getData(), result_projection, normTolerance);

        RealVector v_projection_2 = v1.projection(v2_t);
        double[] result_projection_2 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_2.getData(), result_projection_2, normTolerance);

    }

    public void testMisc() {
        OpenMapRealVector v1 = new OpenMapRealVector(vec1);

        String out1 = v1.toString();
        assertTrue("some output ",  out1.length()!=0);
        try {
            v1.checkVectorDimensions(2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }


    }

    public void testPredicates() {

        OpenMapRealVector v = new OpenMapRealVector(new double[] { 0, 1, 2 });

        assertFalse(v.isNaN());
        v.setEntry(1, Double.NaN);
        assertTrue(v.isNaN());

        assertFalse(v.isInfinite());
        v.setEntry(0, Double.POSITIVE_INFINITY);
        assertFalse(v.isInfinite()); // NaN has higher priority than infinity
        v.setEntry(1, 1);
        assertTrue(v.isInfinite());

        v.setEntry(0, 0);
        assertEquals(v, new OpenMapRealVector(new double[] { 0, 1, 2 }));
        assertNotSame(v, new OpenMapRealVector(new double[] { 0, 1, 2 + FastMath.ulp(2)}));
        assertNotSame(v, new OpenMapRealVector(new double[] { 0, 1, 2, 3 }));

    }

    public void testSerial()  {
        OpenMapRealVector v = new OpenMapRealVector(new double[] { 0, 1, 2 });
        assertEquals(v,TestUtils.serializeAndRecover(v));
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " +  i + " elements differ", m[i],n[i],tolerance);
        }
    }

}
