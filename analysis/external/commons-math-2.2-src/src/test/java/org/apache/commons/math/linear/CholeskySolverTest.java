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

import org.apache.commons.math.MathException;

public class CholeskySolverTest extends TestCase {

    private double[][] testData = new double[][] {
            {  1,  2,   4,   7,  11 },
            {  2, 13,  23,  38,  58 },
            {  4, 23,  77, 122, 182 },
            {  7, 38, 122, 294, 430 },
            { 11, 58, 182, 430, 855 }
    };

    public CholeskySolverTest(String name) {
        super(name);
    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() throws MathException {
        DecompositionSolver solver =
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(testData)).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[2][2]);
        try {
            solver.solve(b);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        }
        try {
            solver.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        }
        try {
            solver.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        }
    }

    /** test solve */
    public void testSolve() throws MathException {
        DecompositionSolver solver =
            new CholeskyDecompositionImpl(MatrixUtils.createRealMatrix(testData)).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
                {   78,  -13,    1 },
                {  414,  -62,   -1 },
                { 1312, -202,  -37 },
                { 2989, -542,  145 },
                { 5510, -1465, 201 }
        });
        RealMatrix xRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1,  0,  1 },
                { 0,  1,  1 },
                { 2,  1, -4 },
                { 2,  2,  2 },
                { 5, -3,  0 }
        });

        // using RealMatrix
        assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), 1.0e-13);

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         new ArrayRealVector(solver.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using ArrayRealVector
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            assertEquals(0,
                         solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

        // using RealVector with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            ArrayRealVectorTest.RealVectorTestImpl v =
                new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
            assertEquals(0,
                         solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),
                         1.0e-13);
        }

    }

    /** test determinant */
    public void testDeterminant() throws MathException {
        assertEquals(7290000.0, getDeterminant(MatrixUtils.createRealMatrix(testData)), 1.0e-15);
    }

    private double getDeterminant(RealMatrix m) throws MathException {
        return new CholeskyDecompositionImpl(m).getDeterminant();
    }

}
