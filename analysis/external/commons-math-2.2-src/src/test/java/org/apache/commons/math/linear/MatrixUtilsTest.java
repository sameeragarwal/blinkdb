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

import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionConversionException;
import org.apache.commons.math.fraction.FractionField;

/**
 * Test cases for the {@link MatrixUtils} class.
 *
 * @version $Revision: 1003899 $ $Date: 2010-10-03 00:06:55 +0200 (dim. 03 oct. 2010) $
 */

public final class MatrixUtilsTest extends TestCase {

    protected double[][] testData = { {1d,2d,3d}, {2d,5d,3d}, {1d,0d,8d} };
    protected double[][] nullMatrix = null;
    protected double[] row = {1,2,3};
    protected BigDecimal[] bigRow =
        {new BigDecimal(1),new BigDecimal(2),new BigDecimal(3)};
    protected String[] stringRow = {"1", "2", "3"};
    protected Fraction[] fractionRow =
        {new Fraction(1),new Fraction(2),new Fraction(3)};
    protected double[][] rowMatrix = {{1,2,3}};
    protected BigDecimal[][] bigRowMatrix =
        {{new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)}};
    protected String[][] stringRowMatrix = {{"1", "2", "3"}};
    protected Fraction[][] fractionRowMatrix =
        {{new Fraction(1), new Fraction(2), new Fraction(3)}};
    protected double[] col = {0,4,6};
    protected BigDecimal[] bigCol =
        {new BigDecimal(0),new BigDecimal(4),new BigDecimal(6)};
    protected String[] stringCol = {"0","4","6"};
    protected Fraction[] fractionCol =
        {new Fraction(0),new Fraction(4),new Fraction(6)};
    protected double[] nullDoubleArray = null;
    protected double[][] colMatrix = {{0},{4},{6}};
    protected BigDecimal[][] bigColMatrix =
        {{new BigDecimal(0)},{new BigDecimal(4)},{new BigDecimal(6)}};
    protected String[][] stringColMatrix = {{"0"}, {"4"}, {"6"}};
    protected Fraction[][] fractionColMatrix =
        {{new Fraction(0)},{new Fraction(4)},{new Fraction(6)}};

    public MatrixUtilsTest(String name) {
        super(name);
    }


    public void testCreateRealMatrix() {
        assertEquals(new BlockRealMatrix(testData),
                MatrixUtils.createRealMatrix(testData));
        try {
            MatrixUtils.createRealMatrix(new double[][] {{1}, {1,2}});  // ragged
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRealMatrix(new double[][] {{}, {}});  // no columns
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testcreateFieldMatrix() {
        assertEquals(new Array2DRowFieldMatrix<Fraction>(asFraction(testData)),
                     MatrixUtils.createFieldMatrix(asFraction(testData)));
        assertEquals(new Array2DRowFieldMatrix<Fraction>(fractionColMatrix),
                     MatrixUtils.createFieldMatrix(fractionColMatrix));
        try {
            MatrixUtils.createFieldMatrix(asFraction(new double[][] {{1}, {1,2}}));  // ragged
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createFieldMatrix(asFraction(new double[][] {{}, {}}));  // no columns
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createFieldMatrix((Fraction[][])null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Deprecated
    public void testCreateBigMatrix() {
        assertEquals(new BigMatrixImpl(testData),
                MatrixUtils.createBigMatrix(testData));
        assertEquals(new BigMatrixImpl(BigMatrixImplTest.asBigDecimal(testData), true),
                MatrixUtils.createBigMatrix(BigMatrixImplTest.asBigDecimal(testData), false));
        assertEquals(new BigMatrixImpl(BigMatrixImplTest.asBigDecimal(testData), false),
                MatrixUtils.createBigMatrix(BigMatrixImplTest.asBigDecimal(testData), true));
        assertEquals(new BigMatrixImpl(bigColMatrix),
                MatrixUtils.createBigMatrix(bigColMatrix));
        assertEquals(new BigMatrixImpl(stringColMatrix),
                MatrixUtils.createBigMatrix(stringColMatrix));
        try {
            MatrixUtils.createBigMatrix(new double[][] {{1}, {1,2}});  // ragged
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createBigMatrix(new double[][] {{}, {}});  // no columns
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createBigMatrix(nullMatrix);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testCreateRowRealMatrix() {
        assertEquals(MatrixUtils.createRowRealMatrix(row),
                     new BlockRealMatrix(rowMatrix));
        try {
            MatrixUtils.createRowRealMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testCreateRowFieldMatrix() {
        assertEquals(MatrixUtils.createRowFieldMatrix(asFraction(row)),
                     new Array2DRowFieldMatrix<Fraction>(asFraction(rowMatrix)));
        assertEquals(MatrixUtils.createRowFieldMatrix(fractionRow),
                     new Array2DRowFieldMatrix<Fraction>(fractionRowMatrix));
        try {
            MatrixUtils.createRowFieldMatrix(new Fraction[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowFieldMatrix((Fraction[]) null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Deprecated
    public void testCreateRowBigMatrix() {
        assertEquals(MatrixUtils.createRowBigMatrix(row),
                new BigMatrixImpl(rowMatrix));
        assertEquals(MatrixUtils.createRowBigMatrix(bigRow),
                new BigMatrixImpl(bigRowMatrix));
        assertEquals(MatrixUtils.createRowBigMatrix(stringRow),
                new BigMatrixImpl(stringRowMatrix));
        try {
            MatrixUtils.createRowBigMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createRowBigMatrix(nullDoubleArray);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testCreateColumnRealMatrix() {
        assertEquals(MatrixUtils.createColumnRealMatrix(col),
                     new BlockRealMatrix(colMatrix));
        try {
            MatrixUtils.createColumnRealMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnRealMatrix(null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testCreateColumnFieldMatrix() {
        assertEquals(MatrixUtils.createColumnFieldMatrix(asFraction(col)),
                     new Array2DRowFieldMatrix<Fraction>(asFraction(colMatrix)));
        assertEquals(MatrixUtils.createColumnFieldMatrix(fractionCol),
                     new Array2DRowFieldMatrix<Fraction>(fractionColMatrix));

        try {
            MatrixUtils.createColumnFieldMatrix(new Fraction[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnFieldMatrix((Fraction[]) null);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Deprecated
    public void testCreateColumnBigMatrix() {
        assertEquals(MatrixUtils.createColumnBigMatrix(col),
                new BigMatrixImpl(colMatrix));
        assertEquals(MatrixUtils.createColumnBigMatrix(bigCol),
                new BigMatrixImpl(bigColMatrix));
        assertEquals(MatrixUtils.createColumnBigMatrix(stringCol),
                new BigMatrixImpl(stringColMatrix));

        try {
            MatrixUtils.createColumnBigMatrix(new double[] {});  // empty
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            MatrixUtils.createColumnBigMatrix(nullDoubleArray);  // null
            fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityMatrix(RealMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    assertEquals(m.getEntry(i, j), 1d, 0);
                } else {
                    assertEquals(m.getEntry(i, j), 0d, 0);
                }
            }
        }
    }

    public void testCreateIdentityMatrix() {
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(3));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(2));
        checkIdentityMatrix(MatrixUtils.createRealIdentityMatrix(1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Verifies that the matrix is an identity matrix
     */
    protected void checkIdentityFieldMatrix(FieldMatrix<Fraction> m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    assertEquals(m.getEntry(i, j), Fraction.ONE);
                } else {
                    assertEquals(m.getEntry(i, j), Fraction.ZERO);
                }
            }
        }
    }

    public void testcreateFieldIdentityMatrix() {
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 3));
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 2));
        checkIdentityFieldMatrix(MatrixUtils.createFieldIdentityMatrix(FractionField.getInstance(), 1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testBigFractionConverter() {
        BigFraction[][] bfData = {
                { new BigFraction(1), new BigFraction(2), new BigFraction(3) },
                { new BigFraction(2), new BigFraction(5), new BigFraction(3) },
                { new BigFraction(1), new BigFraction(0), new BigFraction(8) }
        };
        FieldMatrix<BigFraction> m = new Array2DRowFieldMatrix<BigFraction>(bfData, false);
        RealMatrix converted = MatrixUtils.bigFractionMatrixToRealMatrix(m);
        RealMatrix reference = new Array2DRowRealMatrix(testData, false);
        assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
    }

    public void testFractionConverter() {
        Fraction[][] fData = {
                { new Fraction(1), new Fraction(2), new Fraction(3) },
                { new Fraction(2), new Fraction(5), new Fraction(3) },
                { new Fraction(1), new Fraction(0), new Fraction(8) }
        };
        FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<Fraction>(fData, false);
        RealMatrix converted = MatrixUtils.fractionMatrixToRealMatrix(m);
        RealMatrix reference = new Array2DRowRealMatrix(testData, false);
        assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
    }

    public static final Fraction[][] asFraction(double[][] data) {
        Fraction d[][] = new Fraction[data.length][];
        try {
            for (int i = 0; i < data.length; ++i) {
                double[] dataI = data[i];
                Fraction[] dI  = new Fraction[dataI.length];
                for (int j = 0; j < dataI.length; ++j) {
                    dI[j] = new Fraction(dataI[j]);
                }
                d[i] = dI;
            }
        } catch (FractionConversionException fce) {
            fail(fce.getMessage());
        }
        return d;
    }

    public static final Fraction[] asFraction(double[] data) {
        Fraction d[] = new Fraction[data.length];
        try {
            for (int i = 0; i < data.length; ++i) {
                d[i] = new Fraction(data[i]);
            }
        } catch (FractionConversionException fce) {
            fail(fce.getMessage());
        }
        return d;
    }

    /**
     * Verifies that the matrix is an identity matrix
     */
    @Deprecated
    protected void checkIdentityBigMatrix(BigMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j =0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    assertEquals(m.getEntry(i, j), BigMatrixImpl.ONE);
                } else {
                    assertEquals(m.getEntry(i, j), BigMatrixImpl.ZERO);
                }
            }
        }
    }

    @Deprecated
    public void testCreateBigIdentityMatrix() {
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(3));
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(2));
        checkIdentityBigMatrix(MatrixUtils.createBigIdentityMatrix(1));
        try {
            MatrixUtils.createRealIdentityMatrix(0);
            fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

}

