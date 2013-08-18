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

import org.apache.commons.math.TestUtils;

/**
 * Test cases for the {@link OpenMapRealMatrix} class.
 *
 * @version $Revision: 902201 $ $Date: 2008-11-07 06:48:13 -0800 (Fri, 07 Nov
 *          2008) $
 */
public final class SparseRealMatrixTest extends TestCase {

    // 3 x 3 identity matrix
    protected double[][] id = { { 1d, 0d, 0d }, { 0d, 1d, 0d }, { 0d, 0d, 1d } };
    // Test data for group operations
    protected double[][] testData = { { 1d, 2d, 3d }, { 2d, 5d, 3d },
            { 1d, 0d, 8d } };
    protected double[][] testDataLU = { { 2d, 5d, 3d }, { .5d, -2.5d, 6.5d },
            { 0.5d, 0.2d, .2d } };
    protected double[][] testDataPlus2 = { { 3d, 4d, 5d }, { 4d, 7d, 5d },
            { 3d, 2d, 10d } };
    protected double[][] testDataMinus = { { -1d, -2d, -3d },
            { -2d, -5d, -3d }, { -1d, 0d, -8d } };
    protected double[] testDataRow1 = { 1d, 2d, 3d };
    protected double[] testDataCol3 = { 3d, 3d, 8d };
    protected double[][] testDataInv = { { -40d, 16d, 9d }, { 13d, -5d, -3d },
            { 5d, -2d, -1d } };
    protected double[] preMultTest = { 8, 12, 33 };
    protected double[][] testData2 = { { 1d, 2d, 3d }, { 2d, 5d, 3d } };
    protected double[][] testData2T = { { 1d, 2d }, { 2d, 5d }, { 3d, 3d } };
    protected double[][] testDataPlusInv = { { -39d, 18d, 12d },
            { 15d, 0d, 0d }, { 6d, -2d, 7d } };

    // lu decomposition tests
    protected double[][] luData = { { 2d, 3d, 3d }, { 0d, 5d, 7d }, { 6d, 9d, 8d } };
    protected double[][] luDataLUDecomposition = { { 6d, 9d, 8d },
            { 0d, 5d, 7d }, { 0.33333333333333, 0d, 0.33333333333333 } };

    // singular matrices
    protected double[][] singular = { { 2d, 3d }, { 2d, 3d } };
    protected double[][] bigSingular = { { 1d, 2d, 3d, 4d },
            { 2d, 5d, 3d, 4d }, { 7d, 3d, 256d, 1930d }, { 3d, 7d, 6d, 8d } }; // 4th

    // row
    // =
    // 1st
    // +
    // 2nd
    protected double[][] detData = { { 1d, 2d, 3d }, { 4d, 5d, 6d },
            { 7d, 8d, 10d } };
    protected double[][] detData2 = { { 1d, 3d }, { 2d, 4d } };

    // vectors
    protected double[] testVector = { 1, 2, 3 };
    protected double[] testVector2 = { 1, 2, 3, 4 };

    // submatrix accessor tests
    protected double[][] subTestData = { { 1, 2, 3, 4 },
            { 1.5, 2.5, 3.5, 4.5 }, { 2, 4, 6, 8 }, { 4, 5, 6, 7 } };

    // array selections
    protected double[][] subRows02Cols13 = { { 2, 4 }, { 4, 8 } };
    protected double[][] subRows03Cols12 = { { 2, 3 }, { 5, 6 } };
    protected double[][] subRows03Cols123 = { { 2, 3, 4 }, { 5, 6, 7 } };

    // effective permutations
    protected double[][] subRows20Cols123 = { { 4, 6, 8 }, { 2, 3, 4 } };
    protected double[][] subRows31Cols31 = { { 7, 5 }, { 4.5, 2.5 } };

    // contiguous ranges
    protected double[][] subRows01Cols23 = { { 3, 4 }, { 3.5, 4.5 } };
    protected double[][] subRows23Cols00 = { { 2 }, { 4 } };
    protected double[][] subRows00Cols33 = { { 4 } };

    // row matrices
    protected double[][] subRow0 = { { 1, 2, 3, 4 } };
    protected double[][] subRow3 = { { 4, 5, 6, 7 } };

    // column matrices
    protected double[][] subColumn1 = { { 2 }, { 2.5 }, { 4 }, { 5 } };
    protected double[][] subColumn3 = { { 4 }, { 4.5 }, { 8 }, { 7 } };

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    public SparseRealMatrixTest(String name) {
        super(name);
    }

    /** test dimensions */
    public void testDimensions() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix m2 = createSparseMatrix(testData2);
        assertEquals("testData row dimension", 3, m.getRowDimension());
        assertEquals("testData column dimension", 3, m.getColumnDimension());
        assertTrue("testData is square", m.isSquare());
        assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
        assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
        assertTrue("testData2 is not square", !m2.isSquare());
    }

    /** test copy functions */
    public void testCopyFunctions() {
        OpenMapRealMatrix m1 = createSparseMatrix(testData);
        RealMatrix m2 = m1.copy();
        assertEquals(m1.getClass(), m2.getClass());
        assertEquals((m2), m1);
        OpenMapRealMatrix m3 = createSparseMatrix(testData);
        RealMatrix m4 = m3.copy();
        assertEquals(m3.getClass(), m4.getClass());
        assertEquals((m4), m3);
    }

    /** test add */
    public void testAdd() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
        OpenMapRealMatrix mDataPlusInv = createSparseMatrix(testDataPlusInv);
        RealMatrix mPlusMInv = m.add(mInv);
        for (int row = 0; row < m.getRowDimension(); row++) {
            for (int col = 0; col < m.getColumnDimension(); col++) {
                assertEquals("sum entry entry",
                    mDataPlusInv.getEntry(row, col), mPlusMInv.getEntry(row, col),
                    entryTolerance);
            }
        }
    }

    /** test add failure */
    public void testAddFail() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix m2 = createSparseMatrix(testData2);
        try {
            m.add(m2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test norm */
    public void testNorm() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix m2 = createSparseMatrix(testData2);
        assertEquals("testData norm", 14d, m.getNorm(), entryTolerance);
        assertEquals("testData2 norm", 7d, m2.getNorm(), entryTolerance);
    }

    /** test m-n = m + -n */
    public void testPlusMinus() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix n = createSparseMatrix(testDataInv);
        assertClose("m-n = m + -n", m.subtract(n),
            n.scalarMultiply(-1d).add(m), entryTolerance);
        try {
            m.subtract(createSparseMatrix(testData2));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test multiply */
    public void testMultiply() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
        OpenMapRealMatrix identity = createSparseMatrix(id);
        OpenMapRealMatrix m2 = createSparseMatrix(testData2);
        assertClose("inverse multiply", m.multiply(mInv), identity,
                entryTolerance);
        assertClose("inverse multiply", m.multiply(new BlockRealMatrix(testDataInv)), identity,
                    entryTolerance);
        assertClose("inverse multiply", mInv.multiply(m), identity,
                entryTolerance);
        assertClose("identity multiply", m.multiply(identity), m,
                entryTolerance);
        assertClose("identity multiply", identity.multiply(mInv), mInv,
                entryTolerance);
        assertClose("identity multiply", m2.multiply(identity), m2,
                entryTolerance);
        try {
            m.multiply(createSparseMatrix(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    // Additional Test for Array2DRowRealMatrixTest.testMultiply

    private double[][] d3 = new double[][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
    private double[][] d4 = new double[][] { { 1 }, { 2 }, { 3 }, { 4 } };
    private double[][] d5 = new double[][] { { 30 }, { 70 } };

    public void testMultiply2() {
        RealMatrix m3 = createSparseMatrix(d3);
        RealMatrix m4 = createSparseMatrix(d4);
        RealMatrix m5 = createSparseMatrix(d5);
        assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
    }

    /** test trace */
    public void testTrace() {
        RealMatrix m = createSparseMatrix(id);
        assertEquals("identity trace", 3d, m.getTrace(), entryTolerance);
        m = createSparseMatrix(testData2);
        try {
            m.getTrace();
            fail("Expecting NonSquareMatrixException");
        } catch (NonSquareMatrixException ex) {
            // ignored
        }
    }

    /** test sclarAdd */
    public void testScalarAdd() {
        RealMatrix m = createSparseMatrix(testData);
        assertClose("scalar add", createSparseMatrix(testDataPlus2),
            m.scalarAdd(2d), entryTolerance);
    }

    /** test operate */
    public void testOperate() {
        RealMatrix m = createSparseMatrix(id);
        assertClose("identity operate", testVector, m.operate(testVector),
                entryTolerance);
        assertClose("identity operate", testVector, m.operate(
                new ArrayRealVector(testVector)).getData(), entryTolerance);
        m = createSparseMatrix(bigSingular);
        try {
            m.operate(testVector);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test issue MATH-209 */
    public void testMath209() {
        RealMatrix a = createSparseMatrix(new double[][] {
                { 1, 2 }, { 3, 4 }, { 5, 6 } });
        double[] b = a.operate(new double[] { 1, 1 });
        assertEquals(a.getRowDimension(), b.length);
        assertEquals(3.0, b[0], 1.0e-12);
        assertEquals(7.0, b[1], 1.0e-12);
        assertEquals(11.0, b[2], 1.0e-12);
    }

    /** test transpose */
    public void testTranspose() {

        RealMatrix m = createSparseMatrix(testData);
        RealMatrix mIT = new LUDecompositionImpl(m).getSolver().getInverse().transpose();
        RealMatrix mTI = new LUDecompositionImpl(m.transpose()).getSolver().getInverse();
        assertClose("inverse-transpose", mIT, mTI, normTolerance);
        m = createSparseMatrix(testData2);
        RealMatrix mt = createSparseMatrix(testData2T);
        assertClose("transpose",mt,m.transpose(),normTolerance);
    }

    /** test preMultiply by vector */
    public void testPremultiplyVector() {
        RealMatrix m = createSparseMatrix(testData);
        assertClose("premultiply", m.preMultiply(testVector), preMultTest,
            normTolerance);
        assertClose("premultiply", m.preMultiply(
            new ArrayRealVector(testVector).getData()), preMultTest, normTolerance);
        m = createSparseMatrix(bigSingular);
        try {
            m.preMultiply(testVector);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    public void testPremultiply() {
        RealMatrix m3 = createSparseMatrix(d3);
        RealMatrix m4 = createSparseMatrix(d4);
        RealMatrix m5 = createSparseMatrix(d5);
        assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);

        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
        OpenMapRealMatrix identity = createSparseMatrix(id);
        assertClose("inverse multiply", m.preMultiply(mInv), identity,
                entryTolerance);
        assertClose("inverse multiply", mInv.preMultiply(m), identity,
                entryTolerance);
        assertClose("identity multiply", m.preMultiply(identity), m,
                entryTolerance);
        assertClose("identity multiply", identity.preMultiply(mInv), mInv,
                entryTolerance);
        try {
            m.preMultiply(createSparseMatrix(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    public void testGetVectors() {
        RealMatrix m = createSparseMatrix(testData);
        assertClose("get row", m.getRow(0), testDataRow1, entryTolerance);
        assertClose("get col", m.getColumn(2), testDataCol3, entryTolerance);
        try {
            m.getRow(10);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // ignored
        }
        try {
            m.getColumn(-1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // ignored
        }
    }

    public void testGetEntry() {
        RealMatrix m = createSparseMatrix(testData);
        assertEquals("get entry", m.getEntry(0, 1), 2d, entryTolerance);
        try {
            m.getEntry(10, 4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    /** test examples in user guide */
    public void testExamples() {
        // Create a real matrix with two rows and three columns
        double[][] matrixData = { { 1d, 2d, 3d }, { 2d, 5d, 3d } };
        RealMatrix m = createSparseMatrix(matrixData);
        // One more with three rows, two columns
        double[][] matrixData2 = { { 1d, 2d }, { 2d, 5d }, { 1d, 7d } };
        RealMatrix n = createSparseMatrix(matrixData2);
        // Now multiply m by n
        RealMatrix p = m.multiply(n);
        assertEquals(2, p.getRowDimension());
        assertEquals(2, p.getColumnDimension());
        // Invert p
        RealMatrix pInverse = new LUDecompositionImpl(p).getSolver().getInverse();
        assertEquals(2, pInverse.getRowDimension());
        assertEquals(2, pInverse.getColumnDimension());

        // Solve example
        double[][] coefficientsData = { { 2, 3, -2 }, { -1, 7, 6 },
                { 4, -3, -5 } };
        RealMatrix coefficients = createSparseMatrix(coefficientsData);
        double[] constants = { 1, -2, 1 };
        double[] solution = new LUDecompositionImpl(coefficients).getSolver().solve(constants);
        assertEquals(2 * solution[0] + 3 * solution[1] - 2 * solution[2],
                constants[0], 1E-12);
        assertEquals(-1 * solution[0] + 7 * solution[1] + 6 * solution[2],
                constants[1], 1E-12);
        assertEquals(4 * solution[0] - 3 * solution[1] - 5 * solution[2],
                constants[2], 1E-12);

    }

    // test submatrix accessors
    public void testSubMatrix() {
        RealMatrix m = createSparseMatrix(subTestData);
        RealMatrix mRows23Cols00 = createSparseMatrix(subRows23Cols00);
        RealMatrix mRows00Cols33 = createSparseMatrix(subRows00Cols33);
        RealMatrix mRows01Cols23 = createSparseMatrix(subRows01Cols23);
        RealMatrix mRows02Cols13 = createSparseMatrix(subRows02Cols13);
        RealMatrix mRows03Cols12 = createSparseMatrix(subRows03Cols12);
        RealMatrix mRows03Cols123 = createSparseMatrix(subRows03Cols123);
        RealMatrix mRows20Cols123 = createSparseMatrix(subRows20Cols123);
        RealMatrix mRows31Cols31 = createSparseMatrix(subRows31Cols31);
        assertEquals("Rows23Cols00", mRows23Cols00, m.getSubMatrix(2, 3, 0, 0));
        assertEquals("Rows00Cols33", mRows00Cols33, m.getSubMatrix(0, 0, 3, 3));
        assertEquals("Rows01Cols23", mRows01Cols23, m.getSubMatrix(0, 1, 2, 3));
        assertEquals("Rows02Cols13", mRows02Cols13,
            m.getSubMatrix(new int[] { 0, 2 }, new int[] { 1, 3 }));
        assertEquals("Rows03Cols12", mRows03Cols12,
            m.getSubMatrix(new int[] { 0, 3 }, new int[] { 1, 2 }));
        assertEquals("Rows03Cols123", mRows03Cols123,
            m.getSubMatrix(new int[] { 0, 3 }, new int[] { 1, 2, 3 }));
        assertEquals("Rows20Cols123", mRows20Cols123,
            m.getSubMatrix(new int[] { 2, 0 }, new int[] { 1, 2, 3 }));
        assertEquals("Rows31Cols31", mRows31Cols31,
            m.getSubMatrix(new int[] { 3, 1 }, new int[] { 3, 1 }));
        assertEquals("Rows31Cols31", mRows31Cols31,
            m.getSubMatrix(new int[] { 3, 1 }, new int[] { 3, 1 }));

        try {
            m.getSubMatrix(1, 0, 2, 4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(-1, 1, 2, 2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1, 0, 2, 2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1, 0, 2, 4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {}, new int[] { 0 });
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] { 0 }, new int[] { 4 });
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    public void testGetRowMatrix() {
        RealMatrix m = createSparseMatrix(subTestData);
        RealMatrix mRow0 = createSparseMatrix(subRow0);
        RealMatrix mRow3 = createSparseMatrix(subRow3);
        assertEquals("Row0", mRow0, m.getRowMatrix(0));
        assertEquals("Row3", mRow3, m.getRowMatrix(3));
        try {
            m.getRowMatrix(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getRowMatrix(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    public void testGetColumnMatrix() {
        RealMatrix m = createSparseMatrix(subTestData);
        RealMatrix mColumn1 = createSparseMatrix(subColumn1);
        RealMatrix mColumn3 = createSparseMatrix(subColumn3);
        assertEquals("Column1", mColumn1, m.getColumnMatrix(1));
        assertEquals("Column3", mColumn3, m.getColumnMatrix(3));
        try {
            m.getColumnMatrix(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getColumnMatrix(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    public void testGetRowVector() {
        RealMatrix m = createSparseMatrix(subTestData);
        RealVector mRow0 = new ArrayRealVector(subRow0[0]);
        RealVector mRow3 = new ArrayRealVector(subRow3[0]);
        assertEquals("Row0", mRow0, m.getRowVector(0));
        assertEquals("Row3", mRow3, m.getRowVector(3));
        try {
            m.getRowVector(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getRowVector(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    public void testGetColumnVector() {
        RealMatrix m = createSparseMatrix(subTestData);
        RealVector mColumn1 = columnToVector(subColumn1);
        RealVector mColumn3 = columnToVector(subColumn3);
        assertEquals("Column1", mColumn1, m.getColumnVector(1));
        assertEquals("Column3", mColumn3, m.getColumnVector(3));
        try {
            m.getColumnVector(-1);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getColumnVector(4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    private RealVector columnToVector(double[][] column) {
        double[] data = new double[column.length];
        for (int i = 0; i < data.length; ++i) {
            data[i] = column[i][0];
        }
        return new ArrayRealVector(data, false);
    }

    public void testEqualsAndHashCode() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        OpenMapRealMatrix m1 = m.copy();
        OpenMapRealMatrix mt = (OpenMapRealMatrix) m.transpose();
        assertTrue(m.hashCode() != mt.hashCode());
        assertEquals(m.hashCode(), m1.hashCode());
        assertEquals(m, m);
        assertEquals(m, m1);
        assertFalse(m.equals(null));
        assertFalse(m.equals(mt));
        assertFalse(m.equals(createSparseMatrix(bigSingular)));
    }

    public void testToString() {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        assertEquals("OpenMapRealMatrix{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}",
            m.toString());
        m = new OpenMapRealMatrix(1, 1);
        assertEquals("OpenMapRealMatrix{{0.0}}", m.toString());
    }

    public void testSetSubMatrix() throws Exception {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        m.setSubMatrix(detData2, 1, 1);
        RealMatrix expected = createSparseMatrix(new double[][] {
                { 1.0, 2.0, 3.0 }, { 2.0, 1.0, 3.0 }, { 1.0, 2.0, 4.0 } });
        assertEquals(expected, m);

        m.setSubMatrix(detData2, 0, 0);
        expected = createSparseMatrix(new double[][] {
                { 1.0, 3.0, 3.0 }, { 2.0, 4.0, 3.0 }, { 1.0, 2.0, 4.0 } });
        assertEquals(expected, m);

        m.setSubMatrix(testDataPlus2, 0, 0);
        expected = createSparseMatrix(new double[][] {
                { 3.0, 4.0, 5.0 }, { 4.0, 7.0, 5.0 }, { 3.0, 2.0, 10.0 } });
        assertEquals(expected, m);

        // javadoc example
        OpenMapRealMatrix matrix =
            createSparseMatrix(new double[][] {
        { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 0, 1, 2 } });
        matrix.setSubMatrix(new double[][] { { 3, 4 }, { 5, 6 } }, 1, 1);
        expected = createSparseMatrix(new double[][] {
                { 1, 2, 3, 4 }, { 5, 3, 4, 8 }, { 9, 5, 6, 2 } });
        assertEquals(expected, matrix);

        // dimension overflow
        try {
            m.setSubMatrix(testData, 1, 1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        // dimension underflow
        try {
            m.setSubMatrix(testData, -1, 1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }
        try {
            m.setSubMatrix(testData, 1, -1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }

        // null
        try {
            m.setSubMatrix(null, 1, 1);
            fail("expecting NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new OpenMapRealMatrix(0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // ragged
        try {
            m.setSubMatrix(new double[][] { { 1 }, { 2, 3 } }, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // empty
        try {
            m.setSubMatrix(new double[][] { {} }, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

    }

    public void testSerial()  {
        OpenMapRealMatrix m = createSparseMatrix(testData);
        assertEquals(m,TestUtils.serializeAndRecover(m));
    }

    // --------------- -----------------Protected methods

    /** verifies that two matrices are close (1-norm) */
    protected void assertClose(String msg, RealMatrix m, RealMatrix n,
            double tolerance) {
        assertTrue(msg, m.subtract(n).getNorm() < tolerance);
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " + i + " elements differ", m[i], n[i],
                    tolerance);
        }
    }

    private OpenMapRealMatrix createSparseMatrix(double[][] data) {
        OpenMapRealMatrix matrix = new OpenMapRealMatrix(data.length, data[0].length);
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                matrix.setEntry(row, col, data[row][col]);
            }
        }
        return matrix;
    }


}
