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
import java.lang.reflect.Array;

import junit.framework.TestCase;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionField;

/**
 * Test cases for the {@link ArrayFieldVector} class.
 *
 * @version $Revision: 1003993 $ $Date: 2010-10-03 18:39:16 +0200 (dim. 03 oct. 2010) $
 */
public class ArrayFieldVectorTest extends TestCase {

    //
    protected Fraction[][] ma1 = {
            {new Fraction(1), new Fraction(2), new Fraction(3)},
            {new Fraction(4), new Fraction(5), new Fraction(6)},
            {new Fraction(7), new Fraction(8), new Fraction(9)}
    };
    protected Fraction[] vec1 = {new Fraction(1), new Fraction(2), new Fraction(3)};
    protected Fraction[] vec2 = {new Fraction(4), new Fraction(5), new Fraction(6)};
    protected Fraction[] vec3 = {new Fraction(7), new Fraction(8), new Fraction(9)};
    protected Fraction[] vec4 = { new Fraction(1), new Fraction(2), new Fraction(3),
                                  new Fraction(4), new Fraction(5), new Fraction(6),
                                  new Fraction(7), new Fraction(8), new Fraction(9)};
    protected Fraction[] vec_null = {new Fraction(0), new Fraction(0), new Fraction(0)};
    protected Fraction[] dvec1 = {new Fraction(1), new Fraction(2), new Fraction(3),
                                  new Fraction(4), new Fraction(5), new Fraction(6),
                                  new Fraction(7), new Fraction(8), new Fraction(9)};
    protected Fraction[][] mat1 = {
            {new Fraction(1), new Fraction(2), new Fraction(3)},
            {new Fraction(4), new Fraction(5), new Fraction(6)},
            {new Fraction(7), new Fraction(8), new Fraction(9)}
    };

    // Testclass to test the FieldVector<Fraction> interface
    // only with enough content to support the test
    public static class FieldVectorTestImpl<T extends FieldElement<T>>
        implements FieldVector<T>, Serializable {

        private static final long serialVersionUID = 3970959016014158539L;

        private final Field<T> field;

        /** Entries of the vector. */
        protected T[] data;

        /** Build an array of elements.
         * @param length size of the array to build
         * @return a new array
         */
        @SuppressWarnings("unchecked") // field is of type T
        private T[] buildArray(final int length) {
            return (T[]) Array.newInstance(field.getZero().getClass(), length);
        }

        public FieldVectorTestImpl(T[] d) {
            field = d[0].getField();
            data = d.clone();
        }

        public Field<T> getField() {
            return field;
        }

        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }

        public FieldVector<T> copy() {
            throw unsupported();
        }

        public FieldVector<T> add(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> add(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> subtract(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> subtract(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> mapAdd(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapAddToSelf(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapSubtract(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapSubtractToSelf(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapMultiply(T d) {
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].multiply(d);
            }
            return new FieldVectorTestImpl<T>(out);
        }

        public FieldVector<T> mapMultiplyToSelf(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapDivide(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapDivideToSelf(T d) {
            throw unsupported();
        }

        public FieldVector<T> mapInv() {
            throw unsupported();
        }

        public FieldVector<T> mapInvToSelf() {
            throw unsupported();
        }

        public FieldVector<T> ebeMultiply(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> ebeMultiply(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> ebeDivide(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> ebeDivide(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public T[] getData() {
            return data.clone();
        }

        public T dotProduct(FieldVector<T> v) throws IllegalArgumentException {
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }

        public T dotProduct(T[] v) throws IllegalArgumentException {
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v[i]));
            }
            return dot;
        }

        public FieldVector<T> projection(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldVector<T> projection(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldMatrix<T> outerProduct(FieldVector<T> v) throws IllegalArgumentException {
            throw unsupported();
        }

        public FieldMatrix<T> outerProduct(T[] v) throws IllegalArgumentException {
            throw unsupported();
        }

        public T getEntry(int index) throws MatrixIndexException {
            return data[index];
        }

        public int getDimension() {
            return data.length;
        }

        public FieldVector<T> append(FieldVector<T> v) {
            throw unsupported();
        }

        public FieldVector<T> append(T d) {
            throw unsupported();
        }

        public FieldVector<T> append(T[] a) {
            throw unsupported();
        }

        public FieldVector<T> getSubVector(int index, int n) throws MatrixIndexException {
            throw unsupported();
        }

        public void setEntry(int index, T value) throws MatrixIndexException {
            throw unsupported();
        }

        public void setSubVector(int index, FieldVector<T> v) throws MatrixIndexException {
            throw unsupported();
        }

        public void setSubVector(int index, T[] v) throws MatrixIndexException {
            throw unsupported();
        }

        public void set(T value) {
            throw unsupported();
        }

        public T[] toArray() {
            throw unsupported();
        }

    }

    public void testConstructors() {

        ArrayFieldVector<Fraction> v0 = new ArrayFieldVector<Fraction>(FractionField.getInstance());
        assertEquals(0, v0.getDimension());

        ArrayFieldVector<Fraction> v1 = new ArrayFieldVector<Fraction>(FractionField.getInstance(), 7);
        assertEquals(7, v1.getDimension());
        assertEquals(new Fraction(0), v1.getEntry(6));

        ArrayFieldVector<Fraction> v2 = new ArrayFieldVector<Fraction>(5, new Fraction(123, 100));
        assertEquals(5, v2.getDimension());
        assertEquals(new Fraction(123, 100), v2.getEntry(4));

        ArrayFieldVector<Fraction> v3 = new ArrayFieldVector<Fraction>(vec1);
        assertEquals(3, v3.getDimension());
        assertEquals(new Fraction(2), v3.getEntry(1));

        ArrayFieldVector<Fraction> v4 = new ArrayFieldVector<Fraction>(vec4, 3, 2);
        assertEquals(2, v4.getDimension());
        assertEquals(new Fraction(4), v4.getEntry(0));
        try {
            new ArrayFieldVector<Fraction>(vec4, 8, 3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

        FieldVector<Fraction> v5_i = new ArrayFieldVector<Fraction>(dvec1);
        assertEquals(9, v5_i.getDimension());
        assertEquals(new Fraction(9), v5_i.getEntry(8));

        ArrayFieldVector<Fraction> v5 = new ArrayFieldVector<Fraction>(dvec1);
        assertEquals(9, v5.getDimension());
        assertEquals(new Fraction(9), v5.getEntry(8));

        ArrayFieldVector<Fraction> v6 = new ArrayFieldVector<Fraction>(dvec1, 3, 2);
        assertEquals(2, v6.getDimension());
        assertEquals(new Fraction(4), v6.getEntry(0));
        try {
            new ArrayFieldVector<Fraction>(dvec1, 8, 3);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

        ArrayFieldVector<Fraction> v7 = new ArrayFieldVector<Fraction>(v1);
        assertEquals(7, v7.getDimension());
        assertEquals(new Fraction(0), v7.getEntry(6));

        FieldVectorTestImpl<Fraction> v7_i = new FieldVectorTestImpl<Fraction>(vec1);

        ArrayFieldVector<Fraction> v7_2 = new ArrayFieldVector<Fraction>(v7_i);
        assertEquals(3, v7_2.getDimension());
        assertEquals(new Fraction(2), v7_2.getEntry(1));

        ArrayFieldVector<Fraction> v8 = new ArrayFieldVector<Fraction>(v1, true);
        assertEquals(7, v8.getDimension());
        assertEquals(new Fraction(0), v8.getEntry(6));
        assertNotSame("testData not same object ", v1.data, v8.data);

        ArrayFieldVector<Fraction> v8_2 = new ArrayFieldVector<Fraction>(v1, false);
        assertEquals(7, v8_2.getDimension());
        assertEquals(new Fraction(0), v8_2.getEntry(6));
        assertEquals(v1.data, v8_2.data);

        ArrayFieldVector<Fraction> v9 = new ArrayFieldVector<Fraction>(v1, v3);
        assertEquals(10, v9.getDimension());
        assertEquals(new Fraction(1), v9.getEntry(7));

    }

    public void testDataInOut() {

        ArrayFieldVector<Fraction> v1 = new ArrayFieldVector<Fraction>(vec1);
        ArrayFieldVector<Fraction> v2 = new ArrayFieldVector<Fraction>(vec2);
        ArrayFieldVector<Fraction> v4 = new ArrayFieldVector<Fraction>(vec4);
        FieldVectorTestImpl<Fraction> v2_t = new FieldVectorTestImpl<Fraction>(vec2);

        FieldVector<Fraction> v_append_1 = v1.append(v2);
        assertEquals(6, v_append_1.getDimension());
        assertEquals(new Fraction(4), v_append_1.getEntry(3));

        FieldVector<Fraction> v_append_2 = v1.append(new Fraction(2));
        assertEquals(4, v_append_2.getDimension());
        assertEquals(new Fraction(2), v_append_2.getEntry(3));

        FieldVector<Fraction> v_append_3 = v1.append(vec2);
        assertEquals(6, v_append_3.getDimension());
        assertEquals(new Fraction(4), v_append_3.getEntry(3));

        FieldVector<Fraction> v_append_4 = v1.append(v2_t);
        assertEquals(6, v_append_4.getDimension());
        assertEquals(new Fraction(4), v_append_4.getEntry(3));

        FieldVector<Fraction> v_copy = v1.copy();
        assertEquals(3, v_copy.getDimension());
        assertNotSame("testData not same object ", v1.data, v_copy.getData());

        Fraction[] a_frac = v1.toArray();
        assertEquals(3, a_frac.length);
        assertNotSame("testData not same object ", v1.data, a_frac);


//      ArrayFieldVector<Fraction> vout4 = (ArrayFieldVector<Fraction>) v1.clone();
//      assertEquals(3, vout4.getDimension());
//      assertEquals(v1.data, vout4.data);


        FieldVector<Fraction> vout5 = v4.getSubVector(3, 3);
        assertEquals(3, vout5.getDimension());
        assertEquals(new Fraction(5), vout5.getEntry(1));
        try {
            v4.getSubVector(3, 7);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        ArrayFieldVector<Fraction> v_set1 = (ArrayFieldVector<Fraction>) v1.copy();
        v_set1.setEntry(1, new Fraction(11));
        assertEquals(new Fraction(11), v_set1.getEntry(1));
        try {
            v_set1.setEntry(3, new Fraction(11));
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        ArrayFieldVector<Fraction> v_set2 = (ArrayFieldVector<Fraction>) v4.copy();
        v_set2.set(3, v1);
        assertEquals(new Fraction(1), v_set2.getEntry(3));
        assertEquals(new Fraction(7), v_set2.getEntry(6));
        try {
            v_set2.set(7, v1);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }

        ArrayFieldVector<Fraction> v_set3 = (ArrayFieldVector<Fraction>) v1.copy();
        v_set3.set(new Fraction(13));
        assertEquals(new Fraction(13), v_set3.getEntry(2));

        try {
            v_set3.getEntry(23);
            fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected behavior
        }

        ArrayFieldVector<Fraction> v_set4 = (ArrayFieldVector<Fraction>) v4.copy();
        v_set4.setSubVector(3, v2_t);
        assertEquals(new Fraction(4), v_set4.getEntry(3));
        assertEquals(new Fraction(7), v_set4.getEntry(6));
        try {
            v_set4.setSubVector(7, v2_t);
            fail("MatrixIndexException expected");
        } catch (MatrixIndexException ex) {
            // expected behavior
        }


        ArrayFieldVector<Fraction> vout10 = (ArrayFieldVector<Fraction>) v1.copy();
        ArrayFieldVector<Fraction> vout10_2 = (ArrayFieldVector<Fraction>) v1.copy();
        assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, new Fraction(11, 10));
        assertNotSame(vout10, vout10_2);

    }

    public void testMapFunctions() {
        ArrayFieldVector<Fraction> v1 = new ArrayFieldVector<Fraction>(vec1);

        //octave =  v1 .+ 2.0
        FieldVector<Fraction> v_mapAdd = v1.mapAdd(new Fraction(2));
        Fraction[] result_mapAdd = {new Fraction(3), new Fraction(4), new Fraction(5)};
        checkArray("compare vectors" ,result_mapAdd,v_mapAdd.getData());

        //octave =  v1 .+ 2.0
        FieldVector<Fraction> v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(new Fraction(2));
        Fraction[] result_mapAddToSelf = {new Fraction(3), new Fraction(4), new Fraction(5)};
        checkArray("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.getData());

        //octave =  v1 .- 2.0
        FieldVector<Fraction> v_mapSubtract = v1.mapSubtract(new Fraction(2));
        Fraction[] result_mapSubtract = {new Fraction(-1), new Fraction(0), new Fraction(1)};
        checkArray("compare vectors" ,result_mapSubtract,v_mapSubtract.getData());

        //octave =  v1 .- 2.0
        FieldVector<Fraction> v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(new Fraction(2));
        Fraction[] result_mapSubtractToSelf = {new Fraction(-1), new Fraction(0), new Fraction(1)};
        checkArray("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.getData());

        //octave =  v1 .* 2.0
        FieldVector<Fraction> v_mapMultiply = v1.mapMultiply(new Fraction(2));
        Fraction[] result_mapMultiply = {new Fraction(2), new Fraction(4), new Fraction(6)};
        checkArray("compare vectors" ,result_mapMultiply,v_mapMultiply.getData());

        //octave =  v1 .* 2.0
        FieldVector<Fraction> v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(new Fraction(2));
        Fraction[] result_mapMultiplyToSelf = {new Fraction(2), new Fraction(4), new Fraction(6)};
        checkArray("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.getData());

        //octave =  v1 ./ 2.0
        FieldVector<Fraction> v_mapDivide = v1.mapDivide(new Fraction(2));
        Fraction[] result_mapDivide = {new Fraction(1, 2), new Fraction(1), new Fraction(3, 2)};
        checkArray("compare vectors" ,result_mapDivide,v_mapDivide.getData());

        //octave =  v1 ./ 2.0
        FieldVector<Fraction> v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(new Fraction(2));
        Fraction[] result_mapDivideToSelf = {new Fraction(1, 2), new Fraction(1), new Fraction(3, 2)};
        checkArray("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.getData());

        //octave =  v1 .^-1
        FieldVector<Fraction> v_mapInv = v1.mapInv();
        Fraction[] result_mapInv = {new Fraction(1),new Fraction(1, 2),new Fraction(1, 3)};
        checkArray("compare vectors" ,result_mapInv,v_mapInv.getData());

        //octave =  v1 .^-1
        FieldVector<Fraction> v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapInvToSelf();
        Fraction[] result_mapInvToSelf = {new Fraction(1),new Fraction(1, 2),new Fraction(1, 3)};
        checkArray("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.getData());

    }

    public void testBasicFunctions() {
        ArrayFieldVector<Fraction> v1 = new ArrayFieldVector<Fraction>(vec1);
        ArrayFieldVector<Fraction> v2 = new ArrayFieldVector<Fraction>(vec2);
        new ArrayFieldVector<Fraction>(vec_null);

        FieldVectorTestImpl<Fraction> v2_t = new FieldVectorTestImpl<Fraction>(vec2);

        //octave =  v1 + v2
        ArrayFieldVector<Fraction> v_add = v1.add(v2);
        Fraction[] result_add = {new Fraction(5), new Fraction(7), new Fraction(9)};
        checkArray("compare vect" ,v_add.getData(),result_add);

        FieldVectorTestImpl<Fraction> vt2 = new FieldVectorTestImpl<Fraction>(vec2);
        FieldVector<Fraction> v_add_i = v1.add(vt2);
        Fraction[] result_add_i = {new Fraction(5), new Fraction(7), new Fraction(9)};
        checkArray("compare vect" ,v_add_i.getData(),result_add_i);

        //octave =  v1 - v2
        ArrayFieldVector<Fraction> v_subtract = v1.subtract(v2);
        Fraction[] result_subtract = {new Fraction(-3), new Fraction(-3), new Fraction(-3)};
        checkArray("compare vect" ,v_subtract.getData(),result_subtract);

        FieldVector<Fraction> v_subtract_i = v1.subtract(vt2);
        Fraction[] result_subtract_i = {new Fraction(-3), new Fraction(-3), new Fraction(-3)};
        checkArray("compare vect" ,v_subtract_i.getData(),result_subtract_i);

        // octave v1 .* v2
        ArrayFieldVector<Fraction>  v_ebeMultiply = v1.ebeMultiply(v2);
        Fraction[] result_ebeMultiply = {new Fraction(4), new Fraction(10), new Fraction(18)};
        checkArray("compare vect" ,v_ebeMultiply.getData(),result_ebeMultiply);

        FieldVector<Fraction>  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        Fraction[] result_ebeMultiply_2 = {new Fraction(4), new Fraction(10), new Fraction(18)};
        checkArray("compare vect" ,v_ebeMultiply_2.getData(),result_ebeMultiply_2);

        // octave v1 ./ v2
        ArrayFieldVector<Fraction>  v_ebeDivide = v1.ebeDivide(v2);
        Fraction[] result_ebeDivide = {new Fraction(1, 4), new Fraction(2, 5), new Fraction(1, 2)};
        checkArray("compare vect" ,v_ebeDivide.getData(),result_ebeDivide);

        FieldVector<Fraction>  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        Fraction[] result_ebeDivide_2 = {new Fraction(1, 4), new Fraction(2, 5), new Fraction(1, 2)};
        checkArray("compare vect" ,v_ebeDivide_2.getData(),result_ebeDivide_2);

        // octave  dot(v1,v2)
        Fraction dot =  v1.dotProduct(v2);
        assertEquals("compare val ",new Fraction(32), dot);

        // octave  dot(v1,v2_t)
        Fraction dot_2 =  v1.dotProduct(v2_t);
        assertEquals("compare val ",new Fraction(32), dot_2);

        FieldMatrix<Fraction> m_outerProduct = v1.outerProduct(v2);
        assertEquals("compare val ",new Fraction(4), m_outerProduct.getEntry(0,0));

        FieldMatrix<Fraction> m_outerProduct_2 = v1.outerProduct(v2_t);
        assertEquals("compare val ",new Fraction(4), m_outerProduct_2.getEntry(0,0));

        ArrayFieldVector<Fraction> v_projection = v1.projection(v2);
        Fraction[] result_projection = {new Fraction(128, 77), new Fraction(160, 77), new Fraction(192, 77)};
        checkArray("compare vect", v_projection.getData(), result_projection);

        FieldVector<Fraction> v_projection_2 = v1.projection(v2_t);
        Fraction[] result_projection_2 = {new Fraction(128, 77), new Fraction(160, 77), new Fraction(192, 77)};
        checkArray("compare vect", v_projection_2.getData(), result_projection_2);

    }

    public void testMisc() {
        ArrayFieldVector<Fraction> v1 = new ArrayFieldVector<Fraction>(vec1);
        ArrayFieldVector<Fraction> v4 = new ArrayFieldVector<Fraction>(vec4);
        FieldVector<Fraction> v4_2 = new ArrayFieldVector<Fraction>(vec4);

        String out1 = v1.toString();
        assertTrue("some output ",  out1.length()!=0);
        /*
         Fraction[] dout1 = v1.copyOut();
        assertEquals(3, dout1.length);
        assertNotSame("testData not same object ", v1.data, dout1);
         */
        try {
            v1.checkVectorDimensions(2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

       try {
            v1.checkVectorDimensions(v4);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

        try {
            v1.checkVectorDimensions(v4_2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

    }

    public void testSerial()  {
        ArrayFieldVector<Fraction> v = new ArrayFieldVector<Fraction>(vec1);
        assertEquals(v,TestUtils.serializeAndRecover(v));
    }

    public void testZeroVectors() {

        // when the field is not specified, array cannot be empty
        try {
            new ArrayFieldVector<Fraction>(new Fraction[0]);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }
        try {
            new ArrayFieldVector<Fraction>(new Fraction[0], true);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }
        try {
            new ArrayFieldVector<Fraction>(new Fraction[0], false);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected behavior
        }

        // when the field is specified, array can be empty
        assertEquals(0, new ArrayFieldVector<Fraction>(FractionField.getInstance(), new Fraction[0]).getDimension());
        assertEquals(0, new ArrayFieldVector<Fraction>(FractionField.getInstance(), new Fraction[0], true).getDimension());
        assertEquals(0, new ArrayFieldVector<Fraction>(FractionField.getInstance(), new Fraction[0], false).getDimension());

    }

    /** verifies that two vectors are equals */
    protected void checkArray(String msg, Fraction[] m, Fraction[] n) {
        if (m.length != n.length) {
            fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " +  i + " elements differ", m[i],n[i]);
        }
    }

}
