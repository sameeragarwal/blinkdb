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

import java.util.Random;

import org.apache.commons.math.linear.MatrixVisitorException;

import junit.framework.TestCase;

public class QRSolverTest extends TestCase {
    double[][] testData3x3NonSingular = {
            { 12, -51,   4 },
            {  6, 167, -68 },
            { -4,  24, -41 }
    };

    double[][] testData3x3Singular = {
            { 1, 2,  2 },
            { 2, 4,  6 },
            { 4, 8, 12 }
    };

    double[][] testData3x4 = {
            { 12, -51,   4, 1 },
            {  6, 167, -68, 2 },
            { -4,  24, -41, 3 }
    };

    double[][] testData4x3 = {
            { 12, -51,   4 },
            {  6, 167, -68 },
            { -4,  24, -41 },
            { -5,  34,   7 }
    };

    public QRSolverTest(String name) {
        super(name);
    }

    /** test rank */
    public void testRank() {
        DecompositionSolver solver =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular)).getSolver();
        assertTrue(solver.isNonSingular());

        solver = new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3Singular)).getSolver();
        assertFalse(solver.isNonSingular());

        solver = new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x4)).getSolver();
        assertTrue(solver.isNonSingular());

        solver = new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData4x3)).getSolver();
        assertTrue(solver.isNonSingular());

    }

    /** test solve dimension errors */
    public void testSolveDimensionErrors() {
        DecompositionSolver solver =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular)).getSolver();
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
            solver.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected behavior
        }
    }

    /** test solve rank errors */
    public void testSolveRankErrors() {
        DecompositionSolver solver =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3Singular)).getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[3][2]);
        try {
            solver.solve(b);
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        }
        try {
            solver.solve(b.getColumn(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        }
        try {
            solver.solve(b.getColumnVector(0));
            fail("an exception should have been thrown");
        } catch (InvalidMatrixException iae) {
            // expected behavior
        }
    }

    /** test solve */
    public void testSolve() {
        QRDecomposition decomposition =
            new QRDecompositionImpl(MatrixUtils.createRealMatrix(testData3x3NonSingular));
        DecompositionSolver solver = decomposition.getSolver();
        RealMatrix b = MatrixUtils.createRealMatrix(new double[][] {
                { -102, 12250 }, { 544, 24500 }, { 167, -36750 }
        });
        RealMatrix xRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 2515 }, { 2, 422 }, { -3, 898 }
        });

        // using RealMatrix
        assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), 2.0e-16 * xRef.getNorm());

        // using double[]
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            final double[] x = solver.solve(b.getColumn(i));
            final double error = new ArrayRealVector(x).subtract(xRef.getColumnVector(i)).getNorm();
            assertEquals(0, error, 3.0e-16 * xRef.getColumnVector(i).getNorm());
        }

        // using ArrayRealVector
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            final RealVector x = solver.solve(b.getColumnVector(i));
            final double error = x.subtract(xRef.getColumnVector(i)).getNorm();
            assertEquals(0, error, 3.0e-16 * xRef.getColumnVector(i).getNorm());
        }

        // using RealVector with an alternate implementation
        for (int i = 0; i < b.getColumnDimension(); ++i) {
            ArrayRealVectorTest.RealVectorTestImpl v =
                new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
            final RealVector x = solver.solve(v);
            final double error = x.subtract(xRef.getColumnVector(i)).getNorm();
            assertEquals(0, error, 3.0e-16 * xRef.getColumnVector(i).getNorm());
        }

    }

    public void testOverdetermined() throws MatrixVisitorException {
        final Random r    = new Random(5559252868205245l);
        int          p    = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int          q    = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        RealMatrix   a    = createTestMatrix(r, p, q);
        RealMatrix   xRef = createTestMatrix(r, q, BlockRealMatrix.BLOCK_SIZE + 3);

        // build a perturbed system: A.X + noise = B
        RealMatrix b = a.multiply(xRef);
        final double noise = 0.001;
        b.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(int row, int column, double value) {
                return value * (1.0 + noise * (2 * r.nextDouble() - 1));
            }
        });

        // despite perturbation, the least square solution should be pretty good
        RealMatrix x = new QRDecompositionImpl(a).getSolver().solve(b);
        assertEquals(0, x.subtract(xRef).getNorm(), 0.01 * noise * p * q);

    }

    public void testUnderdetermined() throws MatrixVisitorException {
        final Random r    = new Random(42185006424567123l);
        int          p    = (5 * BlockRealMatrix.BLOCK_SIZE) / 4;
        int          q    = (7 * BlockRealMatrix.BLOCK_SIZE) / 4;
        RealMatrix   a    = createTestMatrix(r, p, q);
        RealMatrix   xRef = createTestMatrix(r, q, BlockRealMatrix.BLOCK_SIZE + 3);
        RealMatrix   b    = a.multiply(xRef);
        RealMatrix   x = new QRDecompositionImpl(a).getSolver().solve(b);

        // too many equations, the system cannot be solved at all
        assertTrue(x.subtract(xRef).getNorm() / (p * q) > 0.01);

        // the last unknown should have been set to 0
        assertEquals(0.0, x.getSubMatrix(p, q - 1, 0, x.getColumnDimension() - 1).getNorm());

    }

    private RealMatrix createTestMatrix(final Random r, final int rows, final int columns) throws MatrixVisitorException {
        RealMatrix m = MatrixUtils.createRealMatrix(rows, columns);
        m.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor(){
            @Override
            public double visit(int row, int column, double value) {
                return 2.0 * r.nextDouble() - 1.0;
            }
        });
        return m;
    }
}
