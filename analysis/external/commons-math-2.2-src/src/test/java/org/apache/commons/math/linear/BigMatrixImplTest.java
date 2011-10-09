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

import java.math.BigDecimal;

import junit.framework.TestCase;


/**
 * Test cases for the {@link BigMatrixImpl} class.
 *
 * @version $Revision: 902201 $ $Date: 2010-01-22 19:18:16 +0100 (ven. 22 janv. 2010) $
 */
@Deprecated
public final class BigMatrixImplTest extends TestCase {

    // Test data for String constructors
    protected  String[][] testDataString = { {"1","2","3"}, {"2","5","3"}, {"1","0","8"} };

    // 3 x 3 identity matrix
    protected double[][] id = { {1d,0d,0d}, {0d,1d,0d}, {0d,0d,1d} };

    // Test data for group operations
    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[][] testDataLU = {{2d, 5d, 3d}, {.5d, -2.5d, 6.5d}, {0.5d, 0.2d, .2d}};
    protected double[][] testDataPlus2 = { {3d,4d,5d}, {4d,7d,5d}, {3d,2d,10d} };
    protected double[][] testDataMinus = { {-1d,-2d,-3d}, {-2d,-5d,-3d},
            {-1d,0d,-8d} };
    protected double[] testDataRow1 = {1d,2d,3d};
    protected double[] testDataCol3 = {3d,3d,8d};
    protected double[][] testDataInv =
        { {-40d,16d,9d}, {13d,-5d,-3d}, {5d,-2d,-1d} };
    protected double[] preMultTest = {8,12,33};
    protected double[][] testData2 ={ {1d,2d,3d}, {2d,5d,3d}};
    protected double[][] testData2T = { {1d,2d}, {2d,5d}, {3d,3d}};
    protected double[][] testDataPlusInv =
        { {-39d,18d,12d}, {15d,0d,0d}, {6d,-2d,7d} };

    // lu decomposition tests
    protected double[][] luData = { {2d,3d,3d}, {0d,5d,7d}, {6d,9d,8d} };
    protected double[][] luDataLUDecomposition = { {6d,9d,8d}, {0d,5d,7d},
            {0.33333333333333,0d,0.33333333333333} };

    // singular matrices
    protected double[][] singular = { {2d,3d}, {2d,3d} };
    protected double[][] bigSingular = {{1d,2d,3d,4d}, {2d,5d,3d,4d},
            {7d,3d,256d,1930d}, {3d,7d,6d,8d}}; // 4th row = 1st + 2nd
    protected double[][] detData = { {1d,2d,3d}, {4d,5d,6d}, {7d,8d,10d} };
    protected double[][] detData2 = { {1d, 3d}, {2d, 4d}};

    // vectors
    protected double[] testVector = {1,2,3};
    protected double[] testVector2 = {1,2,3,4};

    // submatrix accessor tests
    protected double[][] subTestData = {{1, 2, 3, 4}, {1.5, 2.5, 3.5, 4.5},
            {2, 4, 6, 8}, {4, 5, 6, 7}};
    // array selections
    protected double[][] subRows02Cols13 = { {2, 4}, {4, 8}};
    protected double[][] subRows03Cols12 = { {2, 3}, {5, 6}};
    protected double[][] subRows03Cols123 = { {2, 3, 4} , {5, 6, 7}};
    // effective permutations
    protected double[][] subRows20Cols123 = { {4, 6, 8} , {2, 3, 4}};
    protected double[][] subRows31Cols31 = {{7, 5}, {4.5, 2.5}};
    // contiguous ranges
    protected double[][] subRows01Cols23 = {{3,4} , {3.5, 4.5}};
    protected double[][] subRows23Cols00 = {{2} , {4}};
    protected double[][] subRows00Cols33 = {{4}};
    // row matrices
    protected double[][] subRow0 = {{1,2,3,4}};
    protected double[][] subRow3 = {{4,5,6,7}};
    // column matrices
    protected double[][] subColumn1 = {{2}, {2.5}, {4}, {5}};
    protected double[][] subColumn3 = {{4}, {4.5}, {8}, {7}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    public BigMatrixImplTest(String name) {
        super(name);
    }

    public static final double[] asDouble(BigDecimal[] data) {
        double d[] = new double[data.length];
        for (int i=0;i<d.length;i++) {
            d[i] = data[i].doubleValue();
        }
        return d;
    }

    public static final double[][] asDouble(BigDecimal[][] data) {
        double d[][] = new double[data.length][data[0].length];
        for (int i=0;i<d.length;i++) {
            for (int j=0;j<d[i].length;j++)
            d[i][j] = data[i][j].doubleValue();
        }
        return d;
    }

    public static final BigDecimal[] asBigDecimal(double [] data) {
        BigDecimal d[] = new BigDecimal[data.length];
        for (int i=0;i<d.length;i++) {
            d[i] = new BigDecimal(data[i]);
        }
        return d;
    }

    public static final BigDecimal[][] asBigDecimal(double [][] data) {
        BigDecimal d[][] = new BigDecimal[data.length][data[0].length];
        for (int i=0;i<d.length;i++) {
            for (int j=0;j<data[i].length;j++) {
                d[i][j] = new BigDecimal(data[i][j]);
            }
        }
        return d;
    }

    /** test dimensions */
    public void testDimensions() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl m2 = new BigMatrixImpl(testData2);
        assertEquals("testData row dimension",3,m.getRowDimension());
        assertEquals("testData column dimension",3,m.getColumnDimension());
        assertTrue("testData is square",m.isSquare());
        assertEquals("testData2 row dimension",m2.getRowDimension(),2);
        assertEquals("testData2 column dimension",m2.getColumnDimension(),3);
        assertTrue("testData2 is not square",!m2.isSquare());
    }

    /** test copy functions */
    public void testCopyFunctions() {
        BigMatrixImpl m1 = new BigMatrixImpl(testData);
        BigMatrixImpl m2 = new BigMatrixImpl(m1.getData());
        assertEquals(m2,m1);
        BigMatrixImpl m3 = new BigMatrixImpl(testData);
        BigMatrixImpl m4 = new BigMatrixImpl(m3.getData(), false);
        assertEquals(m4,m3);
    }

    /** test constructors */
    public void testConstructors() {
        BigMatrix m1 = new BigMatrixImpl(testData);
        BigMatrix m2 = new BigMatrixImpl(testDataString);
        BigMatrix m3 = new BigMatrixImpl(asBigDecimal(testData));
        BigMatrix m4 = new BigMatrixImpl(asBigDecimal(testData), true);
        BigMatrix m5 = new BigMatrixImpl(asBigDecimal(testData), false);
        assertClose("double, string", m1, m2, Double.MIN_VALUE);
        assertClose("double, BigDecimal", m1, m3, Double.MIN_VALUE);
        assertClose("string, BigDecimal", m2, m3, Double.MIN_VALUE);
        assertClose("double, BigDecimal/true", m1, m4, Double.MIN_VALUE);
        assertClose("double, BigDecimal/false", m1, m5, Double.MIN_VALUE);
        try {
            new BigMatrixImpl(new String[][] {{"0", "hello", "1"}});
            fail("Expecting NumberFormatException");
        } catch (NumberFormatException ex) {
            // expected
        }
        try {
            new BigMatrixImpl(new String[][] {});
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new BigMatrixImpl(new String[][] {{},{}});
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new BigMatrixImpl(new String[][] {{"a", "b"},{"c"}});
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            new BigMatrixImpl(0, 1);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new BigMatrixImpl(1, 0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /** test add */
    public void testAdd() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl mInv = new BigMatrixImpl(testDataInv);
        BigMatrix mPlusMInv = m.add(mInv);
        double[][] sumEntries = asDouble(mPlusMInv.getData());
        for (int row = 0; row < m.getRowDimension(); row++) {
            for (int col = 0; col < m.getColumnDimension(); col++) {
                assertEquals("sum entry entry",
                    testDataPlusInv[row][col],sumEntries[row][col],
                        entryTolerance);
            }
        }
    }

    /** test add failure */
    public void testAddFail() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl m2 = new BigMatrixImpl(testData2);
        try {
            m.add(m2);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test norm */
    public void testNorm() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl m2 = new BigMatrixImpl(testData2);
        assertEquals("testData norm",14d,m.getNorm().doubleValue(),entryTolerance);
        assertEquals("testData2 norm",7d,m2.getNorm().doubleValue(),entryTolerance);
    }

     /** test m-n = m + -n */
    public void testPlusMinus() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl m2 = new BigMatrixImpl(testDataInv);
        assertClose("m-n = m + -n",m.subtract(m2),
            m2.scalarMultiply(new BigDecimal(-1d)).add(m),entryTolerance);
        try {
            m.subtract(new BigMatrixImpl(testData2));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test multiply */
     public void testMultiply() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl mInv = new BigMatrixImpl(testDataInv);
        BigMatrixImpl identity = new BigMatrixImpl(id);
        BigMatrixImpl m2 = new BigMatrixImpl(testData2);
        assertClose("inverse multiply",m.multiply(mInv),
            identity,entryTolerance);
        assertClose("inverse multiply",mInv.multiply(m),
            identity,entryTolerance);
        assertClose("identity multiply",m.multiply(identity),
            m,entryTolerance);
        assertClose("identity multiply",identity.multiply(mInv),
            mInv,entryTolerance);
        assertClose("identity multiply",m2.multiply(identity),
            m2,entryTolerance);
        try {
            m.multiply(new BigMatrixImpl(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    //Additional Test for BigMatrixImplTest.testMultiply

    private double[][] d3 = new double[][] {{1,2,3,4},{5,6,7,8}};
    private double[][] d4 = new double[][] {{1},{2},{3},{4}};
    private double[][] d5 = new double[][] {{30},{70}};

    public void testMultiply2() {
       BigMatrix m3 = new BigMatrixImpl(d3);
       BigMatrix m4 = new BigMatrixImpl(d4);
       BigMatrix m5 = new BigMatrixImpl(d5);
       assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
   }

    /** test isSingular */
    public void testIsSingular() {
        BigMatrixImpl m = new BigMatrixImpl(singular);
        assertTrue("singular",m.isSingular());
        m = new BigMatrixImpl(bigSingular);
        assertTrue("big singular",m.isSingular());
        m = new BigMatrixImpl(id);
        assertTrue("identity nonsingular",!m.isSingular());
        m = new BigMatrixImpl(testData);
        assertTrue("testData nonsingular",!m.isSingular());
    }

    /** test inverse */
    public void testInverse() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrix mInv = new BigMatrixImpl(testDataInv);
        assertClose("inverse",mInv,m.inverse(),normTolerance);
        assertClose("inverse^2",m,m.inverse().inverse(),10E-12);

        // Not square
        m = new BigMatrixImpl(testData2);
        try {
            m.inverse();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }

        // Singular
        m = new BigMatrixImpl(singular);
        try {
            m.inverse();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
    }

    /** test solve */
    public void testSolve() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrix mInv = new BigMatrixImpl(testDataInv);
        // being a bit slothful here -- actually testing that X = A^-1 * B
        assertClose("inverse-operate",
                    asDouble(mInv.operate(asBigDecimal(testVector))),
                    asDouble(m.solve(asBigDecimal(testVector))),
                    normTolerance);
        try {
            asDouble(m.solve(asBigDecimal(testVector2)));
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        BigMatrix bs = new BigMatrixImpl(bigSingular);
        try {
            bs.solve(bs);
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // ignored
        }
        try {
            m.solve(bs);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            new BigMatrixImpl(testData2).solve(bs);
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
        try {
            (new BigMatrixImpl(testData2)).luDecompose();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // ignored
        }
    }

    /** test determinant */
    public void testDeterminant() {
        BigMatrix m = new BigMatrixImpl(bigSingular);
        assertEquals("singular determinant",0,m.getDeterminant().doubleValue(),0);
        m = new BigMatrixImpl(detData);
        assertEquals("nonsingular test",-3d,m.getDeterminant().doubleValue(),normTolerance);

        // Examples verified against R (version 1.8.1, Red Hat Linux 9)
        m = new BigMatrixImpl(detData2);
        assertEquals("nonsingular R test 1",-2d,m.getDeterminant().doubleValue(),normTolerance);
        m = new BigMatrixImpl(testData);
        assertEquals("nonsingular  R test 2",-1d,m.getDeterminant().doubleValue(),normTolerance);

        try {
            double d = new BigMatrixImpl(testData2).getDeterminant().doubleValue();
            fail("Expecting InvalidMatrixException, got " + d);
        } catch (InvalidMatrixException ex) {
            // ignored
        }
    }

    /** test trace */
    public void testTrace() {
        BigMatrix m = new BigMatrixImpl(id);
        assertEquals("identity trace",3d,m.getTrace().doubleValue(),entryTolerance);
        m = new BigMatrixImpl(testData2);
        try {
            double t = m.getTrace().doubleValue();
            fail("Expecting NonSquareMatrixException, got " + t);
        } catch (NonSquareMatrixException ex) {
            // ignored
        }
    }

    /** test sclarAdd */
    public void testScalarAdd() {
        BigMatrix m = new BigMatrixImpl(testData);
        assertClose("scalar add",new BigMatrixImpl(testDataPlus2),
            m.scalarAdd(new BigDecimal(2d)),entryTolerance);
    }

    /** test operate */
    public void testOperate() {
        BigMatrix m = new BigMatrixImpl(id);
        double[] x = asDouble(m.operate(asBigDecimal(testVector)));
        assertClose("identity operate",testVector,x,entryTolerance);
        m = new BigMatrixImpl(bigSingular);
        try {
            asDouble(m.operate(asBigDecimal(testVector)));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    /** test issue MATH-209 */
    public void testMath209() {
        BigMatrix a = new BigMatrixImpl(new BigDecimal[][] {
                { new BigDecimal(1), new BigDecimal(2) },
                { new BigDecimal(3), new BigDecimal(4) },
                { new BigDecimal(5), new BigDecimal(6) }
        }, false);
        BigDecimal[] b = a.operate(new BigDecimal[] { new BigDecimal(1), new BigDecimal(1) });
        assertEquals(a.getRowDimension(), b.length);
        assertEquals( 3.0, b[0].doubleValue(), 1.0e-12);
        assertEquals( 7.0, b[1].doubleValue(), 1.0e-12);
        assertEquals(11.0, b[2].doubleValue(), 1.0e-12);
    }

    /** test transpose */
    public void testTranspose() {
        BigMatrix m = new BigMatrixImpl(testData);
        assertClose("inverse-transpose",m.inverse().transpose(),
            m.transpose().inverse(),normTolerance);
        m = new BigMatrixImpl(testData2);
        BigMatrix mt = new BigMatrixImpl(testData2T);
        assertClose("transpose",mt,m.transpose(),normTolerance);
    }

    /** test preMultiply by vector */
    public void testPremultiplyVector() {
        BigMatrix m = new BigMatrixImpl(testData);
        assertClose("premultiply",asDouble(m.preMultiply(asBigDecimal(testVector))),preMultTest,normTolerance);
        m = new BigMatrixImpl(bigSingular);
        try {
            m.preMultiply(asBigDecimal(testVector));
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    public void testPremultiply() {
        BigMatrix m3 = new BigMatrixImpl(d3);
        BigMatrix m4 = new BigMatrixImpl(d4);
        BigMatrix m5 = new BigMatrixImpl(d5);
        assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);

        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl mInv = new BigMatrixImpl(testDataInv);
        BigMatrixImpl identity = new BigMatrixImpl(id);
        new BigMatrixImpl(testData2);
        assertClose("inverse multiply",m.preMultiply(mInv),
                identity,entryTolerance);
        assertClose("inverse multiply",mInv.preMultiply(m),
                identity,entryTolerance);
        assertClose("identity multiply",m.preMultiply(identity),
                m,entryTolerance);
        assertClose("identity multiply",identity.preMultiply(mInv),
                mInv,entryTolerance);
        try {
            m.preMultiply(new BigMatrixImpl(bigSingular));
            fail("Expecting illegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // ignored
        }
    }

    public void testGetVectors() {
        BigMatrix m = new BigMatrixImpl(testData);
        assertClose("get row",m.getRowAsDoubleArray(0),testDataRow1,entryTolerance);
        assertClose("get col",m.getColumnAsDoubleArray(2),testDataCol3,entryTolerance);
        try {
            m.getRowAsDoubleArray(10);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // ignored
        }
        try {
            m.getColumnAsDoubleArray(-1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // ignored
        }
    }

    public void testLUDecomposition() throws Exception {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrix lu = m.getLUMatrix();
        assertClose("LU decomposition", lu, new BigMatrixImpl(testDataLU), normTolerance);
        verifyDecomposition(m, lu);
        m = new BigMatrixImpl(luData);
        lu = m.getLUMatrix();
        assertClose("LU decomposition", lu, new BigMatrixImpl(luDataLUDecomposition), normTolerance);
        verifyDecomposition(m, lu);
        m = new BigMatrixImpl(testDataMinus);
        lu = m.getLUMatrix();
        verifyDecomposition(m, lu);
        m = new BigMatrixImpl(id);
        lu = m.getLUMatrix();
        verifyDecomposition(m, lu);
        try {
            m = new BigMatrixImpl(bigSingular); // singular
            lu = m.getLUMatrix();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
        try {
            m = new BigMatrixImpl(testData2);  // not square
            lu = m.getLUMatrix();
            fail("Expecting InvalidMatrixException");
        } catch (InvalidMatrixException ex) {
            // expected
        }
    }

   /**
    * test submatrix accessors
    */
    public void testSubMatrix() {
        BigMatrix m = new BigMatrixImpl(subTestData);
        BigMatrix mRows23Cols00 = new BigMatrixImpl(subRows23Cols00);
        BigMatrix mRows00Cols33 = new BigMatrixImpl(subRows00Cols33);
        BigMatrix mRows01Cols23 = new BigMatrixImpl(subRows01Cols23);
        BigMatrix mRows02Cols13 = new BigMatrixImpl(subRows02Cols13);
        BigMatrix mRows03Cols12 = new BigMatrixImpl(subRows03Cols12);
        BigMatrix mRows03Cols123 = new BigMatrixImpl(subRows03Cols123);
        BigMatrix mRows20Cols123 = new BigMatrixImpl(subRows20Cols123);
        BigMatrix mRows31Cols31 = new BigMatrixImpl(subRows31Cols31);
        assertEquals("Rows23Cols00", mRows23Cols00,
                m.getSubMatrix(2 , 3 , 0, 0));
        assertEquals("Rows00Cols33", mRows00Cols33,
                m.getSubMatrix(0 , 0 , 3, 3));
        assertEquals("Rows01Cols23", mRows01Cols23,
                m.getSubMatrix(0 , 1 , 2, 3));
        assertEquals("Rows02Cols13", mRows02Cols13,
                m.getSubMatrix(new int[] {0,2}, new int[] {1,3}));
        assertEquals("Rows03Cols12", mRows03Cols12,
                m.getSubMatrix(new int[] {0,3}, new int[] {1,2}));
        assertEquals("Rows03Cols123", mRows03Cols123,
                m.getSubMatrix(new int[] {0,3}, new int[] {1,2,3}));
        assertEquals("Rows20Cols123", mRows20Cols123,
                m.getSubMatrix(new int[] {2,0}, new int[] {1,2,3}));
        assertEquals("Rows31Cols31", mRows31Cols31,
                m.getSubMatrix(new int[] {3,1}, new int[] {3,1}));
        assertEquals("Rows31Cols31", mRows31Cols31,
                m.getSubMatrix(new int[] {3,1}, new int[] {3,1}));

        try {
            m.getSubMatrix(1,0,2,4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(-1,1,2,2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1,0,2,2);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(1,0,2,4);
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {}, new int[] {0});
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
        try {
            m.getSubMatrix(new int[] {0}, new int[] {4});
            fail("Expecting MatrixIndexException");
        } catch (MatrixIndexException ex) {
            // expected
        }
    }

    public void testGetColumnMatrix() {
        BigMatrix m = new BigMatrixImpl(subTestData);
        BigMatrix mColumn1 = new BigMatrixImpl(subColumn1);
        BigMatrix mColumn3 = new BigMatrixImpl(subColumn3);
        assertEquals("Column1", mColumn1,
                m.getColumnMatrix(1));
        assertEquals("Column3", mColumn3,
                m.getColumnMatrix(3));
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

    public void testGetRowMatrix() {
        BigMatrix m = new BigMatrixImpl(subTestData);
        BigMatrix mRow0 = new BigMatrixImpl(subRow0);
        BigMatrix mRow3 = new BigMatrixImpl(subRow3);
        assertEquals("Row0", mRow0,
                m.getRowMatrix(0));
        assertEquals("Row3", mRow3,
                m.getRowMatrix(3));
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

    public void testEqualsAndHashCode() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        BigMatrixImpl m1 = (BigMatrixImpl) m.copy();
        BigMatrixImpl mt = (BigMatrixImpl) m.transpose();
        assertTrue(m.hashCode() != mt.hashCode());
        assertEquals(m.hashCode(), m1.hashCode());
        assertEquals(m, m);
        assertEquals(m, m1);
        assertFalse(m.equals(null));
        assertFalse(m.equals(mt));
        assertFalse(m.equals(new BigMatrixImpl(bigSingular)));
        // Different scales make BigDecimals, so matrices unequal
        m = new BigMatrixImpl(new String[][] {{"2.0"}});
        m1 = new BigMatrixImpl(new String[][] {{"2.00"}});
        assertTrue(m.hashCode() != m1.hashCode());
        assertFalse(m.equals(m1));
    }

    public void testToString() {
        BigMatrixImpl m = new BigMatrixImpl(testData);
        assertEquals("BigMatrixImpl{{1,2,3},{2,5,3},{1,0,8}}",
                m.toString());
        m = new BigMatrixImpl();
        assertEquals("BigMatrixImpl{}",
                m.toString());
    }

    public void testSetSubMatrix() throws Exception {
        BigDecimal[][] detData3 =
            MatrixUtils.createBigMatrix(detData2).getData();
        BigMatrixImpl m = new BigMatrixImpl(testData);
        m.setSubMatrix(detData3,1,1);
        BigMatrix expected = MatrixUtils.createBigMatrix
            (new double[][] {{1.0,2.0,3.0},{2.0,1.0,3.0},{1.0,2.0,4.0}});
        assertEquals(expected, m);

        m.setSubMatrix(detData3,0,0);
        expected = MatrixUtils.createBigMatrix
            (new double[][] {{1.0,3.0,3.0},{2.0,4.0,3.0},{1.0,2.0,4.0}});
        assertEquals(expected, m);

        BigDecimal[][] testDataPlus3 =
            MatrixUtils.createBigMatrix(testDataPlus2).getData();
        m.setSubMatrix(testDataPlus3,0,0);
        expected = MatrixUtils.createBigMatrix
        (new double[][] {{3.0,4.0,5.0},{4.0,7.0,5.0},{3.0,2.0,10.0}});
        assertEquals(expected, m);

        // javadoc example
        BigMatrixImpl matrix = (BigMatrixImpl) MatrixUtils.createBigMatrix
            (new double[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 0, 1 , 2}});
        matrix.setSubMatrix(new BigDecimal[][] {{new BigDecimal(3),
            new BigDecimal(4)}, {new BigDecimal(5), new BigDecimal(6)}}, 1, 1);
        expected = MatrixUtils.createBigMatrix
            (new BigDecimal[][] {{new BigDecimal(1), new BigDecimal(2),
             new BigDecimal(3), new BigDecimal(4)}, {new BigDecimal(5),
             new BigDecimal(3), new BigDecimal(4), new BigDecimal(8)},
             {new BigDecimal(9), new BigDecimal(5) , new BigDecimal(6),
              new BigDecimal(2)}});
        assertEquals(expected, matrix);

        // dimension overflow
        try {
            m.setSubMatrix(matrix.getData(),1,1);
            fail("expecting MatrixIndexException");
        } catch (MatrixIndexException e) {
            // expected
        }

        // null
        try {
            m.setSubMatrix(null,1,1);
            fail("expecting NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }

        // ragged
        try {
            m.setSubMatrix(new BigDecimal[][] {{new BigDecimal(1)},
                    {new BigDecimal(2), new BigDecimal(3)}}, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // empty
        try {
            m.setSubMatrix(new BigDecimal[][] {{}}, 0, 0);
            fail("expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

    }

    //--------------- -----------------Protected methods

    /** verifies that two matrices are close (1-norm) */
    protected void assertClose(String msg, BigMatrix m, BigMatrix n,
        double tolerance) {
        assertTrue(msg,m.subtract(n).getNorm().doubleValue() < tolerance);
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
        double tolerance) {
        if (m.length != n.length) {
            fail("vectors not same length");
        }
        for (int i = 0; i < m.length; i++) {
            assertEquals(msg + " " +  i + " elements differ",
                m[i],n[i],tolerance);
        }
    }

    /** extracts the l  and u matrices from compact lu representation */
    protected void splitLU(BigMatrix lu, BigDecimal[][] lowerData, BigDecimal[][] upperData) throws InvalidMatrixException {
        if (!lu.isSquare() || lowerData.length != lowerData[0].length || upperData.length != upperData[0].length ||
                lowerData.length != upperData.length
                || lowerData.length != lu.getRowDimension()) {
            throw new InvalidMatrixException("incorrect dimensions");
        }
        int n = lu.getRowDimension();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j < i) {
                    lowerData[i][j] = lu.getEntry(i, j);
                    upperData[i][j] = new BigDecimal(0);
                } else if (i == j) {
                    lowerData[i][j] = new BigDecimal(1);
                    upperData[i][j] = lu.getEntry(i, j);
                } else {
                    lowerData[i][j] = new BigDecimal(0);
                    upperData[i][j] = lu.getEntry(i, j);
                }
            }
        }
    }

    /** Returns the result of applying the given row permutation to the matrix */
    protected BigMatrix permuteRows(BigMatrix matrix, int[] permutation) {
        if (!matrix.isSquare() || matrix.getRowDimension() != permutation.length) {
            throw new IllegalArgumentException("dimension mismatch");
        }
        int n = matrix.getRowDimension();
        int m = matrix.getColumnDimension();
        BigDecimal out[][] = new BigDecimal[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                out[i][j] = matrix.getEntry(permutation[i], j);
            }
        }
        return new BigMatrixImpl(out);
    }

    /** Extracts l and u matrices from lu and verifies that matrix = l times u modulo permutation */
    protected void verifyDecomposition(BigMatrix matrix, BigMatrix lu) throws Exception{
        int n = matrix.getRowDimension();
        BigDecimal[][] lowerData = new BigDecimal[n][n];
        BigDecimal[][] upperData = new BigDecimal[n][n];
        splitLU(lu, lowerData, upperData);
        BigMatrix lower =new BigMatrixImpl(lowerData);
        BigMatrix upper = new BigMatrixImpl(upperData);
        int[] permutation = ((BigMatrixImpl) matrix).getPermutation();
        BigMatrix permuted = permuteRows(matrix, permutation);
        assertClose("lu decomposition does not work", permuted,
                lower.multiply(upper), normTolerance);
    }

//    /** Useful for debugging */
//    private void dumpMatrix(BigMatrix m) {
//          for (int i = 0; i < m.getRowDimension(); i++) {
//              String os = "";
//              for (int j = 0; j < m.getColumnDimension(); j++) {
//                  os += m.getEntry(i, j) + " ";
//              }
//              System.out.println(os);
//          }
//    }

}

