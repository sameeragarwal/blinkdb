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

import junit.framework.TestCase;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.linear.RealVector.Entry;
import org.apache.commons.math.util.FastMath;

import java.util.Iterator;
import java.util.Random;

/**
 *
 */
public class AbstractRealVectorTest extends TestCase {
    private double[] vec1 = { 1d, 2d, 3d, 4d, 5d };
    private double[] vec2 = { -3d, 0d, 0d, 2d, 1d };

    private static class TestVectorImpl extends AbstractRealVector {
        private double[] values;

        TestVectorImpl(double[] values) {
            this.values = values;
        }

        @Override
        public double[] getData() { return values; }

        @Override
        public AbstractRealVector copy() {
            return new TestVectorImpl(values.clone());
        }


        UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Test implementation only supports methods necessary for testing");
        }

        @Override
        public RealVector add(RealVector v) throws IllegalArgumentException {
            RealVector result = new ArrayRealVector(v);
            return result.add(this);
        }

        @Override
        public RealVector subtract(RealVector v) throws IllegalArgumentException {
            RealVector result = new ArrayRealVector(v);
            return result.subtract(this).mapMultiplyToSelf(-1);
        }

        @Override
        public RealVector mapAddToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] += d;
            }
            return this;
        }

        @Override
        public RealVector mapSubtractToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] -= d;
            }
            return this;
        }

        @Override
        public RealVector mapMultiplyToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] *= d;
            }
            return this;
        }

        @Override
        public RealVector mapDivideToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] /= d;
            }
            return this;
        }

        @Override
        public RealVector mapPowToSelf(double d) {
            for(int i=0; i<values.length; i++) {
                values[i] = FastMath.pow(values[i], d);
            }
            return this;
        }

        @Override
        public RealVector mapInvToSelf() {
            for(int i=0; i<values.length; i++) {
                values[i] = 1/values[i];
            }
            return this;
        }

        public RealVector ebeMultiply(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        public RealVector ebeDivide(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        @Override
        public double dotProduct(RealVector v) throws IllegalArgumentException {
            throw unsupported();
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

        public RealVector projection(RealVector v) throws IllegalArgumentException {
            throw unsupported();
        }

        public double getEntry(int index) throws MatrixIndexException {
            return values[index];
        }

        public void setEntry(int index, double value) throws MatrixIndexException {
            values[index] = value;
        }

        public int getDimension() {
            return values.length;
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

        public boolean isNaN() {
            throw unsupported();
        }

        public boolean isInfinite() {
            throw unsupported();
        }
    }

    private static void assertEquals(double[] d1, double[] d2) {
        assertEquals(d1.length, d2.length);
        for(int i=0; i<d1.length; i++) assertEquals(d1[i], d2[i]);
    }

    public void testMap() throws Exception {
        double[] vec1Squared = { 1d, 4d, 9d, 16d, 25d };
        RealVector v = new TestVectorImpl(vec1.clone());
        RealVector w = v.map(new UnivariateRealFunction() { public double value(double x) { return x * x; } });
        assertEquals(vec1Squared, w.getData());
    }

    public void testIterator() throws Exception {
        RealVector v = new TestVectorImpl(vec2.clone());
        Entry e;
        int i = 0;
        for(Iterator<Entry> it = v.iterator(); it.hasNext() && (e = it.next()) != null; i++) {
            assertEquals(vec2[i], e.getValue());
        }
    }

    public void testSparseIterator() throws Exception {
        RealVector v = new TestVectorImpl(vec2.clone());
        Entry e;
        int i = 0;
        double[] nonDefaultV2 = { -3d, 2d, 1d };
        for(Iterator<Entry> it = v.sparseIterator(); it.hasNext() && (e = it.next()) != null; i++) {
            assertEquals(nonDefaultV2[i], e.getValue());
        }
        double [] onlyOne = {0d, 1.0, 0d};
        v = new TestVectorImpl(onlyOne);
        for(Iterator<Entry> it = v.sparseIterator(); it.hasNext() && (e = it.next()) != null; ) {
            assertEquals(onlyOne[1], e.getValue());
        }
        
    }

    public void testClone() throws Exception {
        double[] d = new double[1000000];
        Random r = new Random(1234);
        for(int i=0;i<d.length; i++) d[i] = r.nextDouble();
        assertTrue(new ArrayRealVector(d).getNorm() > 0);
        double[] c = d.clone();
        c[0] = 1;
        assertNotSame(c[0], d[0]);
        d[0] = 1;
        assertEquals(new ArrayRealVector(d).getNorm(), new ArrayRealVector(c).getNorm());
        long cloneTime = 0;
        long setAndAddTime = 0;
        for(int i=0; i<10; i++) {
          long start = System.nanoTime();
          double[] v = d.clone();
          for(int j=0; j<v.length; j++) v[j] += 1234.5678;
          if(i > 4) cloneTime += System.nanoTime() - start;
          start = System.nanoTime();
          v = new double[d.length];
          for(int j=0; j<v.length; j++) v[j] = d[j] + 1234.5678;
          if(i > 4) setAndAddTime += System.nanoTime() - start;
        }
    }
}
