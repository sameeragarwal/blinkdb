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

public class LUDecompositionImplTest extends TestCase {
    private double[][] testData = {
            { 1.0, 2.0, 3.0},
            { 2.0, 5.0, 3.0},
            { 1.0, 0.0, 8.0}
    };
    private double[][] testDataMinus = {
            { -1.0, -2.0, -3.0},
            { -2.0, -5.0, -3.0},
            { -1.0,  0.0, -8.0}
    };
    private double[][] luData = {
            { 2.0, 3.0, 3.0 },
            { 0.0, 5.0, 7.0 },
            { 6.0, 9.0, 8.0 }
    };

    // singular matrices
    private double[][] singular = {
            { 2.0, 3.0 },
            { 2.0, 3.0 }
    };
    private double[][] bigSingular = {
            { 1.0, 2.0,   3.0,    4.0 },
            { 2.0, 5.0,   3.0,    4.0 },
            { 7.0, 3.0, 256.0, 1930.0 },
            { 3.0, 7.0,   6.0,    8.0 }
    }; // 4th row = 1st + 2nd

    private static final double entryTolerance = 10e-16;

    private static final double normTolerance = 10e-14;

    public LUDecompositionImplTest(String name) {
        super(name);
    }

    /** test dimensions */
    public void testDimensions() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        LUDecomposition LU = new LUDecompositionImpl(matrix);
        assertEquals(testData.length, LU.getL().getRowDimension());
        assertEquals(testData.length, LU.getL().getColumnDimension());
        assertEquals(testData.length, LU.getU().getRowDimension());
        assertEquals(testData.length, LU.getU().getColumnDimension());
        assertEquals(testData.length, LU.getP().getRowDimension());
        assertEquals(testData.length, LU.getP().getColumnDimension());

    }

    /** test non-square matrix */
    public void testNonSquare() {
        try {
            new LUDecompositionImpl(MatrixUtils.createRealMatrix(new double[3][2]));
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        }
    }

    /** test PA = LU */
    public void testPAEqualLU() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        LUDecomposition lu = new LUDecompositionImpl(matrix);
        RealMatrix l = lu.getL();
        RealMatrix u = lu.getU();
        RealMatrix p = lu.getP();
        double norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(testDataMinus);
        lu = new LUDecompositionImpl(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealIdentityMatrix(17);
        lu = new LUDecompositionImpl(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        norm = l.multiply(u).subtract(p.multiply(matrix)).getNorm();
        assertEquals(0, norm, normTolerance);

        matrix = MatrixUtils.createRealMatrix(singular);
        lu = new LUDecompositionImpl(matrix);
        assertFalse(lu.getSolver().isNonSingular());
        assertNull(lu.getL());
        assertNull(lu.getU());
        assertNull(lu.getP());

        matrix = MatrixUtils.createRealMatrix(bigSingular);
        lu = new LUDecompositionImpl(matrix);
        assertFalse(lu.getSolver().isNonSingular());
        assertNull(lu.getL());
        assertNull(lu.getU());
        assertNull(lu.getP());

    }

    /** test that L is lower triangular with unit diagonal */
    public void testLLowerTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix l = new LUDecompositionImpl(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            assertEquals(l.getEntry(i, i), 1, entryTolerance);
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                assertEquals(l.getEntry(i, j), 0, entryTolerance);
            }
        }
    }

    /** test that U is upper triangular */
    public void testUUpperTriangular() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix u = new LUDecompositionImpl(matrix).getU();
        for (int i = 0; i < u.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                assertEquals(u.getEntry(i, j), 0, entryTolerance);
            }
        }
    }

    /** test that P is a permutation matrix */
    public void testPPermutation() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(testData);
        RealMatrix p   = new LUDecompositionImpl(matrix).getP();

        RealMatrix ppT = p.multiply(p.transpose());
        RealMatrix id  = MatrixUtils.createRealIdentityMatrix(p.getRowDimension());
        assertEquals(0, ppT.subtract(id).getNorm(), normTolerance);

        for (int i = 0; i < p.getRowDimension(); i++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int j = 0; j < p.getColumnDimension(); j++) {
                final double e = p.getEntry(i, j);
                if (e == 0) {
                    ++zeroCount;
                } else if (e == 1) {
                    ++oneCount;
                } else {
                    ++otherCount;
                }
            }
            assertEquals(p.getColumnDimension() - 1, zeroCount);
            assertEquals(1, oneCount);
            assertEquals(0, otherCount);
        }

        for (int j = 0; j < p.getColumnDimension(); j++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int i = 0; i < p.getRowDimension(); i++) {
                final double e = p.getEntry(i, j);
                if (e == 0) {
                    ++zeroCount;
                } else if (e == 1) {
                    ++oneCount;
                } else {
                    ++otherCount;
                }
            }
            assertEquals(p.getRowDimension() - 1, zeroCount);
            assertEquals(1, oneCount);
            assertEquals(0, otherCount);
        }

    }


    /** test singular */
    public void testSingular() {
        LUDecomposition lu =
            new LUDecompositionImpl(MatrixUtils.createRealMatrix(testData));
        assertTrue(lu.getSolver().isNonSingular());
        lu = new LUDecompositionImpl(MatrixUtils.createRealMatrix(singular));
        assertFalse(lu.getSolver().isNonSingular());
        lu = new LUDecompositionImpl(MatrixUtils.createRealMatrix(bigSingular));
        assertFalse(lu.getSolver().isNonSingular());
    }

    /** test matrices values */
    public void testMatricesValues1() {
       LUDecomposition lu =
            new LUDecompositionImpl(MatrixUtils.createRealMatrix(testData));
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.5, 1.0, 0.0 },
                { 0.5, 0.2, 1.0 }
        });
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                { 2.0,  5.0, 3.0 },
                { 0.0, -2.5, 6.5 },
                { 0.0,  0.0, 0.2 }
        });
        RealMatrix pRef = MatrixUtils.createRealMatrix(new double[][] {
                { 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 1.0 },
                { 1.0, 0.0, 0.0 }
        });
        int[] pivotRef = { 1, 2, 0 };

        // check values against known references
        RealMatrix l = lu.getL();
        assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix u = lu.getU();
        assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-13);
        RealMatrix p = lu.getP();
        assertEquals(0, p.subtract(pRef).getNorm(), 1.0e-13);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        assertTrue(l == lu.getL());
        assertTrue(u == lu.getU());
        assertTrue(p == lu.getP());

    }

    /** test matrices values */
    public void testMatricesValues2() {
       LUDecomposition lu =
            new LUDecompositionImpl(MatrixUtils.createRealMatrix(luData));
        RealMatrix lRef = MatrixUtils.createRealMatrix(new double[][] {
                {    1.0,    0.0, 0.0 },
                {    0.0,    1.0, 0.0 },
                { 1.0 / 3.0, 0.0, 1.0 }
        });
        RealMatrix uRef = MatrixUtils.createRealMatrix(new double[][] {
                { 6.0, 9.0,    8.0    },
                { 0.0, 5.0,    7.0    },
                { 0.0, 0.0, 1.0 / 3.0 }
        });
        RealMatrix pRef = MatrixUtils.createRealMatrix(new double[][] {
                { 0.0, 0.0, 1.0 },
                { 0.0, 1.0, 0.0 },
                { 1.0, 0.0, 0.0 }
        });
        int[] pivotRef = { 2, 1, 0 };

        // check values against known references
        RealMatrix l = lu.getL();
        assertEquals(0, l.subtract(lRef).getNorm(), 1.0e-13);
        RealMatrix u = lu.getU();
        assertEquals(0, u.subtract(uRef).getNorm(), 1.0e-13);
        RealMatrix p = lu.getP();
        assertEquals(0, p.subtract(pRef).getNorm(), 1.0e-13);
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivotRef.length; ++i) {
            assertEquals(pivotRef[i], pivot[i]);
        }

        // check the same cached instance is returned the second time
        assertTrue(l == lu.getL());
        assertTrue(u == lu.getU());
        assertTrue(p == lu.getP());

    }

}
