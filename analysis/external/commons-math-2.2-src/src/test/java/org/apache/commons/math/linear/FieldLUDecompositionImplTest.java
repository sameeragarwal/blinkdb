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
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionField;

public class FieldLUDecompositionImplTest extends TestCase {
    private Fraction[][] testData = {
            { new Fraction(1), new Fraction(2), new Fraction(3)},
            { new Fraction(2), new Fraction(5), new Fraction(3)},
            { new Fraction(1), new Fraction(0), new Fraction(8)}
    };
    private Fraction[][] testDataMinus = {
            { new Fraction(-1), new Fraction(-2), new Fraction(-3)},
            { new Fraction(-2), new Fraction(-5), new Fraction(-3)},
            { new Fraction(-1),  new Fraction(0), new Fraction(-8)}
    };
    private Fraction[][] luData = {
            { new Fraction(2), new Fraction(3), new Fraction(3) },
            { new Fraction(2), new Fraction(3), new Fraction(7) },
            { new Fraction(6), new Fraction(6), new Fraction(8) }
    };

    // singular matrices
    private Fraction[][] singular = {
            { new Fraction(2), new Fraction(3) },
            { new Fraction(2), new Fraction(3) }
    };
    private Fraction[][] bigSingular = {
            { new Fraction(1), new Fraction(2),   new Fraction(3),    new Fraction(4) },
            { new Fraction(2), new Fraction(5),   new Fraction(3),    new Fraction(4) },
            { new Fraction(7), new Fraction(3), new Fraction(256), new Fraction(1930) },
            { new Fraction(3), new Fraction(7),   new Fraction(6),    new Fraction(8) }
    }; // 4th row = 1st + 2nd

    public FieldLUDecompositionImplTest(String name) {
        super(name);
    }

    /** test dimensions */
    public void testDimensions() {
        FieldMatrix<Fraction> matrix = new Array2DRowFieldMatrix<Fraction>(testData);
        FieldLUDecomposition<Fraction> LU = new FieldLUDecompositionImpl<Fraction>(matrix);
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
            new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                    { Fraction.ZERO, Fraction.ZERO },
                    { Fraction.ZERO, Fraction.ZERO },
                    { Fraction.ZERO, Fraction.ZERO }
            }));
            fail("Expected InvalidMatrixException");
        } catch (InvalidMatrixException ime) {
            // expected behavior
        }
    }

    /** test PA = LU */
    public void testPAEqualLU() {
        FieldMatrix<Fraction> matrix = new Array2DRowFieldMatrix<Fraction>(testData);
        FieldLUDecomposition<Fraction> lu = new FieldLUDecompositionImpl<Fraction>(matrix);
        FieldMatrix<Fraction> l = lu.getL();
        FieldMatrix<Fraction> u = lu.getU();
        FieldMatrix<Fraction> p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<Fraction>(testDataMinus);
        lu = new FieldLUDecompositionImpl<Fraction>(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(), 17, 17);
        for (int i = 0; i < matrix.getRowDimension(); ++i) {
            matrix.setEntry(i, i, Fraction.ONE);
        }
        lu = new FieldLUDecompositionImpl<Fraction>(matrix);
        l = lu.getL();
        u = lu.getU();
        p = lu.getP();
        TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));

        matrix = new Array2DRowFieldMatrix<Fraction>(singular);
        lu = new FieldLUDecompositionImpl<Fraction>(matrix);
        assertFalse(lu.getSolver().isNonSingular());
        assertNull(lu.getL());
        assertNull(lu.getU());
        assertNull(lu.getP());

        matrix = new Array2DRowFieldMatrix<Fraction>(bigSingular);
        lu = new FieldLUDecompositionImpl<Fraction>(matrix);
        assertFalse(lu.getSolver().isNonSingular());
        assertNull(lu.getL());
        assertNull(lu.getU());
        assertNull(lu.getP());

    }

    /** test that L is lower triangular with unit diagonal */
    public void testLLowerTriangular() {
        FieldMatrix<Fraction> matrix = new Array2DRowFieldMatrix<Fraction>(testData);
        FieldMatrix<Fraction> l = new FieldLUDecompositionImpl<Fraction>(matrix).getL();
        for (int i = 0; i < l.getRowDimension(); i++) {
            assertEquals(Fraction.ONE, l.getEntry(i, i));
            for (int j = i + 1; j < l.getColumnDimension(); j++) {
                assertEquals(Fraction.ZERO, l.getEntry(i, j));
            }
        }
    }

    /** test that U is upper triangular */
    public void testUUpperTriangular() {
        FieldMatrix<Fraction> matrix = new Array2DRowFieldMatrix<Fraction>(testData);
        FieldMatrix<Fraction> u = new FieldLUDecompositionImpl<Fraction>(matrix).getU();
        for (int i = 0; i < u.getRowDimension(); i++) {
            for (int j = 0; j < i; j++) {
                assertEquals(Fraction.ZERO, u.getEntry(i, j));
            }
        }
    }

    /** test that P is a permutation matrix */
    public void testPPermutation() {
        FieldMatrix<Fraction> matrix = new Array2DRowFieldMatrix<Fraction>(testData);
        FieldMatrix<Fraction> p   = new FieldLUDecompositionImpl<Fraction>(matrix).getP();

        FieldMatrix<Fraction> ppT = p.multiply(p.transpose());
        FieldMatrix<Fraction> id  =
            new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),
                                          p.getRowDimension(), p.getRowDimension());
        for (int i = 0; i < id.getRowDimension(); ++i) {
            id.setEntry(i, i, Fraction.ONE);
        }
        TestUtils.assertEquals(id, ppT);

        for (int i = 0; i < p.getRowDimension(); i++) {
            int zeroCount  = 0;
            int oneCount   = 0;
            int otherCount = 0;
            for (int j = 0; j < p.getColumnDimension(); j++) {
                final Fraction e = p.getEntry(i, j);
                if (e.equals(Fraction.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Fraction.ONE)) {
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
                final Fraction e = p.getEntry(i, j);
                if (e.equals(Fraction.ZERO)) {
                    ++zeroCount;
                } else if (e.equals(Fraction.ONE)) {
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
        FieldLUDecomposition<Fraction> lu =
            new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(testData));
        assertTrue(lu.getSolver().isNonSingular());
        lu = new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(singular));
        assertFalse(lu.getSolver().isNonSingular());
        lu = new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(bigSingular));
        assertFalse(lu.getSolver().isNonSingular());
    }

    /** test matrices values */
    public void testMatricesValues1() {
       FieldLUDecomposition<Fraction> lu =
            new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(testData));
        FieldMatrix<Fraction> lRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(1), new Fraction(0), new Fraction(0) },
                { new Fraction(2), new Fraction(1), new Fraction(0) },
                { new Fraction(1), new Fraction(-2), new Fraction(1) }
        });
        FieldMatrix<Fraction> uRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(1),  new Fraction(2), new Fraction(3) },
                { new Fraction(0), new Fraction(1), new Fraction(-3) },
                { new Fraction(0),  new Fraction(0), new Fraction(-1) }
        });
        FieldMatrix<Fraction> pRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(1), new Fraction(0), new Fraction(0) },
                { new Fraction(0), new Fraction(1), new Fraction(0) },
                { new Fraction(0), new Fraction(0), new Fraction(1) }
        });
        int[] pivotRef = { 0, 1, 2 };

        // check values against known references
        FieldMatrix<Fraction> l = lu.getL();
        TestUtils.assertEquals(lRef, l);
        FieldMatrix<Fraction> u = lu.getU();
        TestUtils.assertEquals(uRef, u);
        FieldMatrix<Fraction> p = lu.getP();
        TestUtils.assertEquals(pRef, p);
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
       FieldLUDecomposition<Fraction> lu =
            new FieldLUDecompositionImpl<Fraction>(new Array2DRowFieldMatrix<Fraction>(luData));
        FieldMatrix<Fraction> lRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(1), new Fraction(0), new Fraction(0) },
                { new Fraction(3), new Fraction(1), new Fraction(0) },
                { new Fraction(1), new Fraction(0), new Fraction(1) }
        });
        FieldMatrix<Fraction> uRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(2), new Fraction(3), new Fraction(3)    },
                { new Fraction(0), new Fraction(-3), new Fraction(-1)  },
                { new Fraction(0), new Fraction(0), new Fraction(4) }
        });
        FieldMatrix<Fraction> pRef = new Array2DRowFieldMatrix<Fraction>(new Fraction[][] {
                { new Fraction(1), new Fraction(0), new Fraction(0) },
                { new Fraction(0), new Fraction(0), new Fraction(1) },
                { new Fraction(0), new Fraction(1), new Fraction(0) }
        });
        int[] pivotRef = { 0, 2, 1 };

        // check values against known references
        FieldMatrix<Fraction> l = lu.getL();
        TestUtils.assertEquals(lRef, l);
        FieldMatrix<Fraction> u = lu.getU();
        TestUtils.assertEquals(uRef, u);
        FieldMatrix<Fraction> p = lu.getP();
        TestUtils.assertEquals(pRef, p);
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
